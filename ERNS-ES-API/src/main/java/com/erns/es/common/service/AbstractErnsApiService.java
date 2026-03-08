package com.erns.es.common.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

abstract public class AbstractErnsApiService {

	protected RestTemplate restTemplate;
	@Autowired
	protected ResourceLoader resourceLoader;

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
		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> map = null;

		try {
			map = mapper.readValue(res.getInputStream(), typeRef);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return map;
	}
}
