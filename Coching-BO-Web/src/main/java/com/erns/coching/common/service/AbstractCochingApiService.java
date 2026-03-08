package com.erns.coching.common.service;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.erns.coching.common.model.ApiResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
abstract public class AbstractCochingApiService {
	
	protected static final ObjectMapper objectMapper = new ObjectMapper();
	
	protected RestTemplate restTemplate;
	
	@Autowired
	protected ResourceLoader resourceLoader;

	public AbstractCochingApiService(){
		objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
	}

	/**
	 * 세션정보를 가져온다.
	 * @return
	 */
	protected HttpSession getSession() {
		ServletRequestAttributes reqAttr = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
    	if(reqAttr == null) {
    		return null;
    	}

		HttpServletRequest req = reqAttr.getRequest();
		HttpSession session = req.getSession(false);

		return session;
	}

	/**
	 * 개발용 Dummy Json 로드
	 * @param filePath
	 * @return
	 */
	protected Map<String, Object> loadDummyData(String filePath){
		Resource res = resourceLoader.getResource(filePath);

		TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
		Map<String, Object> map = null;

		try {
			map = objectMapper.readValue(res.getInputStream(), typeRef);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return map;
	}

//	/**
//	 * 개발용 Dummy Json 로드
//	 * @param filePath
//	 * @return
//	 */
//	protected ContractPaymentSetDTO loadPaymentDummyData(String filePath){
//		Resource res = resourceLoader.getResource(filePath);
//
//		ObjectMapper mapper = new ObjectMapper();
//
//		ContractPaymentSetDTO paymentResVO = null;
//		try {
//			paymentResVO = mapper.readValue(res.getInputStream(), ContractPaymentSetDTO.class);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		return paymentResVO;
//	}

	/**
	 * 현재 request 를 얻는다.
	 * @return
	 */
	protected final HttpServletRequest getRequest() {
		ServletRequestAttributes reqAttr = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		if(reqAttr == null) {
			return null;
		}

		HttpServletRequest req = reqAttr.getRequest();
		return req;
	}
	
	/**
	 * DUMMY API result 를 리턴한다
	 * @param res
	 * @param filePath
	 */
	protected void sendDummyResult(HttpServletResponse res, String filePath) {
		ApiResult axRet = new ApiResult(loadDummyData(filePath));
		try {
			res.setContentType(MediaType.APPLICATION_JSON_VALUE);            
            res.setCharacterEncoding("UTF-8");
            
            String jsonResponse = objectMapper.writeValueAsString(axRet);
            res.getWriter().write(jsonResponse);
            
        } catch (IOException e) {
            log.error("Error writing response", e);
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
	}
}
