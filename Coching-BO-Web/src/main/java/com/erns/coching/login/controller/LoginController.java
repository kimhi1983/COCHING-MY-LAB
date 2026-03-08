package com.erns.coching.login.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erns.coching.common.Const;
import com.erns.coching.common.aop.ApiLogging;
import com.erns.coching.common.exception.JweException;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.common.type.UserRole;
import com.erns.coching.login.domain.AclTokenVO;
import com.erns.coching.login.domain.LoginDTO;
import com.erns.coching.login.domain.LoginUserDTO;
import com.erns.coching.login.domain.TokenRefreshDTO;
import com.erns.coching.login.domain.vg.ValidGroupLogin0001;
import com.erns.coching.login.domain.vg.ValidGroupLogin0002;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * <p>사용자 로그인 API Controller</p> 
 *
 * @author Hunwoo Park 
 *
 */
@RestController
@RequestMapping("/api/login")
@Slf4j
public class LoginController extends BaseLoginController {
	
	@PostConstruct
	public void startup()
	{
		log.info("LoginController is started");	
	}
	
	/**
	 * 로그인 처리
	 * @param request
	 * @param response
	 * @param param
	 * @param errors
	 * @return
	 * @throws JOSEException
	 */
	@ApiLogging(logType = UserLogType.LOGIN)
	@PostMapping("/login.api")
    public ApiResult login(
    		HttpServletRequest request, HttpServletResponse response,
    		@RequestBody @Validated(ValidGroupLogin0001.class) LoginDTO param,
    		Errors errors) throws JOSEException {
		
		ApiResult axRet = new ApiResult();  
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}
		
        //super.getLoginedUserObject(request);
		log.debug("Try Login Start... USER ID: {}, IP: {}, AGENT: {}", param.getUserId(), request.getRemoteAddr(), request.getHeader("user-agent"));
		
		LoginUserDTO passCheckUser = loginService.checkPasswd(param.getUserId(), param.getPassword());
        if(null == passCheckUser) {
        	//아이디 없음, 비밀번호 불일치 
        	log.debug("[Login Error] LOGIN_ERR_NOT_FOUND_USER: {}", ApiResultError.LOGIN_ERR_NOT_FOUND_USER.getMessage());
        	return new ApiResult(ApiResultError.LOGIN_ERR_NOT_FOUND_USER);        	
        }
        
        if(passCheckUser.getPwdErrCnt() > Const.ADMIN_PASSWD_ERROR_MAX_COUNT) {
        	//비밀번호 5회 오류
        	log.debug("[Login Error] LOGIN_ERR_PASS_BLOCKED: {}", ApiResultError.LOGIN_ERR_PASS_BLOCKED.getMessage());
        	return new ApiResult(ApiResultError.LOGIN_ERR_PASS_BLOCKED);
        }
        
        //web session 무효화
        invalidateSession(request);
        
        //로그인 처리
        LoginUserDTO loginUser = loginService.loadUser(passCheckUser.getUserId());
        List<String> userRoles = Arrays.asList(UserRole.ADMIN.getValue());
        
        //로그인 & 토큰발급
        Map<String, Object> retLogin = doLogin(loginUser, userRoles, param.isRemainLogin());
        
        return axRet.set(ApiResultError.NO_ERROR).setResultData(retLogin);
    }
	
	

	/**
	 * Access 토큰 재발급
	 * @param param
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiLogging(logType = UserLogType.LOGIN_REFRESH_TOKEN)
	@PostMapping("/refreshToken.api")
	public ApiResult refreshToken(
			HttpServletRequest request, HttpServletResponse response,
    		@RequestBody @Validated(ValidGroupLogin0002.class)TokenRefreshDTO param,
    		Errors errors) {
		
		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);  
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}
        
		Map<String, Object> retData = new HashMap<String,Object>();        
        try {
        	//1. refreshToken 유효성 체크
        	EncryptedJWT rJwe = jwtConfig.decrypt(param.getRefreshToken());
        	EncryptedJWT aJwe = jwtConfig.decrypt(param.getAccessToken());
        	
        	ApiResultError refreshTokenVerify = jwtConfig.verifyIdentityToken(rJwe, true);
        	if (ApiResultError.NO_ERROR != refreshTokenVerify) {
        		log.info("1.1. RefreshToken 오류: {}", refreshTokenVerify);
    			throw new JweException(refreshTokenVerify);
    		}
        	JWTClaimsSet rClaims = rJwe.getJWTClaimsSet();
        	
        	ApiResultError accessTokenVerify = jwtConfig.verifyIdentityToken(aJwe, true);
        	if (!(ApiResultError.NO_ERROR == accessTokenVerify || ApiResultError.ERROR_EXPIRED_JWT == accessTokenVerify)) {
        		log.info("1.2. AccessToken 오류: {}", accessTokenVerify);
    			throw new JweException(accessTokenVerify);
    		}
        	JWTClaimsSet aClaims = aJwe.getJWTClaimsSet();
        	
        	//2. DB에 등록된 refreshToken 인지 확인
        	AclTokenVO dbRt = aclTokenService.getRefresh(param.getRefreshToken());
        	if(dbRt == null) {
        		log.info("2. 미등록 RefreshToken: {}", param.getRefreshToken());
        		return axRet.set(ApiResultError.NO_AUTH_TOKEN_INVALID);
        	}
        	
        	//3.기존 토큰에서 LoginedUserObject 을 얻어온다        	
            LoginUserDTO luObj = null;
        	List<String> userRoles = Arrays.asList(UserRole.ADMIN.getValue());
        	{
        		String userId = (String)rClaims.getSubject(); //userId
        		if(!StringUtils.hasText(userId)) {
        			log.info("3.1. 미등록 사용자: {}", userId);
        			return axRet.set(ApiResultError.NO_AUTH_NOT_FOUND_USER);
        		}
        		
        	    luObj = loginService.loadUser(userId);        	    
        	    if(luObj == null) {
        	    	log.info("3.2. 미등록 사용자 로드 실패: {}", userId);
            		return axRet.set(ApiResultError.NO_AUTH_NOT_FOUND_USER);
            	}
        	}
			
			//4.refreshToken 리턴 
        	retData.put(Const.AUTH_TOKEN_REFRESH, param.getRefreshToken());        	
			if(jwtConfig.isNeedNewRefreshJwToken(rClaims)){
				Boolean remainLogin = rClaims.getBooleanClaim("remainLogin"); //로그인유지
				
				//만약 만료시간 30% 이내이면 다시 발급
				AclTokenVO jwtNewRefreshToken = jwtConfig.createRefreshToken(luObj, userRoles, remainLogin == null ? false : remainLogin);
				
				//기존 token->blacklist, 신규토근 저장
				aclTokenService.updateNewRefreshToken(jwtNewRefreshToken, param.getRefreshToken());				
				retData.put(Const.AUTH_TOKEN_REFRESH, jwtNewRefreshToken.getToken());
			}
			log.info("4. RefreshToken 토큰 반환 : {}", retData.get(Const.AUTH_TOKEN_REFRESH));
			
			//5. Access Token 리턴
			{
				//토근 재발급
				AclTokenVO jwtNewAccessToken = jwtConfig.createAccessToken(luObj, userRoles);
		        log.debug("new make jwtAccessToken:{}", jwtNewAccessToken.getToken());
		        aclTokenService.addAccessToken(jwtNewAccessToken);
		        
		        //기존 access token blacklist 처리 저장
		        AclTokenVO ablModel = genAclTokenModel(param.getAccessToken(), luObj, aClaims.getExpirationTime());
		        aclTokenService.addBlacklistToken(ablModel);
		        
		        retData.put(Const.AUTH_TOKEN_ACCESS, jwtNewAccessToken.getToken());
			}
			log.info("5. AccessToken 토큰 생성 : {}", retData.get(Const.AUTH_TOKEN_ACCESS));
			
	        	        
	        axRet.set(ApiResultError.NO_ERROR).setResultData(retData);
	        
	        
        }catch(JweException eje) {
    		log.warn("[JWT] Token invalide! {} {}", eje.getMessage(), param.getRefreshToken());
			return axRet.set(eje.getCode());
		}catch(Exception e) {			
			log.error("[JWT] Token is not verified! {} {}", e.getMessage(), param.getRefreshToken());
			return axRet.set(ApiResultError.NO_AUTH_REFRESH_TOKEN_INVALID);
		}
		        
        return axRet;
    }
	
	/**
	 * 토큰 로그아웃처리
	 * @param param
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiLogging(logType = UserLogType.LOGOUT)	
	@PostMapping("/logout.api")
	public ApiResult doLogout(
    		@RequestBody Map<String, Object> param,
    		HttpServletRequest request, HttpServletResponse response) {
        
		log.debug("{}:{}", "accessToken", param.get("accessToken")); 
		log.debug("{}:{}", "refreshToken", param.get("refreshToken"));
		String accessToken = (String)param.get("accessToken");
		String refreshToken = (String)param.get("refreshToken");
		
		//web session 무효화
        invalidateSession(request);
        
        //Logout 처리
        addUserLog(UserLogType.LOGOUT, param);
        doLogout(accessToken, refreshToken);        
		
        ApiResult axRet = new ApiResult();        
        axRet.set(ApiResultError.NO_ERROR);
		        
        return axRet;
    }

}
