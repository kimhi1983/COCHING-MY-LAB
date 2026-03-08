package com.erns.coching.partner.controller;

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
import com.erns.coching.partner.domain.PartnerSearchDTO;
import com.erns.coching.partner.domain.vg.ValidGroupPartner0001;
import com.erns.coching.partner.domain.vg.ValidGroupPartner0002;
import com.erns.coching.partner.service.PartnerService;

import lombok.extern.slf4j.Slf4j;

/**
 * 원료사 정보 API
 * @author dsnam@erns.co.kr
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/partner")
public class PartnerApiController extends AbstractApiController {
	@Autowired
	private PartnerService partnerService;

	/**
	 * <p>원료사 정보 로드</p>
	 * @param request
	 * @param param
	 * @param errors 파라미터 바인딩 오류 내역
	 * @return
	 */
	@ApiLogging(logType = UserLogType.PARTNER_INFO)
	@RequestMapping("/ptnInfo.api")
	@PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_COCHING_USER')")
	public ApiResult getMemInfo(
			HttpServletRequest request,
			@RequestBody PartnerSearchDTO paramDto,
			Errors errors) {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		LoginUserDTO loginInfo = getLoginedUserObject();

		paramDto.setPtnSeq(loginInfo.getPtnSeq());

		Map<String, Object> ptnInfo = partnerService.load(paramDto);
		axRet.setResultData(ptnInfo).set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}
	
    /**
     * 원료사 목록
     * @param request
     * @param param
     * @param errors
     * @return
     */
	@ApiLogging(logType = UserLogType.PARTNER_LIST)
    @RequestMapping("/list.api")    
    public ApiResult getPartnerList(
    		HttpServletRequest request,
    		@RequestBody @Validated(ValidGroupPartner0001.class) PartnerSearchDTO param,
    		Errors errors) {

    	ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
    	if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
    		return bindError(errors, axRet);
    	}
		//API Call
		PageInfo pi = new PageInfo(0, param);
		int totalItem = partnerService.getListCount(param);
		List<Map<String, Object>> list = null;
		if(totalItem <= 0) {
			list = new ArrayList<>();
		}else {
			list = partnerService.getList(param);
		}
        pi.setCurrentPage(totalItem, param.getPage());
		axRet.setSc(param)
			.setPageInfo(pi)
			.setList(list)
			.set(ApiResultError.NO_ERROR, getLocale());

        return axRet;
    }
	
	/**
	 * 원료사 상세
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.PARTNER_GET)
	@PostMapping("/get.api")
	public ApiResult getBoardArticle(
			HttpServletRequest request,
			@RequestBody @Validated(ValidGroupPartner0002.class) PartnerSearchDTO param,
			Errors errors) {
		
		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}
		
		//API Call
		Map<String, Object> data = partnerService.load(param); //게시글 정보
		if(null == data){
			return axRet;
		}

		axRet.setSc(param)
			.setResultData(data)
			.set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

}
