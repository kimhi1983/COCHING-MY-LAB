package com.erns.coching.common.asc;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.erns.coching.common.Const;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * <p>View Interceptor</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Slf4j
public class ViewInterceptor implements HandlerInterceptor {
	
	//오류시 리턴 path
	public static final String REDIRECT_PATH_MB_ERROR = "/view/error.do";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		log.debug("===============VIEWI BEGIN ====================");
		log.debug("Request URI ===> " + request.getRequestURI());
		
		// HttpSession session = request.getSession();
		// HdcUserInfo hui = (HdcUserInfo)session.getAttribute(Const.KEY_HDC_USER_INFO);
		// if(hui == null) {
		// 	//로그인 정보 누락
		// 	logger.debug("== error:0401");
		// 	redirectError(request, response, "0401");
		// 	return false;
		// }
		
		// //Scrap API로 부터 사업자정보 조회
		// CmBizInfoDamoDto bizInfo = hui.getBizInfo();
		// if(bizInfo == null || !StringUtils.hasText(bizInfo.getClientNo())) {
		// 	//가입정보 없음
		// 	logger.debug("== error:0411");
		// 	redirectError(request, response, "0411");
		// 	return false;
		// }
		
		return true;
	}
	
	/**
	 * 에러발생시 redirect 처리
	 * @param request
	 * @param response
	 * @param code
	 * @throws IOException
	 */
	private void redirectError(HttpServletRequest request, HttpServletResponse response, String code) throws IOException {
		FlashMapManager flashMapManager = RequestContextUtils.getFlashMapManager(request);
		if(flashMapManager == null) {
			response.sendRedirect(REDIRECT_PATH_MB_ERROR);
			return;
		}
		
		//파라미터를 1회성으로 전달한다.
		FlashMap flashMap = new FlashMap();
		flashMap.put(Const.MB_ERROR_CODE, code);
		flashMapManager.saveOutputFlashMap(flashMap, request, response);
		
		response.sendRedirect(REDIRECT_PATH_MB_ERROR);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable ModelAndView modelAndView) throws Exception {
		
		log.debug("===============VIEWI END ======================");		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object,
			@Nullable Exception arg3) throws Exception {
	}
}
