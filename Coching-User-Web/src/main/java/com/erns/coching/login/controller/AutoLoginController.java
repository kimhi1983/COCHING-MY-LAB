package com.erns.coching.login.controller;

import com.erns.coching.common.exception.JweException;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserRole;
import com.erns.coching.login.domain.AclTokenVO;
import com.erns.coching.login.domain.LoginUserDTO;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
*
* <p>로그인 API Controller</p>
*
* @author Hunwoo Park
*
*/
@Slf4j
@Controller
@RequestMapping("/common/user")
public class AutoLoginController extends BaseLoginController {

	private static final String REDIRECT_USER_AUTO_LOGIN_URL = "redirect:/coching-user/autoLogin";

	@PostConstruct
	public void startup()
	{
		log.info("LoginController is started");
	}

	/**
	 * 로그인
	 * @param request
	 * @param response
	 * @param redirect
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/login")
	public String login(HttpServletRequest request, HttpServletResponse response,
						RedirectAttributes redirect) throws UnsupportedEncodingException {

		String paramRefreshToken = request.getParameter("refreshToken");
		try {
			//1. refreshToken 유효성 체크
			EncryptedJWT rJwe = jwtConfig.decrypt(paramRefreshToken);
			ApiResultError refreshTokenVerify = jwtConfig.verifyIdentityToken(rJwe, true);
			if (ApiResultError.NO_ERROR != refreshTokenVerify) {
				log.info("1.1. RefreshToken 오류: {}", refreshTokenVerify);
				throw new JweException(refreshTokenVerify);
			}
			JWTClaimsSet rClaims = rJwe.getJWTClaimsSet();

			//2. DB에 등록된 refreshToken 인지 확인
			AclTokenVO dbRt = aclTokenService.getRefresh(paramRefreshToken);
			if(dbRt == null) {
				log.info("2. 미등록 RefreshToken: {}", paramRefreshToken);
				return REDIRECT_USER_AUTO_LOGIN_URL;
			}

			//3.기존 토큰에서 LoginedUserObject 을 얻어온다
			LoginUserDTO luObj = null;
			List<String> partnerRoles = Arrays.asList(UserRole.COCHING_PARTNER.getValue());
			{
				String id = (String)rClaims.getSubject(); //email
				if(!StringUtils.hasText(id)) {
					log.info("3.1. 미등록 사용자: {}", id);
					return REDIRECT_USER_AUTO_LOGIN_URL;
				}

				luObj = loginService.loadUser(id);
				if(luObj == null) {
					log.info("3.2. 미등록 사용자 로드 실패: {}", id);
					return REDIRECT_USER_AUTO_LOGIN_URL;
				}

				if(0 == luObj.getPtnSeq()){
					log.info("3.3. 일반 사용자 로드 실패: {}", id);
					return REDIRECT_USER_AUTO_LOGIN_URL;
				}
			}

			//4.refreshToken
			if(jwtConfig.isNeedNewRefreshJwToken(rClaims)){

				AclTokenVO jwtNewRefreshToken = jwtConfig.createRefreshToken(luObj, partnerRoles, false);

				log.info("4. RefreshToken 토큰 생성 : {}", jwtNewRefreshToken.getToken());
			}

			//5. Access Token
			{
				//토근 재발급
				AclTokenVO jwtNewAccessToken = jwtConfig.createAccessToken(luObj, partnerRoles);
				log.debug("new make jwtAccessToken:{}", jwtNewAccessToken.getToken());
				aclTokenService.addAccessToken(jwtNewAccessToken);

				log.info("5. AccessToken 토큰 생성 : {}", jwtNewAccessToken.getToken());
			}

			//web session 무효화
			invalidateSession(request);

			//로그인 처리
			List<String> userRoles = Arrays.asList(UserRole.COCHING_USER.getValue());

			//로그인 & 토큰발급
			//리턴 값에 비밀번호 재설정여부, 마지막 비밀번호 변경일자등이 같이 리턴됨.
			Map<String, Object> retLogin = doLogin(luObj, userRoles, false);

			String autoLoginRet = mapper.writeValueAsString(retLogin);
			log.debug("autoLoginRet:{}",autoLoginRet);

			redirect.addFlashAttribute("autoAction", "login");
			redirect.addFlashAttribute("autoLoginRet", autoLoginRet);

			return REDIRECT_USER_AUTO_LOGIN_URL;
		}catch(JweException eje) {
			log.warn("[JWT] Token invalide! {} {}", eje.getMessage(), paramRefreshToken);

			redirect.addFlashAttribute("autoAction", "error");
			redirect.addFlashAttribute("autoMessage", "로그인 중 오류가 발생하였습니다.");
			return REDIRECT_USER_AUTO_LOGIN_URL;
		}catch(Exception e) {
			log.error("[JWT] Token is not verified! {} {}", e.getMessage(), paramRefreshToken);
			redirect.addFlashAttribute("autoAction", "error");
			redirect.addFlashAttribute("autoMessage", "로그인 중 오류가 발생하였습니다.");
			return REDIRECT_USER_AUTO_LOGIN_URL;
		}
	}
}
