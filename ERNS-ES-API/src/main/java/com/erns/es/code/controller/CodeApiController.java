package com.erns.es.code.controller;

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

import com.erns.es.code.domain.AddressSearchDTO;
import com.erns.es.code.domain.CodeSearchDTO;
import com.erns.es.code.domain.vg.ValidGroupAddress0001;
import com.erns.es.code.domain.vg.ValidGroupCode0001;
import com.erns.es.code.domain.vg.ValidGroupCode0002;
import com.erns.es.code.service.CodeMasterService;
import com.erns.es.code.service.CodeService;
import com.erns.es.common.aop.ApiLogging;
import com.erns.es.common.controller.AbstractApiController;
import com.erns.es.common.model.ApiResult;
import com.erns.es.common.model.PageInfo;
import com.erns.es.common.service.AddressService;
import com.erns.es.common.type.ApiResultError;
import com.nimbusds.jose.JOSEException;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * <p>공통코드 API Controller</p>
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
     * @param request
     * @param param
     * @param errors
     * @return
     */
    @RequestMapping("/mst/list.api")
    @ApiLogging
    public ApiResult getCodeMasterList(
    		HttpServletRequest request,
    		@RequestBody @Validated(ValidGroupCode0001.class) CodeSearchDTO param,
    		Errors errors) {

    	ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
    	if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
    		return bindError(errors, axRet);
    	}
		//API Call
    	PageInfo pi = new PageInfo(0, param);
    	int totalItem = codeMasterService.getListCount(param);
		List<Map<String, Object>> list = null;
		if(totalItem <= 0) {
			list = new ArrayList<Map<String, Object>>();
		}else {
			list = codeMasterService.getList(param);
			pi.setCurrentPage(totalItem, param.getPage());
		}

		axRet.setSc(param).setPageInfo(pi).setList(list);

    	return axRet.set(ApiResultError.NO_ERROR);
    }

    /**
     * 코드 목록
     * @param request
     * @param param
     * @param errors
     * @return
     */
    @RequestMapping("/list.api")
    @ApiLogging
    public ApiResult getCodeList(
    		HttpServletRequest request,
    		@RequestBody @Validated(ValidGroupCode0002.class) CodeSearchDTO param,
    		Errors errors) {

    	ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
    	if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
    		return bindError(errors, axRet);
    	}

    	//API Call
    	PageInfo pi = new PageInfo(0, param);
    	int totalItem = codeService.getListCount(param);
    	List<Map<String, Object>> list = null;
    	if(totalItem <= 0) {
    		list = new ArrayList<Map<String, Object>>();
    	}else {
    		list = codeService.getList(param);
    		pi.setCurrentPage(totalItem, param.getPage());
    	}

		axRet.setPageInfo(pi).setSc(param).setList(list);
    	return axRet.set(ApiResultError.NO_ERROR);
    }

    /**
	 * Enum 코드 검색
	 * @param request
	 * @param response
	 * @param param
	 * @param errors
	 * @return
	 * @throws JOSEException
	 */
	@ApiLogging
	@PostMapping("/enum.api")
    public ApiResult getEnumList(
    		HttpServletRequest request, HttpServletResponse response,
    		@RequestBody @Validated(ValidGroupCode0001.class) CodeSearchDTO param,
    		Errors errors) throws JOSEException {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}


		axRet.setSc(param);
		//TODO : 개발필요
//		switch(param.getCodeMst()) {
//
//		}

		return axRet.set(ApiResultError.NO_ERROR);
    }


	/**
	 * 주소
	 * @param request
	 * @param response
	 * @param param
	 * @param errors
	 * @return
	 * @throws JOSEException
	 */
	@ApiLogging
	@PostMapping("/address/list.api")
    public ApiResult addressList (
    		HttpServletRequest request, HttpServletResponse response,
    		@RequestBody @Validated(ValidGroupAddress0001.class) AddressSearchDTO param,
    		Errors errors) throws JOSEException {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}


		try {
			//API Call
			Map<String, Object> res = addressService.getSearchResult(param.getKeyword(), param.getPage(), param.getRowSize());

			@SuppressWarnings("rawtypes")
			Map addressResult = (Map)res.get("results");
			if(addressResult != null) {
				axRet.setResultData(addressResult).setSc(param).set(ApiResultError.NO_ERROR);
			}

		}catch(Exception e) {
			e.printStackTrace();

			axRet.set(ApiResultError.ERROR_DEFAULT);
		}


		return axRet;
    }
}
