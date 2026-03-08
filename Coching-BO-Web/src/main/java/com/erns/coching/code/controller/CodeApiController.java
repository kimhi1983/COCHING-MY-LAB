package com.erns.coching.code.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erns.coching.code.domain.AddressSearchDTO;
import com.erns.coching.code.domain.CodeMasterMultipleSetDTO;
import com.erns.coching.code.domain.CodeMasterSearchDTO;
import com.erns.coching.code.domain.CodeMasterSetDTO;
import com.erns.coching.code.domain.CodeMasterVO;
import com.erns.coching.code.domain.CodeMultipleSetDTO;
import com.erns.coching.code.domain.CodeSearchDTO;
import com.erns.coching.code.domain.CodeSetDTO;
import com.erns.coching.code.domain.CodeVO;
import com.erns.coching.code.domain.vg.ValidCode0031;
import com.erns.coching.code.domain.vg.ValidCode0032;
import com.erns.coching.code.domain.vg.ValidGroupAddress0001;
import com.erns.coching.code.domain.vg.ValidGroupCode0001;
import com.erns.coching.code.domain.vg.ValidGroupCode0002;
import com.erns.coching.code.domain.vg.ValidGroupCode0032;
import com.erns.coching.code.service.CodeMasterService;
import com.erns.coching.code.service.CodeService;
import com.erns.coching.common.aop.ApiLogging;
import com.erns.coching.common.controller.AbstractApiController;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.model.PageInfo;
import com.erns.coching.common.service.AddressService;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.SiteType;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.login.domain.LoginUserDTO;
import com.nimbusds.jose.JOSEException;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * <p>
 * 공통코드 API Controller
 * </p>
 *
 * @author Hunwoo Park
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/common/code")
@PreAuthorize("isAuthenticated()")
public class CodeApiController extends AbstractApiController {

	@Autowired
	private CodeMasterService codeMasterService;

	@Autowired
	private CodeService codeService;

	@Autowired
	private AddressService addressService;

	/**
	 * 그룹코드 목록
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.CODE_MST_LIST)
	@RequestMapping("/mst/list.api")
	public ApiResult getCodeMasterList(
			HttpServletRequest request,
			@RequestBody @Validated(ValidGroupCode0001.class) CodeMasterSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}
		// API Call
		PageInfo pi = new PageInfo(0, param);
		int totalItem = codeMasterService.getListCount(param);
		List<Map<String, Object>> list = null;
		if (totalItem <= 0) {
			list = new ArrayList<Map<String, Object>>();
		} else {
			list = codeMasterService.getList(param);
			pi.setCurrentPage(totalItem, param.getPage());
		}

		axRet.setSc(param).setPageInfo(pi).setList(list);

		return axRet.set(ApiResultError.NO_ERROR);
	}
	
	/**
	 * 그룹코드 상태 설정
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.CODE_MST_UDT_STATE)
	@PostMapping("/mst/setState.api")
	public ApiResult setCodeMasterState(
			HttpServletRequest request,
			@RequestBody @Validated(ValidGroupCode0032.class) CodeMasterMultipleSetDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			LoginUserDTO loginUser = getLoginedUserObject();

			List<CodeMasterVO> setList = new ArrayList<>();
			for (CodeMasterSetDTO setDto : param.getList()) {
				if ("".equals(setDto.getGrpCode())){
					continue;
				}
				
				CodeMasterVO codeMaster = CodeMasterVO.builder()
						.grpCode(setDto.getGrpCode())
						.useYn(setDto.getUseYn())
						.chnr(loginUser.getSeq())
						.build();

				setList.add(codeMaster);
			}

			int ret = codeMasterService.updateState(setList);
			axRet.setResultData(ret)
				.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}

	/**
	 * 코드 목록
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.CODE_LIST)
	@RequestMapping("/list.api")
	public ApiResult getCodeList(
			HttpServletRequest request,
			@RequestBody @Validated(ValidGroupCode0002.class) CodeSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		// API Call
		PageInfo pi = new PageInfo(0, param);
		int totalItem = codeService.getListCount(param);
		List<Map<String, Object>> list = null;
		if (totalItem <= 0) {
			list = new ArrayList<Map<String, Object>>();
		} else {
			list = codeService.getList(param);
			pi.setCurrentPage(totalItem, param.getPage());
		}

		axRet.setPageInfo(pi).setSc(param).setList(list);
		return axRet.set(ApiResultError.NO_ERROR);
	}

	/**
	 * Enum 코드 검색
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @param errors
	 * @return
	 * @throws JOSEException
	 */
	@ApiLogging(logType = UserLogType.CODE_ENUM)
	@PostMapping("/enum.api")
	public ApiResult getEnumList(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody @Validated(ValidGroupCode0001.class) CodeSearchDTO param,
			Errors errors) throws JOSEException {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		axRet.setSc(param);
		switch (param.getGrpCode()) {
			case "USER_LOG_TYPE": // 사용자 로그 타입
				axRet.setArray(UserLogType.values());
				break;
			
			case "SITE_TYPE": // 사이트 타입
				axRet.setArray(SiteType.values());
				break;
		}

		return axRet.set(ApiResultError.NO_ERROR);
	}

	/**
	 * 주소
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @param errors
	 * @return
	 * @throws JOSEException
	 */
	@ApiLogging(logType = UserLogType.CODE_ADDRESS)
	@PostMapping("/address/list.api")
	public ApiResult addressList(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody @Validated(ValidGroupAddress0001.class) AddressSearchDTO param,
			Errors errors) throws JOSEException {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			// API Call
			Map<String, Object> res = addressService.getSearchResult(param.getKeyword(), param.getPage(), param.getRowSize());

			@SuppressWarnings("rawtypes")
			Map addressResult = (Map) res.get("results");
			if (addressResult != null) {
				axRet.setResultData(addressResult).setSc(param).set(ApiResultError.NO_ERROR);
			}

		} catch (Exception e) {
			e.printStackTrace();

			axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}
	
	
	/**
	 * 그룹코드 상태 설정
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.CODE_UDT_STATE)
	@PostMapping("/setState.api")
	public ApiResult setCodeState(
			HttpServletRequest request,
			@RequestBody @Validated(ValidCode0032.class) CodeMultipleSetDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			LoginUserDTO loginUser = getLoginedUserObject();

			List<CodeVO> setList = new ArrayList<>();
			for (CodeSetDTO setDto : param.getList()) {
				if ("".equals(setDto.getCode())){
					continue;
				}
				
				CodeVO code = CodeVO.builder()
						.grpCode(setDto.getGrpCode())
						.code(setDto.getCode())
						.etc1(setDto.getEtc1())
						.etc2(setDto.getEtc2())
						.etc3(setDto.getEtc3())
						.etc4(setDto.getEtc4())
						.etc5(setDto.getEtc5())
						.codeDesc(setDto.getCodeDesc())
						.etcCmmt(setDto.getEtcCmmt())
						.remarks(setDto.getRemarks())
						.useYn(setDto.getUseYn())
						.chnr(loginUser.getSeq())
						.build();

				setList.add(code);
			}

			int ret = codeService.updateState(setList);
			axRet.setResultData(ret)
				.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}
	
	/**
	 * 코드 순서 설정
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.CODE_UDT_ORDER)
	@PostMapping("/setOrders.api")
	public ApiResult setCodeOrders(
			HttpServletRequest request,
			@RequestBody @Validated(ValidCode0031.class) CodeMultipleSetDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			LoginUserDTO loginUser = getLoginedUserObject();

			List<CodeVO> setList = new ArrayList<>();
			for (CodeSetDTO setDto : param.getList()) {
				if ("".equals(setDto.getCode())){
					continue;
				}

				CodeVO code = CodeVO.builder()
						.grpCode(setDto.getGrpCode())
						.code(setDto.getCode())
						.sortOrd(setDto.getSortOrd())
						.chnr(loginUser.getSeq())
						.build();

				setList.add(code);
			}

			int ret = codeService.updateOrder(setList);
			axRet.setResultData(ret)
				.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}
}
