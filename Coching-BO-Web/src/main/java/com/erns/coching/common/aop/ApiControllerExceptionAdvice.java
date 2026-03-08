package com.erns.coching.common.aop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonParseException;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.type.ApiResultError;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * <p>API Exception Advice</p> 
 *
 * @author Hunwoo Park 
 *
 */
@RestControllerAdvice
@Slf4j
public class ApiControllerExceptionAdvice {
		
//	@Resource
//	protected EgovPropertyService propertyService;
//	
//	@Resource(name="errorMailService")
//    private ErrorMailService errorMailService;
	
	@ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
	public ModelAndView handleNotSupportedException(HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception {
		ModelAndView model = new ModelAndView("jsonView");
		
		model.addAllObjects(new ApiResult(ApiResultError.ERROR_NOT_SUPPORTED_METHOD));
		return model;		
	}
	
	/**
	 * JWE 토큰 만료
	 * @param request
	 * @param response
	 * @param e
	 * @return
	 * @throws Exception
	 */
	@ExceptionHandler(value = {ExpiredJwtException.class})
	public ModelAndView handleExpiredJwtException(HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception {
		ModelAndView model = new ModelAndView("jsonView");
		
		response.setStatus(HttpStatus.UNAUTHORIZED.value());		
		model.addAllObjects(new ApiResult(ApiResultError.NO_AUTH_TOKEN_EXPIRED));
		return model;
	}
	
	/**
	 * ROLE 권한 없음
	 * @param request
	 * @param response
	 * @param e
	 * @return
	 * @throws Exception
	 */
	@ExceptionHandler(value = {AccessDeniedException.class})
	public ModelAndView handleAccessDeniedException(HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception {
		ModelAndView model = new ModelAndView("jsonView");
		
		model.addAllObjects(new ApiResult(ApiResultError.ERROR_ACCESS_DENIED));
		return model;		
	}
	
	@ExceptionHandler(value = {JsonParseException.class, HttpMessageNotReadableException.class, Exception.class})
	public ModelAndView handleException(HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception {
		ModelAndView model = new ModelAndView("jsonView");
		
		log.info("[Error] API Exception !!" );
		//Don't send mail!
		if(e instanceof HttpMessageNotReadableException) {
			log.warn("Exception 400 : {} : {}", e.getClass(), e.getMessage());			
			model.addAllObjects(new ApiResult(ApiResultError.BAD_REQUEST));
			return model;
		}
		
		model.addAllObjects(new ApiResult(ApiResultError.ERROR_SERVER_ERROR));
		
		e.printStackTrace();		
		//Don't send mail!
		if(e.getClass().getName().contains("ClientAbortException")) {
			log.warn("Exception 500 : {} : {}", e.getClass(), e.getMessage());
			return model;
		}
		
//		String sendErrorMail = propertyService.getString("server.error.send.mail");
//		
//		if ("true".equals(sendErrorMail)) {			
//			errorMailService.sendErrorMail(e, request);
//		}else {			
//			logger.info("[Error Mail] Don't send error mail." );
//			logger.error("error:", e);			
//		}
		
		return model;
	}
	
	

}
