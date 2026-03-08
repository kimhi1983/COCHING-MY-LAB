package com.erns.coching.common.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;

import com.erns.coching.common.log.service.ApiLogService;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.LocaleType;
import com.erns.coching.common.type.SiteType;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.log.domain.ApiLogVO;
import com.erns.coching.login.domain.LoginUserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * <p>
 * API 컨트롤러 추상클래스<br/>
 * API 컨트롤러에서 사용할 기본 기능 구현
 * </p>
 *
 * @author Hunwoo Park
 *
 */
@Slf4j
public abstract class AbstractApiController extends AbstractController {

	protected ObjectMapper mapper = new ObjectMapper();

	@Autowired
	protected ApiLogService apiProcLogService;

	/**
	 * 로그인 정보를 얻는다.
	 * 
	 * @return
	 */
	protected LoginUserDTO getLoginedUserObject() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null) {
			return null;
		}

		Object userInfo = authentication.getPrincipal();
		if (userInfo instanceof LoginUserDTO) {
			return (LoginUserDTO) userInfo;
		}
		return null;
	}

	/**
	 * <p>
	 * [공통] Parameter Bind error 처리
	 * {@link AbstractApiController#bindError(Errors, ApiResult)} 을 베이스로 함
	 * </p>
	 * 
	 * @param errors
	 * @return
	 */
	protected final ApiResult bindError(Errors errors) {
		return bindError(errors, null);
	}

	/**
	 * <p>
	 * [공통] Parameter Bind error 처리
	 * 파라미터 bind 오류내역 리턴
	 * </p>
	 * 
	 * @param errors
	 * @param orgAxRet ApiResult이 null이 아니면 넘겨준 ApiResult에 설정
	 * @return
	 */
	protected final ApiResult bindError(Errors errors, ApiResult orgAxRet) {
		ApiResult axRet = orgAxRet == null ? new ApiResult() : orgAxRet;
		log.debug("errors:{}", errors);
		String dfErrMsg = errors.getAllErrors().get(0).getDefaultMessage();

		return axRet.set(ApiResultError.ERROR_PARAMETERS, dfErrMsg, getLocale());
	}

	/**
	 * <p>
	 * [공통] Parameter Bind error 처리
	 * 파라미터 bind 오류내역 리턴
	 * </p>
	 * 
	 * @param errors
	 * @param orgAxRet ApiResult이 null이 아니면 넘겨준 ApiResult에 설정
	 * @return
	 */
	protected final ApiResult bindApiError(ConstraintViolationException e, ApiResult orgAxRet) {
		ApiResult axRet = orgAxRet == null ? new ApiResult() : orgAxRet;
		log.debug("errors:{}", e.getMessage());
		String dfErrMsg = e.getConstraintViolations().iterator().next().getMessage();

		return axRet.set(ApiResultError.ERROR_INTERNAL_API_PARAMETERS, dfErrMsg, getLocale());
	}

	protected void sendBindError(HttpServletResponse res, Errors errors, ApiResult orgAxRet) {
		ApiResult axRet = bindError(errors, orgAxRet);
		sendError(res, axRet);
	}

	protected void sendError(HttpServletResponse res, ApiResult orgAxRet) {
		try {
			res.setContentType(MediaType.APPLICATION_JSON_VALUE);
			res.setCharacterEncoding("UTF-8");

			String jsonResponse = mapper.writeValueAsString(orgAxRet);
			res.getWriter().write(jsonResponse);

		} catch (IOException e) {
			log.error("Error writing response", e);
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 사용자 로드 기록
	 * 
	 * @param type
	 * @param data1
	 * @return
	 */
	protected final ApiLogVO addUserLog(UserLogType type, Object data1) {
		LoginUserDTO luo = getLoginedUserObject();
		return addUserLog(luo, type, data1);
	}

	/**
	 * 사용자 로드 기록
	 * 
	 * @param luo   로그인 정보(수동)
	 * @param type
	 * @param data1
	 * @param data2
	 * @return
	 */
	protected final ApiLogVO addUserLog(LoginUserDTO luo, UserLogType type, Object data1) {
		if (type == null) {
			return null;
		}

		if (luo == null) {
			return null;
		}

		String ip = null;
		HttpServletRequest req = getRequest();
		if (req != null) {
			ip = req.getRemoteAddr();
		}

		try {
			String json1 = "{}";
			if (data1 != null) {
				json1 = mapper.writeValueAsString(data1);
			}

			ApiLogVO ulm = ApiLogVO.builder()
					.membSeq(luo.getSeq())
					.logType(type.getCode())
					.siteType(SiteType.USER_WEB.getCode())
					.accessIp(ip)
					.apiParams(json1)
					.apiRes(null)
					.build();

			apiProcLogService.addLog(ulm);
			return ulm;

		} catch (Exception e) {
			log.error("AddLogError", e);
			return null;
		}

	}

	/**
	 * 다국어 locale 가져오기
	 * 
	 * @return
	 */
	protected final LocaleType getLocale() {
		HttpServletRequest request = getRequest();
		String locale = request.getHeader("COCHING-LOCALE");
		LocaleType localeType = LocaleType.findByCode(locale);

		return localeType;
	}
}
