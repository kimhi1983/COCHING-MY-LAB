package com.erns.coching.sysProp.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erns.coching.common.aop.ApiLogging;
import com.erns.coching.common.controller.AbstractApiController;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.model.PageInfo;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.login.domain.LoginUserDTO;
import com.erns.coching.sysProp.domain.SysPropMultipleSetDTO;
import com.erns.coching.sysProp.domain.SysPropSearchDTO;
import com.erns.coching.sysProp.domain.SysPropSetDTO;
import com.erns.coching.sysProp.domain.SysPropVO;
import com.erns.coching.sysProp.domain.vg.ValidSysProp0021;
import com.erns.coching.sysProp.domain.vg.ValidSysProp0040;
import com.erns.coching.sysProp.service.SysPropService;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>시스템설정 SysPropApiController</p>
 *
 * @author jsjeong@erns.co.kr
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/sysProp")
@PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_ADMIN')")
public class SysPropApiController extends AbstractApiController {

	@Autowired
	private SysPropService sysPropService;

	/**
	 * 시스템설정 목록
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.PROP_LIST)
	@RequestMapping("/list.api")
	public ApiResult getSysPropList(
			HttpServletRequest request,
			@RequestBody SysPropSearchDTO param,
			Errors errors) {
		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) {// 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		// API Call
		PageInfo pi = new PageInfo(0, param);
		int totalItem = sysPropService.getListCount(param);
		List<Map<String, Object>> list = null;
		if (totalItem <= 0) {
			list = new ArrayList<>();
		} else {
			list = sysPropService.getList(param);			
		}
		pi.setCurrentPage(totalItem, param.getPage());

		axRet.setSc(param)
			.setPageInfo(pi)
			.setList(list)
			.set(ApiResultError.NO_ERROR);

		return axRet;
	}

	/**
	 * 시스템 설정 로드
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.PROP_GET)
	@RequestMapping("/get.api")
	public ApiResult getSysProp(
			HttpServletRequest request,
			@RequestBody SysPropSearchDTO param,
			Errors errors) {

		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) {// 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			// API Call
			Map<String, Object> retProp = sysPropService.load(param);
			axRet.setResultData(retProp)
				.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			e.printStackTrace();
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}

	/**
	 * 시스템설정 등록/설정
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.PROP_ADD)
	@RequestMapping("/set.api")
	public ApiResult setSysProp(
			HttpServletRequest request,
			@RequestBody @Validated(ValidSysProp0021.class) SysPropSetDTO param,
			Errors errors) {

		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) {// 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			// API Call
			LoginUserDTO loginUser = getLoginedUserObject();

			SysPropVO setProp = SysPropVO.setSysPropBuilder()
					.updateUser(loginUser)
					.fromDto(param)
					.build();

			sysPropService.setSysProp(setProp);

			SysPropSearchDTO searchDTO = new SysPropSearchDTO();
			Map<String, Object> retProp = sysPropService.load(searchDTO);

			axRet.setResultData(retProp)
				.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			e.printStackTrace();
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}

	/**
	 * 시스템설정 등록/설정
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.PROP_UDT)
	@RequestMapping("/sets.api")
	public ApiResult multipleSetSysProp(
			HttpServletRequest request,
			@RequestBody @Validated(ValidSysProp0021.class) SysPropMultipleSetDTO param,
			Errors errors) {

		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) {// 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			// API Call
			LoginUserDTO loginUser = getLoginedUserObject();

			List<SysPropVO> setList = new ArrayList<>();
			for(SysPropSetDTO setDto : param.getList()){
				SysPropVO setProp = SysPropVO.setSysPropBuilder()
					.updateUser(loginUser)
					.fromDto(setDto)
					.build();

				setList.add(setProp);
			}
			int ret = sysPropService.setSysProp(setList);			
			axRet.setResultData(ret)
				.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			e.printStackTrace();
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}

	/**
	 * 시스템설정 삭제
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.PROP_DEL)
	@RequestMapping("/del.api")
	public ApiResult deleteSysProp(
			HttpServletRequest request,
			@RequestBody @Validated(ValidSysProp0040.class) SysPropSetDTO param,
			Errors errors) {

		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) {// 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			SysPropVO delVo = SysPropVO.builder()
					.sysKey(param.getSysKey())
					.build();
			int ret = sysPropService.delete(delVo);			
			axRet.setResultData(ret)
				.set(ApiResultError.NO_ERROR);
			
		} catch (Exception e) {
			e.printStackTrace();
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}

	/**
	 * 시스템설정 삭제
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.PROP_DEL)
	@RequestMapping("/dels.api")
	public ApiResult deleteMultipleSysProp(
			HttpServletRequest request,
			@RequestBody @Validated(ValidSysProp0040.class) SysPropMultipleSetDTO param,
			Errors errors) {

		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) {// 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			List<SysPropVO> delList = new ArrayList<>();
			for(SysPropSetDTO setDto : param.getList()){
				SysPropVO delVo = SysPropVO.builder()
					.sysKey(setDto.getSysKey())
					.build();

				delList.add(delVo);
			}
			int ret = sysPropService.delete(delList);
			axRet.setResultData(ret)
				.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			e.printStackTrace();
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}
	

}
