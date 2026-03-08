package com.erns.coching.login.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.erns.coching.common.Const;
import com.erns.coching.common.controller.AbstractApiController;
import com.erns.coching.common.type.SiteType;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.config.JwtConfig;
import com.erns.coching.login.domain.AclTokenVO;
import com.erns.coching.login.domain.LoginUserDTO;
import com.erns.coching.login.service.AclTokenService;
import com.erns.coching.login.service.LoginService;
import com.erns.coching.member.service.MemberService;
import com.nimbusds.jose.JOSEException;

import lombok.extern.slf4j.Slf4j;

/**
*
* <p>로그인 Base Controller</p>
*
* @author Hunwoo Park
*
*/
@Slf4j
public class BaseLoginController extends AbstractApiController {

	@Autowired
	protected JwtConfig jwtConfig;

	@Autowired
	protected LoginService loginService;

	@Autowired
	protected AclTokenService aclTokenService;

	@Autowired
	protected MemberService memberService;

	protected AclTokenVO genAclTokenModel(String token, LoginUserDTO luo, Date expDate) {
		return genAclTokenModel(token, luo.getSeq(), expDate);
	}

	protected AclTokenVO genAclTokenModel(String token, long userSeq, Date expDate) {
		return AclTokenVO.builder()
				.siteType(SiteType.USER_WEB.getCode())
				.token(token)
				.userSeq(userSeq)
				.expDate(expDate)
				.regDttm(new Date())
				.build();
	}

	protected boolean isBlacklistToken(String token) {
		AclTokenVO ret = aclTokenService.getBlacklist(token);
		return ret != null;
	}


	protected Map<String, Object> getProfile(LoginUserDTO uInfo){
		Map<String, Object> profile = new HashMap<String,Object>();
		profile.put("userSeq", uInfo.getSeq());
		profile.put("ptnSeq", uInfo.getPtnSeq());
		profile.put("id", uInfo.getId());
		profile.put("userType", uInfo.getUserType());
        profile.put("userName", uInfo.getUserName());
        profile.put("email", uInfo.getEmail());
        profile.put("nickname", uInfo.getNickname());
		//개인정보 사항 추가 금지(ex:phone)

        return profile;
	}

	/**
	 * 로그인 처리
	 * @param uInfo
	 * @return
	 * @throws JOSEException
	 */
	public Map<String, Object> doLogin(LoginUserDTO uInfo, List<String> roleList, boolean remainLogin) throws JOSEException {
		AclTokenVO jwtAccessToken = jwtConfig.createAccessToken(uInfo, roleList);
		AclTokenVO jwtRefreshToken = jwtConfig.createRefreshToken(uInfo, roleList, remainLogin);
        log.debug("new make jwtAccessToken:{}", jwtAccessToken.getToken());
        log.debug("new make jwtRefreshToken:{}", jwtRefreshToken.getToken());

        {	//DB refresh token 저장
        	aclTokenService.addRefreshToken(jwtRefreshToken);

        	//DB access token 저장
        	aclTokenService.addAccessToken(jwtAccessToken);
        }

        try {
        	loginService.setLogin(uInfo);
        	addUserLog(uInfo, UserLogType.LOGIN, null);
        }catch (Exception e) {
        	log.error(" Write Log Error! At user login.", e);
			e.printStackTrace();
		}

        Map<String, Object> retLogin = new HashMap<String,Object>();
        retLogin.put(Const.AUTH_TOKEN_ACCESS, jwtAccessToken.getToken());
        retLogin.put(Const.AUTH_TOKEN_REFRESH, jwtRefreshToken.getToken());
        retLogin.put("pwdChangeDate", uInfo.getPwdChangeDate());
        retLogin.put("pwdInitYn", uInfo.getPwdInitYn());
        retLogin.put("userData", getProfile(uInfo));
        retLogin.put("userRoles", roleList);

        return retLogin;
	}


	/**
	 * 로그아웃 처리
	 * @param uInfo
	 * @return
	 */
	public void doLogout(String accessToken, String refreshToken) {

		try {
        	aclTokenService.setBlackList(accessToken, refreshToken);
        }catch (Exception e) {
        	log.error(" Write Log Error! At user logout.", e);
			e.printStackTrace();
		}
	}


	/**
	 * web session 무효화
	 * @param request
	 */
	public void invalidateSession(HttpServletRequest request) {
		//web session 재설정
    	HttpSession session = request.getSession();
        session.invalidate();
        session = request.getSession(true);
	}

}
