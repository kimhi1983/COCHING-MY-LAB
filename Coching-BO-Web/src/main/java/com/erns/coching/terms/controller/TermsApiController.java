package com.erns.coching.terms.controller;

import java.util.ArrayList;
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
import com.erns.coching.login.domain.LoginUserDTO;
import com.erns.coching.terms.domain.TermsSearchDTO;
import com.erns.coching.terms.domain.TermsSetDTO;
import com.erns.coching.terms.domain.TermsVO;
import com.erns.coching.terms.domain.vg.ValidTerms0001;
import com.erns.coching.terms.domain.vg.ValidTerms0002;
import com.erns.coching.terms.domain.vg.ValidTerms0003;
import com.erns.coching.terms.service.TermsService;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * <p>약관정보 관리 API Controller</p>
 *
 * @author Hunwoo Park
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/terms")
@PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_ADMIN')")
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
            @RequestBody @Validated(ValidTerms0003.class) TermsSearchDTO param,
            Errors errors) {

        ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
        if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

        //API Call
        PageInfo pi = new PageInfo(0, param);
        int totalItem = termsService.getListCount(param);
        List<Map<String, Object>> list = null;
        if(totalItem <= 0) {
    		list = new ArrayList<Map<String, Object>>();
    	}else {
    		list = termsService.getList(param);
    		pi.setCurrentPage(totalItem, param.getPage());
    	}

		axRet.setPageInfo(pi).setSc(param).setList(list);
    	return axRet.set(ApiResultError.NO_ERROR);
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
            @RequestBody @Validated() TermsSearchDTO param,
            Errors errors) {
        ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
        if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		Map<String, Object> terms = termsService.load(param);
		// 상세
		List<Map<String, Object>> versionList = termsService.getVersionList(param);
		terms.put("versionList", versionList);

		// 개수
		int totalCnt = termsService.getListCount(param);
		terms.put("totalCnt", totalCnt);
		axRet.setResultData(terms).set(ApiResultError.NO_ERROR);;

        return axRet;
	}

	/**
	 * 약관 등록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.TERMS_ADD)
	@PostMapping("/new.api")
	public ApiResult insertTerms(
			HttpServletRequest request,
			@RequestBody @Validated({ValidTerms0001.class, ValidTerms0002.class}) TermsSetDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
        if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		LoginUserDTO loginUser = getLoginedUserObject();
		TermsVO newTerms = TermsVO.AddTermsdBuilder()
				.addUser(loginUser)
				.fromDto(param)
				.build();

		if(termsService.insert(newTerms) <= 0) {
			axRet.set(ApiResultError.NO_ERROR);
		}
		axRet.set(ApiResultError.NO_ERROR);


		return axRet;
	}

	/**
	 * 약관 수정
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.TERMS_UDT)
	@PostMapping("/set.api")
	public ApiResult updateTerms(
			HttpServletRequest request,
			@RequestBody @Validated(ValidTerms0002.class) TermsSetDTO param,
			Errors errors) {
		log.info("TermsVO : {}", param);
		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
        if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}
		LoginUserDTO loginUser = getLoginedUserObject();

		TermsVO terms = TermsVO.UpdateTermsdBuilder()
				.updateUser(loginUser)
				.fromDto(param)
				.termsCd(param.getTermsCd())
				.version(param.getVersion())
				.build();

		if(termsService.update(terms) <= 0) {
			axRet.set(ApiResultError.NO_ERROR);
		}
		axRet.set(ApiResultError.NO_ERROR);


		return axRet;
	}

}
