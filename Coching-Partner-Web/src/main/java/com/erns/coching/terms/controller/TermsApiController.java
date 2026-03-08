package com.erns.coching.terms.controller;

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
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.terms.domain.TermsSearchDTO;
import com.erns.coching.terms.domain.vg.ValidTerms0001;
import com.erns.coching.terms.domain.vg.ValidTerms0002;
import com.erns.coching.terms.service.TermsService;

import lombok.extern.slf4j.Slf4j;

/**
*
* <p>Terms API Controller</p>
*
* @author Hunwoo Park
*
*/
@Slf4j
@RestController
@RequestMapping("/api/terms")
@PreAuthorize("permitAll")
public class TermsApiController extends AbstractApiController{

	@Autowired
	private TermsService termsService;


	/**
	 * 약관 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.TERMS_LIST)
	@PostMapping("/list.api")
	public ApiResult getTermsList(
            HttpServletRequest request,
            @RequestBody @Validated(ValidTerms0001.class) TermsSearchDTO param,
            Errors errors) {

        ApiResult axRet = new ApiResult();
        if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

        //API Call
		List<Map<String, Object>> termsList = termsService.getList(param);
		axRet.setSc(param).setList(termsList).set(ApiResultError.NO_ERROR, getLocale());


        return axRet;
	}


	/**
	 * 약관 상세
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.TERMS_GET)
	@PostMapping("/get.api")
	public ApiResult getTerms(
            HttpServletRequest request,
            @RequestBody @Validated(ValidTerms0002.class) TermsSearchDTO param,
            Errors errors) {
        ApiResult axRet = new ApiResult();
        if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		Map<String, Object> terms = termsService.load(param);
		List<Map<String, Object>> versionList = termsService.getVersionList(param);

		terms.put("versionList", versionList);
		axRet.setResultData(terms).set(ApiResultError.NO_ERROR, getLocale());

        return axRet;
	}
}
