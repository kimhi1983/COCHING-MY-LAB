package com.erns.coching.log.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.erns.coching.common.log.domain.IApiProcLogVO;
import com.erns.coching.common.log.service.ApiLogService;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.SiteType;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.config.JwtConfig;
import com.erns.coching.log.domain.ApiLogSearchDTO;
import com.erns.coching.log.domain.ApiLogVO;
import com.erns.coching.log.mapper.ApiLogDAO;
import com.erns.coching.log.service.ApiLogManageService;
import com.erns.coching.login.domain.LoginUserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * <p>
 * 사용자 로그 Service
 * </p>
 *
 * @author Hunwoo Park
 *
 */
@Service
@Transactional
@Slf4j
public class ApiLogServiceImpl implements ApiLogManageService, ApiLogService {

	protected ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private ApiLogDAO dao;

	@Autowired
	private JwtConfig jwtConfig;

	@Override
	public List<Map<String, Object>> getList(ApiLogSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(ApiLogSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public int addLog(IApiProcLogVO param) {
		return dao.insert((ApiLogVO) param);
	}

	@Override
	public IApiProcLogVO generateReqLog(HttpServletRequest request) {
		ApiLogVO retVal = getBase(request);
		retVal.setProcStatus("0"); // 요청

		return retVal;
	}

	@Override
	public IApiProcLogVO generateResLog(HttpServletRequest request, ApiResult apiRet) {
		ApiLogVO retVal = getBase(request);

		boolean isSuccess = ApiResultError.isOk(apiRet.getResultCode());

		// 요청:0, 응답:1, 에러:E
		retVal.setProcStatus(isSuccess ? "1" : "E");
		return retVal;
	}

	@Override
	public IApiProcLogVO generateErrorLog(HttpServletRequest request, Exception e) {
		ApiLogVO retVal = getBase(request);

		// 요청:0, 응답:1, 에러:E
		retVal.setProcStatus("E");

		Map<String, Object> errorResData = new HashMap<>();
		errorResData.put("exception", e.getClass().toString());
		errorResData.put("message", e.getMessage());
		retVal.setResData(errorResData);

		return retVal;
	}

	@Override
	public void setRequestInfo(HttpServletRequest request, IApiProcLogVO logVo) {
		ApiLogVO log = (ApiLogVO) logVo;
		String uri = log.getApiUri();

		Map<String, Object> paramMap = log.getParamMap();
		if (null == paramMap) {
			paramMap = new HashMap<>();
		}

		String routerName = (String) paramMap.get("routerName");
		if (StringUtils.hasText(routerName)) {
			log.setRouterNm(routerName);
		}

		if (uri.startsWith("/api/login/login.api")) {
			//로그인 로그
			log.setLogType(UserLogType.LOGIN.getCode());
			log.setMembSeq(0);			
			//log.setApiParams("{}"); // 로그인 파라미터 숨기기

		} else if (uri.startsWith("/api/login/logout.api")) {
			//로그아웃 로그
			log.setLogType(UserLogType.LOGOUT.getCode());
			if (log.getMembSeq() <= 0 ) {
				// 토큰만료등으로 사용자 정보 없는경우
				log.setMembSeq(0); // 기본 [unknown] 사용자 정보

				LoginUserDTO logoutUser = getUserInfoFromJWTToken((String) paramMap.get("accessToken"));
				if (null != logoutUser) {
					log.setMembSeq(logoutUser.getSeq());
				}
			}
		}

		// //api paramMap 정보등 필요시 추가
		// Map<String, Object> paramData = new HashMap<>();
	}

	/**
	 * 최신로그를 저장
	 */
	@Override
	public void addLastLog(IApiProcLogVO logVo) {
		// TODO : implements this method

		// UserLogVO log = (UserLogVO)logVo;

		// UserLogLastHisVO lLogVo = UserLogLastHisVO.FromUserLogVOBuilder()
		// .userLogVo(log)
		// .build();

		// userlogLastHisService.merge(lLogVo);
	}

	private ApiLogVO getBase(HttpServletRequest request) {
		String uri = request.getRequestURI();

		// 로그인 정보 추출
		LoginUserDTO loginInfo = getLoginedUserObject();
		long membSeq = loginInfo == null ? 0 : loginInfo.getSeq();

		// // 요청 파라미터로 query string 생성
		// Map<String, String[]> paramMap = request.getParameterMap();

		// StringBuffer sb = new StringBuffer();
		// StringBuffer qnSb = new StringBuffer();
		// sb.append(uri + "?");
		// for (String key : paramMap.keySet()) {
		// 	String[] vals = paramMap.get(key);
		// 	String val = vals == null ? "" : String.join(",", vals);

		// 	if (!StringUtils.hasText(val)) {
		// 		qnSb.append(key).append("=").append(val).append("&");
		// 		continue;
		// 	}

		// 	sb.append(key).append("=").append(val).append("&");
		// }

		// sb.append(qnSb); // value 없는 부분 맨 뒤로
		// sb.delete(sb.length() - 1, sb.length()); // & 제거
		// String qStr = sb.toString();
		// qStr = qStr.replaceAll("\\s", ""); // 화이트 스페이스 제거
		// if (qStr.length() > 500) {
		// 	qStr = qStr.substring(0, 500); // 최대 500자
		// }
		// log.debug("qStr: {}", qStr);

		// LogVo 생성 후 리턴
		ApiLogVO baseVo = ApiLogVO.builder()
				.siteType(SiteType.PARTNER_WEB.getCode())
				.membSeq(membSeq)
				// .logType(logType) //나중에 설정
				// .routerNm(routerNm) //나중에 설정
				.accessIp(request.getRemoteAddr())
				// .procStatus(procStatus) //나중에 설정
				.apiUri(uri)
				// .apiParams(apiParams)
				// .apiRes(apiRes)
				.build();

		return baseVo;
	}

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
	 * JWT Token 에서 사용자 정보 강제 추출
	 * 
	 * @param jwtToken
	 * @return
	 */
	private LoginUserDTO getUserInfoFromJWTToken(String jwtToken) {
		if (!StringUtils.hasText(jwtToken)) {
			return null;
		}

		try {
			EncryptedJWT aJwe = jwtConfig.decrypt(jwtToken);
			JWTClaimsSet aClaims = aJwe.getJWTClaimsSet();
			LoginUserDTO userInfo = LoginUserDTO.FromJweBuilder()
					.jweClaims(aClaims.getClaims())
					.build();

			return userInfo;
		} catch (Exception e) {
			// ignore
			log.warn("Cannot Parse JWT Token");
		}

		return null;
	}
}
