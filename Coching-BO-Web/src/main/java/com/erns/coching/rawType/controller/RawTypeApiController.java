package com.erns.coching.rawType.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erns.coching.common.aop.ApiLogging;
import com.erns.coching.common.controller.AbstractApiController;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.model.PageInfo;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.config.JwtConfig;
import com.erns.coching.login.domain.LoginUserDTO;
import com.erns.coching.login.service.LoginService;
import com.erns.coching.member.domain.vg.ValidMember0001;
import com.erns.coching.member.domain.vg.ValidMember0002;
import com.erns.coching.mltln.domain.vg.ValidMltln0002;
import com.erns.coching.mltln.domain.vg.ValidMltln0003;
import com.erns.coching.rawType.domain.RawTypeMultipleSetDTO;
import com.erns.coching.rawType.domain.RawTypeSearchDTO;
import com.erns.coching.rawType.domain.RawTypeSetDTO;
import com.erns.coching.rawType.domain.RawTypeVO;
import com.erns.coching.rawType.domain.vg.ValidRawType0032;
import com.erns.coching.rawType.service.RawTypeService;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * <p>회원정보 API Controller</p>
 *
 * @author Hunwoo Park
 *
 */
@RestController
@RequestMapping("/api/rawType")
@PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
@Slf4j
public class RawTypeApiController extends AbstractApiController {

	@Autowired
	private RawTypeService rawTypeService;

	@Autowired
	protected JwtConfig jwtConfig;

	@Autowired
	protected LoginService loginService;

	/**
	 * 원료 타입 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RAW_TYPE_LIST)
	@PostMapping("/list.api")
	public ApiResult getRawTypeList(
            HttpServletRequest request,
            @RequestBody @Validated(ValidMember0001.class) RawTypeSearchDTO param,
            Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		PageInfo pi = new PageInfo(0, param);
		int totalItem = rawTypeService.getListCount(param);
		pi.setCurrentPage(totalItem, param.getPage());

        List<Map<String, Object>> memberList = rawTypeService.getList(param);
		axRet.setSc(param).setPageInfo(pi).setList(memberList).set(ApiResultError.NO_ERROR);

        return axRet;
	}

	/**
	 * 사용자회원 계정 상세
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RAW_TYPE_GET)
	@PostMapping("/get.api")
	public ApiResult getRawType(
			HttpServletRequest request,
			@RequestBody @Validated(ValidMember0002.class) RawTypeSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		Map<String, Object> member = rawTypeService.load(param);
		axRet.setResultData(member).set(ApiResultError.NO_ERROR);

		return axRet;
	}

	/**
	 * RawType에서 CODE 존재여부 확인
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RAW_TYPE_CK_CODE)
	@PostMapping("/isHaveCode.api")
	public ApiResult isHaveCode(
			HttpServletRequest request,
			@RequestBody RawTypeSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		int isHaveCode = rawTypeService.isHaveCode(param);

		Map<String, Boolean> result = new HashMap<>();
		if(isHaveCode > 0) {
			// 중복 데이터 존재 => 등록 불가
			result.put("isHave", true);
		} else if(isHaveCode == 0) {
			// 등록 가능
			result.put("isHave", false);
		}

		axRet.setResultData(result);

		return axRet.set(ApiResultError.NO_ERROR);
	}


	/**
	 * 성분 구분 타입 생성
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RAW_TYPE_ADD)
	@PostMapping("/new.api")
	public ApiResult insertRawType(
			HttpServletRequest request,
			@RequestBody @Validated(ValidMltln0002.class) RawTypeSetDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		// API Call
		LoginUserDTO loginUser = getLoginedUserObject();
		param.setRgtr(loginUser.getSeq());
		RawTypeVO newType = RawTypeVO.SetTypeBuilder()
				.fromDto(param)
				.build();

		rawTypeService.insert(newType);

		RawTypeSearchDTO scDTO = new RawTypeSearchDTO();
		scDTO.setTypeSeq(newType.getTypeSeq());

		Map<String, Object> retType = rawTypeService.load(scDTO);
		axRet.setResultData(retType).set(ApiResultError.NO_ERROR);

		return axRet;
	}

	/**
	 * MLTLN 수정
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RAW_TYPE_UDT)
	@PostMapping("/set.api")
	public ApiResult updateMltln(
			HttpServletRequest request,
			@RequestBody @Validated(ValidMltln0003.class) RawTypeSetDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		// API Call
		try {
			LoginUserDTO loginUser = getLoginedUserObject();
			param.setChnr(loginUser.getSeq());
			RawTypeVO newType = RawTypeVO.SetTypeBuilder()
					.fromDto(param)
					.build();

			rawTypeService.update(newType);

			RawTypeSearchDTO scDTO = new RawTypeSearchDTO();
			scDTO.setTypeSeq(newType.getTypeSeq());

			Map<String, Object> retType = rawTypeService.load(scDTO);
			axRet.setResultData(retType).set(ApiResultError.NO_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}
	
	/**
	 * 성분 구분 상태 설정
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RAW_TYPE_UDT_STATE)
	@PostMapping("/setState.api")
	public ApiResult setRawTypeState(
			HttpServletRequest request,
			@RequestBody @Validated(ValidRawType0032.class) RawTypeMultipleSetDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			LoginUserDTO loginUser = getLoginedUserObject();

			List<RawTypeVO> setList = new ArrayList<>();
			for (RawTypeSetDTO setDto : param.getList()) {
				if (setDto.getTypeSeq() <= 0){
					continue;
				}
				
				RawTypeVO rawType = RawTypeVO.SetTypeStateBuilder()
						.fromDto(setDto)
						.updateUser(loginUser)
						.build();

				setList.add(rawType);
			}

			int ret = rawTypeService.updateState(setList);
			axRet.setResultData(ret)
				.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}
}
