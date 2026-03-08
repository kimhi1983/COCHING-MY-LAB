package com.erns.coching.common.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.common.type.ApiResultError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * <p>RestTemplate 유틸</p>
 *
 * @author Hunwoo Park
 *
 */
@Slf4j
public class RestTemplateUtil {

	protected static final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * BaseUrl + subPath Url을 리턴
	 * @param baseUrl
	 * @param subPath
	 * @return
	 */
	public static String getApiUrl(String baseUrl, String subPath) {
		if(!StringUtils.hasText(subPath)) {
			return baseUrl;
		}

		if(subPath.startsWith("/")) {
			return String.format("%s%s", baseUrl, subPath);
		}

		return String.format("%s/%s", baseUrl, subPath);
	}

	/**
	 * <p>Post 방식을 사용하여 API Call</p>
	 *
	 * <p>{@link RestTemplateUtil#sendApiPost(RestTemplate, String, MultiValueMap, HttpHeaders)} 를 베이스로 하며,
	 * header 생략</p>
	 *
	 * @param restTemplate
	 * @param uriString 호출할 URL
	 * @param param 전송할 파라미터 values
	 * @return 전송결과를 {@link ApiResult}로 반환
	 */
	public static ApiResult sendApiPost(RestTemplate restTemplate, String uriString, Map<String, Object> param) {
		MultiValueMap<String, String> apiParam = MultiValueMapConverter.convert(objectMapper, param);
		return sendApiPost(restTemplate, uriString, apiParam, null);
	}

	/**
	 * <p>Post 방식을 사용하여 API Call</p>
	 *
	 * <p>{@link RestTemplateUtil#sendApiPost(RestTemplate, String, MultiValueMap, HttpHeaders)} 를 베이스로 하며,
	 * header 생략</p>
	 *
	 * @param restTemplate
	 * @param uriString 호출할 URL
	 * @param param 전송할 파라미터 values
	 * @return 전송결과를 {@link ApiResult}로 반환
	 */
	public static ApiResult sendApiPost(RestTemplate restTemplate, String uriString, MultiValueMap<String, String> param) {
		return sendApiPost(restTemplate, uriString, param, null);
	}


	/**
	 * <p>Post 방식을 사용하여 API Call</p>
	 *
	 * <p>{@link RestTemplateUtil#sendApiPost(RestTemplate, String, IReqDto, HttpHeaders)} 를 베이스로 하며,
	 * header 생략</p>
	 *
	 * @param restTemplate
	 * @param uriString 호출할 URL
	 * @param param 전송할 파라미터 values
	 * @return 전송결과를 {@link ApiResult}로 반환
	 */
	public static ApiResult sendApiPost(RestTemplate restTemplate, String uriString, IReqDto param) {
		return sendApiPost(restTemplate, uriString, param, null);
	}


	/**
	 * <p>Post 방식을 사용하여 API Call</p>
	 *
	 * <p>{@link RestTemplateUtil#sendApiPost(RestTemplate, String, MultiValueMap, HttpHeaders)} 를 베이스로 하며,
	 * Post 파라미터를 IReqDto 인터페이스 형태로 받는다.</p>
	 *
	 * @param restTemplate
	 * @param uriString 호출할 URL
	 * @param param 전송할 파라미터 values
	 * @param headers 추가로 설정할 header 목록
	 * @return 전송결과를 {@link ApiResult}로 반환
	 */
	public static ApiResult sendApiPost(RestTemplate restTemplate, String uriString, IReqDto param, HttpHeaders headers) {
		MultiValueMap<String, String> apiParam = MultiValueMapConverter.convert(objectMapper, param);
		return sendApiPost(restTemplate, uriString, apiParam, headers);
	}

	/**
	 * <p>Post 방식을 사용하여 API Call</p>
	 *
	 * @param restTemplate
	 * @param uriString 호출할 URL
	 * @param param 전송할 파라미터 values
	 * @param headers 추가로 설정할 header 목록
	 * @return 전송결과를 {@link ApiResult}로 반환
	 */
	public static ApiResult sendApiPost(RestTemplate restTemplate, String uriString, MultiValueMap<String, String> param, HttpHeaders headers) {
		ParameterizedTypeReference<HashMap<String, Object>> responseType =
				new ParameterizedTypeReference<HashMap<String, Object>>() {};

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(param, headers);

		Map<String, Object> ret = restTemplate.exchange(uriString
				, HttpMethod.POST
				, request
				, responseType).getBody();

		if(ret == null) {
			return new ApiResult();
		}

		ApiResult axRet = new ApiResult(ret);
		return axRet;
	}
	
	public static void sendApiPostByPass(RestTemplate restTemplate, HttpServletResponse response, String uriString, IReqDto param) {
		sendApiPostByPass(restTemplate, response, uriString, param, null);
	}
	
	public static void sendApiPostByPass(RestTemplate restTemplate, HttpServletResponse response, String uriString, IReqDto param, HttpHeaders headers) {
		MultiValueMap<String, String> apiParam = MultiValueMapConverter.convert(objectMapper, param);
		sendApiPostByPass(restTemplate, response,uriString, apiParam, headers);
	}
	
	public static void sendApiPostByPass(RestTemplate restTemplate, HttpServletResponse response, String uriString, MultiValueMap<String, String> param, HttpHeaders headers) {		
		// 응답을 OutputStream에 바이패스 처리
	    restTemplate.execute(uriString, HttpMethod.POST, requestEntity -> {
	        // 요청 헤더 설정
	        if(headers != null) {
	        	requestEntity.getHeaders().addAll(headers);
	        }
	        
	        if (param != null) {
	            // 요청 바디에 파라미터 추가
	            new FormHttpMessageConverter().write(param, MediaType.APPLICATION_FORM_URLENCODED, requestEntity);
	        }
	    }, responseExtractor -> {
	    	// 응답 헤더 복사
//	        responseExtractor.getHeaders().forEach((key, values) -> {
//	            for (String value : values) {
//	                response.addHeader(key, value);
//	            }
//	        });
	    	response.addHeader("Content-Type", "application/json;charset=UTF-8");
	        
	        try (InputStream inputStream = responseExtractor.getBody();
	        	OutputStream outputStream = response.getOutputStream();
	        ) {
	            byte[] buffer = new byte[8192];
	            int bytesRead;
	            while ((bytesRead = inputStream.read(buffer)) != -1) {
	                outputStream.write(buffer, 0, bytesRead);
	            }
	        }
	        return null; // 반환 값이 필요 없으므로 null 반환
	    });
	}

	/**
	 * <p>Post 방식을 사용하여 API Call(RequestBody)</p>
	 *
	 * <p>{@link RestTemplateUtil#sendApiPost(RestTemplate, String, IReqDto, HttpHeaders)} 를 베이스로 하며,
	 * header 생략</p>
	 *
	 * @param restTemplate
	 * @param uriString 호출할 URL
	 * @param param 전송할 파라미터 values
	 * @return 전송결과를 {@link ApiResult}로 반환
	 * @throws JsonProcessingException
	 */
	public static ApiResult sendApiPostRequestBody(RestTemplate restTemplate, String uriString, IReqDto param) {
		return sendApiPostRequestBody(restTemplate, uriString, param, null);
	}

	/**
	 * <p>Post 방식을 사용하여 API Call(RequestBody)</p>
	 *
	 * @param restTemplate
	 * @param uriString 호출할 URL
	 * @param param 전송할 파라미터 values
	 * @param headers 추가로 설정할 header 목록
	 * @return 전송결과를 {@link ApiResult}로 반환
	 * @throws JsonProcessingException
	 */
	public static ApiResult sendApiPostRequestBody(RestTemplate restTemplate, String uriString, IReqDto param, HttpHeaders headers)  {
		ParameterizedTypeReference<HashMap<String, Object>> responseType =
				new ParameterizedTypeReference<HashMap<String, Object>>() {};

		String jsonBody;
		try {
			jsonBody = objectMapper.writeValueAsString(param);
		} catch (JsonProcessingException e) {
			return new ApiResult(ApiResultError.ERROR_JSON);
		}
		log.debug("jsonBody:{}",jsonBody);

		if(headers == null) {
			headers = new HttpHeaders();
		}
		headers.setContentType(MediaType.APPLICATION_JSON);


		HttpEntity<String> request =
				new HttpEntity<String>(jsonBody, headers);

		Map<String, Object> ret = restTemplate.exchange(uriString
				, HttpMethod.POST
				, request
				, responseType).getBody();

		if(ret == null) {
			return new ApiResult();
		}

		ApiResult axRet = new ApiResult(ret);
		return axRet;
	}
	
	
	
	public static void sendApiPostRequestBodyByPass(RestTemplate restTemplate, HttpServletResponse response, String uriString, IReqDto param) throws JsonProcessingException {
		sendApiPostRequestBodyByPass(restTemplate, response, uriString, param, null);
	}
	
	
	/**
	 * <p>Post 방식을 사용하여 API Call(RequestBody), 응답을 by-pass 처리</p>
	 * @param restTemplate
	 * @param response
	 * @param uriString
	 * @param param
	 * @param headers
	 * @throws JsonProcessingException
	 */
	public static void sendApiPostRequestBodyByPass(RestTemplate restTemplate, HttpServletResponse response, String uriString, IReqDto param, HttpHeaders headers) throws JsonProcessingException {
		// 응답을 OutputStream에 바이패스 처리
	    try {
		    restTemplate.execute(uriString
		    	, HttpMethod.POST
		    	, requestEntity -> {
			        // 요청 헤더 설정
			    	if(headers != null) {
						requestEntity.getHeaders().addAll(headers);
			        }
			    	requestEntity.getHeaders().setContentType(MediaType.APPLICATION_JSON);
			        
			        // 요청 본문 설정
			    	try (OutputStream outputStream = requestEntity.getBody();
			        ) {
			    		//Debug
			    		String jsonBody = objectMapper.writeValueAsString(param);
			    		log.debug("jsonBody: {}", jsonBody);
			    		
			    		objectMapper.writeValue(outputStream, param);
			        }			        
			    }, responseExtractor -> {
			    	
			    	response.addHeader("Content-Type", "application/json;charset=UTF-8");
			    	// 응답 헤더 복사
		//	        responseExtractor.getHeaders().forEach((key, values) -> {
		//	            for (String value : values) {
		//	                response.addHeader(key, value);
		//	            }
		//	        });
			        
			        try (InputStream inputStream = responseExtractor.getBody();
			        	OutputStream outputStream = response.getOutputStream();
			        ) {
			            byte[] buffer = new byte[8192];
			            int bytesRead;
			            while ((bytesRead = inputStream.read(buffer)) != -1) {
			                outputStream.write(buffer, 0, bytesRead);
			            }
			        }
			        return null; // 반환 값이 필요 없으므로 null 반환
			    });
	    } catch (HttpClientErrorException e) {
	        log.error("HTTP Error: {}", e.getResponseBodyAsString(), e);
	        throw e;
	    }
	}
}
