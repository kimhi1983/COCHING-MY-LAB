package com.erns.coching.partner.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erns.coching.common.aop.ApiLogging;
import com.erns.coching.common.controller.AbstractApiController;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.login.domain.LoginUserDTO;
import com.erns.coching.partner.domain.PartnerSearchDTO;
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
@PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_COCHING_PARTNER')")
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
	public ApiResult getPtnInfo(
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
}
