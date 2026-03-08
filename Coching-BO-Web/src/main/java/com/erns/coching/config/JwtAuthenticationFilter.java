package com.erns.coching.config;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.erns.coching.common.exception.JweException;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.type.ApiResultError;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * <p>Request JWE 토근 필터</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtConfig jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 헤더에서 JWT 를 받아옵니다.
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);
        // 유효한 토큰인지 확인합니다.
        
        if(token != null) {
        	
        	try {
    			if(jwtTokenProvider.validateToken(token)) {
    				// 토큰이 유효하면 토큰으로부터 유저 정보를 받아옵니다.
    	            Authentication authentication = jwtTokenProvider.getAuthentication(token);
    	            // SecurityContext 에 Authentication 객체를 저장합니다.
    	            SecurityContextHolder.getContext().setAuthentication(authentication);
    			}    			
    		}catch(JweException jwee) {
    			ApiResultError err = jwee.getCode();
    			if(ApiResultError.ERROR_EXPIRED_JWT == err) {
    				log.warn("[JWT] Token expired! {} {}", jwee.getMessage(), token);
        			tokenExpired((HttpServletRequest)request, (HttpServletResponse)response, token, jwee.getCode());
    			}
    			return;
    		}catch(ParseException pe) {
    			//ignore
    		}
        }
        
        chain.doFilter(request, response);
    }
    
    protected void tokenExpired(HttpServletRequest request, HttpServletResponse response, String jwtToken, ApiResultError err) {
    	ApiResult axRet = new ApiResult(err);
		axRet.put("accessToken", jwtToken);
		
		sendUnauthorizedRes(request, response, axRet);
	}
    
    protected void sendUnauthorizedRes(HttpServletRequest request, HttpServletResponse response, ApiResult axRet) {
		ObjectMapper mapper = new ObjectMapper();
		
		String origin = request.getHeader(HttpHeaders.ORIGIN);
		log.debug("origin:{}", origin);
		
		response.addHeader("Access-Control-Allow-Origin", origin);
		response.addHeader("Access-Control-Allow-Credentials", "true");
		
		response.setContentType("application/json;charset=utf-8");
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		try {
			mapper.writeValue(response.getWriter(), axRet);
		} catch (JsonProcessingException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
}
