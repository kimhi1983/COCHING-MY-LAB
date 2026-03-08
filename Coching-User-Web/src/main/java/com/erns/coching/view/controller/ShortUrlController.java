package com.erns.coching.view.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.erns.coching.common.Const;
import com.erns.coching.member.service.MemberService;
import com.erns.coching.url.domain.ShortUrlVO;
import com.erns.coching.url.service.ShortUrlService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * <p>Short URL Controller</p>
 *
 * @author Hunwoo Park
 *
 */
@Slf4j
@Controller
@RequestMapping("/common")
public class ShortUrlController {

	//404에러
	@Value("#{'${server.error.path:/coching-user/error-404}'.replaceAll('\\s+', '').split(',')}")
	private String ERROR_PATH;

	private final String ERROR_PATH_400 = "/coching-user/error-400";	//잘못된 접근

	private final String ERROR_PATH_410 = "/coching-user/error-410"; //만료된 페이지

	@Autowired
	private ShortUrlService shortUrlService;

	@Autowired
	private MemberService memberService;

	private ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * 짧은 URL 처리
	 * @param request
	 * @param response
	 * @param redirect
	 * @param urlId
	 * @param tk
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/su/tu/{urlId}")
	public String testSendmailCheck(
			HttpServletRequest request,
			HttpServletResponse response,
			RedirectAttributes redirect,
			@PathVariable("urlId") String urlId,
			@RequestParam(name = "tk", required = false, defaultValue = "") String tk) {

		log.debug("urlId:{}", urlId);
		log.debug("tk:{}", tk);

		try {
			ShortUrlVO suvo = shortUrlService.load(urlId);
			log.debug("suvo:{}", suvo);

			if(null == suvo) {
				return "redirect:"+ERROR_PATH;
			}

			if(StringUtils.hasText(suvo.getToken()) && !suvo.getToken().equals(tk) ) {
				log.warn("Not matched token : {} : {}", suvo.getToken(), tk);
				return "redirect:"+ERROR_PATH_400;
			}

			//URI ID 등을 세션에 기록
			HttpSession session = request.getSession();
			session.setAttribute(Const.SEC_SHORT_URL_ID, urlId);
			if(StringUtils.hasText(tk)) {
				session.setAttribute(Const.SEC_SHORT_URLTOKEN, tk);
			}

			//파라미터 전달 및 페이지 이동
			Map<String, Object> paramMap;
			String paramJson = suvo.getParamJson();
			if(!StringUtils.hasText(suvo.getParamJson())) {
				paramMap = new HashMap<>();
			}else {
				paramMap = objectMapper.readValue(paramJson, Map.class);
			}
			log.debug("paramMap:{}", paramMap);

			//Post 방식
			if("POST".equalsIgnoreCase(suvo.getMethod())) {
				for(String paramName : paramMap.keySet()) {
					String pVal = paramMap.getOrDefault(paramName, "").toString();
					redirect.addFlashAttribute(paramName, pVal);
					//Note : 받는 쪽에서 Model 로 받아야함
				}
				return "redirect:"+suvo.getUrl();
			}

			//Get 방식
			for(String paramName : paramMap.keySet()) {
				String pVal = paramMap.getOrDefault(paramName, "").toString();
				redirect.addAttribute(paramName, pVal);
			}
			return "redirect:"+suvo.getUrl();

		}catch(Exception e) {
			return "redirect:"+ERROR_PATH;
		}
	}

	/**
	 * 사용자 비밀번호 변경
	 * @param request
	 * @param response
	 * @param redirect
	 * @param urlId
	 * @param tk
	 * @return
	 */
	@RequestMapping("/login/resetPasswd.do")
	public String resetUserPasswd(
			HttpServletRequest request,
			HttpServletResponse response,
			RedirectAttributes redirect,
			Model model) {

		//URI ID 등을 세션에 기록
		HttpSession session = request.getSession();
		String urlId = (String)session.getAttribute(Const.SEC_SHORT_URL_ID);
		String token = (String)session.getAttribute(Const.SEC_SHORT_URLTOKEN);

		ShortUrlVO suvo = shortUrlService.load(urlId);
		log.debug("suvo:{}", suvo);

		session.removeAttribute(Const.SEC_SHORT_URL_ID);
		session.removeAttribute(Const.SEC_SHORT_URLTOKEN);

		session.removeAttribute(Const.SEC_RESET_USERPW_MEMBSEQ);

		if(null == suvo) {
			log.warn("wrong access!!");
			return "redirect:"+ERROR_PATH_400;
		}

		//토큰 한번더 검증
		if(!StringUtils.hasText(suvo.getToken()) || !suvo.getToken().equals(token) ) {
			log.warn("Not matched token : {} : {}", suvo.getToken(), token);
			return "redirect:"+ERROR_PATH_400;
		}

		//만료시간 확인
		{
			int expiredTime = +30; //30분간 유효
			Date now = new Date();
			Calendar checkTime = Calendar.getInstance();
			checkTime.setTime(suvo.getRgtDttm());
			checkTime.add(Calendar.MINUTE, expiredTime);

			if(now.after(checkTime.getTime())) {
				log.warn("email expired!");
				return "redirect:"+ERROR_PATH_410;
			}
		}

		//비밀번호 변경페이지로 이동
		long membSeq = Long.parseLong(model.getAttribute("membSeq").toString(), 10);
		log.debug("membSeq:{}", membSeq);

		session.setAttribute(Const.SEC_RESET_USERPW_MEMBSEQ, model.getAttribute("membSeq"));
		shortUrlService.updateHits(suvo.getUrlId());

		redirect.addFlashAttribute("authAction", "userPasswdReset");
		redirect.addFlashAttribute("authData", "{}");

		return "redirect:/coching-user/login";
	}
}
