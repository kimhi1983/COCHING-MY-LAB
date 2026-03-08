package com.erns.coching.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.common.type.ApiResultError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * <p>RestTemplate 유틸</p>
 *
 * @author Hunwoo Park
 *
 */
@Slf4j
public class QpRestTemplateUtil {

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
	 * <p>{@link QpRestTemplateUtil#sendApiPost(RestTemplate, String, MultiValueMap, HttpHeaders)} 를 베이스로 하며,
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
	 * <p>{@link QpRestTemplateUtil#sendApiPost(RestTemplate, String, MultiValueMap, HttpHeaders)} 를 베이스로 하며,
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
	 * <p>{@link QpRestTemplateUtil#sendApiPost(RestTemplate, String, IReqDto, HttpHeaders)} 를 베이스로 하며,
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
	 * <p>{@link QpRestTemplateUtil#sendApiPost(RestTemplate, String, MultiValueMap, HttpHeaders)} 를 베이스로 하며,
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

	/**
	 * <p>Post 방식을 사용하여 API Call(RequestBody)</p>
	 *
	 * <p>{@link QpRestTemplateUtil#sendApiPost(RestTemplate, String, IReqDto, HttpHeaders)} 를 베이스로 하며,
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
			return new ApiResult(ApiResultError.ERROR_DEFAULT);
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

	public static String sendApiTextPost(RestTemplate restTemplate, String uriString, IReqDto param, HttpHeaders headers) {

		String responseBody = "";
		try {

			MultiValueMap<String, String> sendApiParam = MultiValueMapConverter.convert(objectMapper, param);
			log.info("sendApiParam:{}", sendApiParam);

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(sendApiParam, headers);

			ResponseEntity<String> responseEntity = restTemplate.exchange(
					uriString, HttpMethod.POST, request, String.class);

			// 응답 바디를 String으로 받아서 출력
			responseBody = responseEntity.getBody();
			log.debug("Response Body: {}", responseBody);


			if(responseBody == null) {
				responseBody = "";
			}

		}catch(Exception e) {
			log.error("Coching Api Error!", e);
		}

		return responseBody;
	}
}
