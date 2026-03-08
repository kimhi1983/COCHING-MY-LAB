package com.erns.coching.member.controller;

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
import com.erns.coching.join.domain.UserJoinDTO;
import com.erns.coching.login.domain.LoginUserDTO;
import com.erns.coching.member.domain.MemberMultipleSetDTO;
import com.erns.coching.member.domain.MemberSearchDTO;
import com.erns.coching.member.domain.MemberSetDTO;
import com.erns.coching.member.domain.MemberVO;
import com.erns.coching.member.domain.vg.ValidAdminMember0001;
import com.erns.coching.member.domain.vg.ValidAdminMember0002;
import com.erns.coching.member.domain.vg.ValidAdminMember0003;
import com.erns.coching.member.domain.vg.ValidAdminMember0004;
import com.erns.coching.member.domain.vg.ValidAdminMember0032;
import com.erns.coching.member.service.MemberService;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * <p>관리자 관리 API Controller</p>
 *
 * @author Hunwoo Park
 *
 */
@RestController
@RequestMapping("/api/member/admin")
@PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
@Slf4j
public class AdminMemberController extends AbstractApiController {

	@Autowired
	private MemberService memberService;

	/**
	 * 관리자 계정 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.ADMIN_MEMBER_LIST)
	@PostMapping("/list.api")
	public ApiResult getAdminUserList(
            HttpServletRequest request,
            @RequestBody MemberSearchDTO param,
            Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		PageInfo pi = new PageInfo(0, param);
		int totalItem = memberService.getListCount(param);
		pi.setCurrentPage(totalItem, param.getPage());

        List<Map<String, Object>> memberList = memberService.getList(param);
		axRet.setSc(param).setPageInfo(pi).setList(memberList).set(ApiResultError.NO_ERROR);

        return axRet;
	}

	/**
	 * 관리자 계정 상세
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.ADMIN_MEMBER_GET)
	@PostMapping("/get.api")
	public ApiResult getAdminUser(
			HttpServletRequest request,
			@RequestBody @Validated(ValidAdminMember0002.class) MemberSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		Map<String, Object> member = memberService.load(param);
		axRet.setResultData(member).set(ApiResultError.NO_ERROR);

		return axRet;
	}

	/**
	 * 관리자 ID 중복확인
	 * @param request
	 * @param paramDto
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.ADMIN_MEMBER_CK_ID)
	@PostMapping("/checkDuplicateId.api")
	public ApiResult checkDuplicateId(
			HttpServletRequest request,
			@RequestBody @Validated(ValidAdminMember0001.class) MemberSearchDTO paramDto,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		MemberVO memberVO = MemberVO
				.MemberDuplicateBuilder()
				.membId(paramDto.getMembId())
				.email(paramDto.getEmail())
				.nickname(paramDto.getNickname())
				.build();
		Map<String, Object> map = memberService.chkDuplicate(memberVO);
		Map<String, Object> data = new HashMap<>();
		if(null != map) {
			data.put("isDuplicate", true);
		}else{
			data.put("isDuplicate", false);
		}

		return axRet.set(ApiResultError.NO_ERROR).setResultData(data);
	}

	/**
	 * 관리자 계정 생성
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.ADMIN_MEMBER_ADD)
	@PostMapping("/set.api")
	public ApiResult insertAdmin(
			HttpServletRequest request,
			@RequestBody @Validated(ValidAdminMember0001.class) UserJoinDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		LoginUserDTO loginUser = getLoginedUserObject();
		param.setRgtr(loginUser.getSeq());
		MemberVO newAdminVO = MemberVO.MemberJoinBuilder()
				.dto(param)
				.build();

		if(memberService.insert(newAdminVO) > 0) {
			MemberSearchDTO loadParam = new MemberSearchDTO();
			loadParam.setMembSeq(newAdminVO.getMembSeq());

			Map<String, Object> member = memberService.load(loadParam);
			axRet.setResultData(member).set(ApiResultError.NO_ERROR);
		}

		return axRet;
	}

	/**
	 * 관리자 계정 수정
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.ADMIN_MEMBER_UDT)
	@PostMapping("/update.api")
	public ApiResult updateAdmin(
			HttpServletRequest request,
			@RequestBody @Validated(ValidAdminMember0003.class) MemberSetDTO setDTO,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		LoginUserDTO loginUser = getLoginedUserObject();
		setDTO.setRgtr(loginUser.getSeq());
		setDTO.setChnr(loginUser.getSeq());

		Map<String, Object> member = memberService.updateMemberWithPswd(setDTO);
		axRet.setResultData(member).set(ApiResultError.NO_ERROR);

		return axRet;
	}

	/**
	 * 관리자 계정 삭제
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.ADMIN_MEMBER_DEL)
	@PostMapping("/delete.api")
	public ApiResult deleteAdmin(
			HttpServletRequest request,
			@RequestBody @Validated(ValidAdminMember0004.class) MemberSetDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		LoginUserDTO loginUser = getLoginedUserObject();

		//TODO
//		AdminMemberVO editAdminVO = AdminMemberVO.builder()
//								  .seq(param.getSeq())
//								  .userId(param.getUserId())
//								  .password(param.getPassword())
//								  .userName(param.getUserName())
//								  .phone(param.getPhone())
//								  .email(param.getEmail())
//								  .chnrSeq(loginUser.getSeq())
//								  .build();


		return axRet;
	}
	
	/**
	 * 관리자 상태 설정
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.ADMIN_MEMBER_UDT_STATE)
	@PostMapping("/setState.api")
	public ApiResult setAdminMasterState(
			HttpServletRequest request,
			@RequestBody @Validated(ValidAdminMember0032.class) MemberMultipleSetDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			LoginUserDTO loginUser = getLoginedUserObject();

			List<MemberVO> setList = new ArrayList<>();
			for (MemberSetDTO setDto : param.getList()) {
				if (setDto.getMembSeq() <= 0){
					continue;
				}
				
				MemberVO member = MemberVO.builder()
						.membSeq(setDto.getMembSeq())
						.useYn(setDto.getUseYn())
						.chnr(loginUser.getSeq())
						.build();

				setList.add(member);
			}

			int ret = memberService.updateState(setList);
			axRet.setResultData(ret)
				.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}
}
