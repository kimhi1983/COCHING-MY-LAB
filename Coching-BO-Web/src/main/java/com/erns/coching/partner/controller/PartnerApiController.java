package com.erns.coching.partner.controller;

import com.erns.coching.common.aop.ApiLogging;
import com.erns.coching.common.controller.AbstractApiController;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.model.PageInfo;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.login.domain.LoginUserDTO;
import com.erns.coching.partner.domain.PartnerSearchDTO;
import com.erns.coching.partner.domain.PartnerSetDTO;
import com.erns.coching.partner.domain.PartnerVO;
import com.erns.coching.partner.domain.vg.ValidGroupPartner0001;
import com.erns.coching.partner.domain.vg.ValidGroupPartner0002;
import com.erns.coching.partner.domain.vg.ValidGroupPartner0003;
import com.erns.coching.partner.domain.vg.ValidGroupPartner0004;
import com.erns.coching.partner.service.PartnerService;
import com.nimbusds.jose.JOSEException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * <p>파트너사 API Controller</p>
 *
 * @author Hunwoo Park
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/partner/")
@PreAuthorize("isAuthenticated()")
public class PartnerApiController extends AbstractApiController {

	@Autowired
    private PartnerService partnerService;

    /**
     * 파트너사 목록
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
			list = new ArrayList<Map<String, Object>>();
		}else {
			list = partnerService.getList(param);
			pi.setCurrentPage(totalItem, param.getPage());
		}

		axRet.setSc(param).setPageInfo(pi).setList(list);

    	return axRet.set(ApiResultError.NO_ERROR);
    }

	/**
	 * 파트너 상세
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.PARTNER_GET)	
	@PostMapping("/load.api")
	public ApiResult getBanner(
			HttpServletRequest request,
			@RequestBody @Validated(ValidGroupPartner0003.class) PartnerSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		Map<String, Object> banner = partnerService.load(param);

		axRet.setResultData(banner).set(ApiResultError.NO_ERROR);

		return axRet;
	}

    /**
     * 계정 목록
     * @param request
     * @param param
     * @param errors
     * @return
     */
		@ApiLogging(logType = UserLogType.MEMBER_PARTNER_USER_LIST)
    @RequestMapping("/memberList.api")    
    public ApiResult getMembList(
    		HttpServletRequest request,
    		@RequestBody @Validated(ValidGroupPartner0002.class) PartnerSearchDTO param,
    		Errors errors) {

    	ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
    	if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
    		return bindError(errors, axRet);
    	}

    	//API Call
		PageInfo pi = new PageInfo(0, param);
    	int totalItem = partnerService.getMembListCount(param);
    	List<Map<String, Object>> list = null;
    	if(totalItem <= 0) {
    		list = new ArrayList<Map<String, Object>>();
    	}else {
    		list = partnerService.getMembList(param);
			pi.setCurrentPage(totalItem, param.getPage());
    	}

		axRet.setSc(param).setPageInfo(pi).setList(list);
    	return axRet.set(ApiResultError.NO_ERROR);
    }

	/**
	 * 원료사 업데이트
	 * @param request
	 * @param partnerSetDto
	 * @param errors
	 * @return
	 * @throws JOSEException
	 */
	@ApiLogging(logType = UserLogType.PARTNER_UDT)
	@PostMapping("/update.api")
	public ApiResult update(
			HttpServletRequest request,
			@RequestBody @Validated(ValidGroupPartner0004.class) PartnerSetDTO partnerSetDto,
			Errors errors) throws JOSEException {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		LoginUserDTO loginInfo = getLoginedUserObject();

		PartnerVO partnerVO = PartnerVO.AddPtnBuilder()
				.fromDto(partnerSetDto)
				.seq(loginInfo.getSeq())
				.build();

		partnerService.update(partnerVO);

		return axRet.set(ApiResultError.NO_ERROR);
	}

	/**
	 * PARTNER NAME 목록(파트너사 등록 시)
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.PARTNER_NAME_LIST)
	@PostMapping("/ptnNmList.api")
	public ApiResult getPtnNmList(
			HttpServletRequest request,
			@RequestBody PartnerSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		int totalItem = partnerService.getPtnNmListCount(param);
		List<Map<String, Object>> list = null;
		if(totalItem <= 0) {
			list = new ArrayList<Map<String, Object>>();
		} else {
			list = partnerService.getPtnNmList(param);
		}

		axRet.setList(list);

		return axRet.set(ApiResultError.NO_ERROR);
	}
}
