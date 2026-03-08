package com.erns.coching.view.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.erns.coching.common.controller.AbstractViewController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/coching-user")
public class UserPcController extends AbstractViewController{

	// URL 패턴별 OG Meta 정보 정의
	private Map<String, Object> DEFAULT_OG_META = new HashMap<>();
	

	private Map<String, Object> ogMetaMap = new HashMap<>();

	@PostConstruct
	public void initOgMetaMap() {
		DEFAULT_OG_META.put("title", "COCHING");
		DEFAULT_OG_META.put("siteName", "COCHING");
		DEFAULT_OG_META.put("description", "COCHING");		
		DEFAULT_OG_META.put("url", "");
		DEFAULT_OG_META.put("type", "website");
		DEFAULT_OG_META.put("keyword", "COCHING");
		

		Map<String, Object> weeklyNewsOgMeta = new HashMap<>();
		weeklyNewsOgMeta.put("title", "COCHING Weekly News");
		weeklyNewsOgMeta.put("siteName", "COCHING");
		weeklyNewsOgMeta.put("description", "코칭 Weekly News - 최신 화장품 업계 소식을 확인하세요");
		weeklyNewsOgMeta.put("url", "/coching-user/board/weeklyNews");
		weeklyNewsOgMeta.put("type", "article");
		weeklyNewsOgMeta.put("keyword", "COCHING");
		
		// URL 패턴과 OG Meta 매핑		
		ogMetaMap.put("/board/weeklyNews", weeklyNewsOgMeta);
		
		log.info("OG Meta Map initialized with {} entries", ogMetaMap.size());
	}

	@RequestMapping(value= {"/**", "/**/**"})
    public String main(ModelMap model, HttpServletRequest req) {
		// log.debug("Coching User App");

		String reqURL = req.getRequestURL().toString();
		String requestURI = req.getRequestURI();
		
		log.debug("REQ_URL:{}", reqURL);
		// log.debug("REQUEST_URI:{}", requestURI);
		
		model.addAttribute("currentURL", reqURL);
		
		// URL에 매칭되는 OG Meta 정보 찾기
		Map<String, Object> ogMeta = new HashMap<>(DEFAULT_OG_META);
		Map<String, Object> targetMeta = findMatchingOgMeta(requestURI);
		if (targetMeta != null) {
			ogMeta.putAll(targetMeta);
		}

		//log.debug("ogMeta:{}", ogMeta);

		// 기본 OG Meta 정보 설정				
		model.addAttribute("ogMeta", ogMeta);
		
		return "app/userPc";
		//return "app/system"; //시스템점검
    }
	
	/**
	 * 요청 URI에 매칭되는 OG Meta 정보를 찾는 메서드
	 */
	private Map<String, Object> findMatchingOgMeta(String requestURI) {
		// 정확히 매칭되는 경우
		if (ogMetaMap.containsKey(requestURI)) {
			return (Map<String, Object>) ogMetaMap.get(requestURI);
		}
		
		// 패턴 매칭 (필요시 확장 가능)
		for (String pattern : ogMetaMap.keySet()) {
			if (requestURI.indexOf(pattern) > 0) {
				return (Map<String, Object>) ogMetaMap.get(pattern);
			}
		}
		
		return null;
	}

}
