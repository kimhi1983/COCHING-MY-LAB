package com.erns.coching.search.service;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

import com.erns.coching.common.file.domain.DocProcessDTO;
import com.erns.coching.common.file.domain.ResultPdfAllProcessDTO;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.service.AbstractCochingApiService;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.util.RestTemplateUtil;
import com.erns.coching.search.domain.EsIngredientSearchDTO;
import com.erns.coching.search.domain.EsSearchDTO;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EsSearchService extends AbstractCochingApiService {

	// 테스트 모드
	@Value("${es.search.api.isDummyTest:true}")
	protected boolean isDummyTest;

	@Value("${es.search.protocal:http}")
	protected String esApiProtocal;

	@Value("${es.search.host:localhost}")
	protected String esApiHostname;

	@Value("${es.search.port:8280}")
	protected int esApiPort;

	@Autowired
	public EsSearchService(RestTemplateBuilder builder) {
		this.restTemplate = builder.build();
	}

	/**
	 * ES API 의 Base URL 리턴
	 * 
	 * @return
	 */
	protected final String getEsApiBaseUrl() {
		return String.format("%s://%s:%d", esApiProtocal, esApiHostname, esApiPort);
	}

	/**
	 * ES API 의 전체 경로를 리턴
	 * 
	 * @param subPath
	 * @return
	 */
	protected final String getEsApiUrl(String pSubPath) {
		String subPath = pSubPath;
		if (subPath.startsWith("/")) {
			subPath = subPath.substring(1);
		}

		return String.format("%s/%s", getEsApiBaseUrl(), subPath);
	}

	/**
	 * 화해 상품검색
	 * 
	 * @param res
	 * @param param
	 * @throws JsonProcessingException
	 */
	public void searchTextForHwProducts(HttpServletResponse res, EsSearchDTO param) throws JsonProcessingException {
		//String apiUrl = getEsApiUrl("/api/es/search/hwProd/text.api");
		String apiUrl = getEsApiUrl("/api/v2/es/search/hwProd/text.api");

		if (isDummyTest) { // 로컬테스트용
			String filePath = String.format("classpath:/api/es/search/hwProd/text_ok.json");
			sendDummyResult(res, filePath);
		} else {
			// By-PASS 리턴
			log.debug("Call ES Api:{}", apiUrl);
			RestTemplateUtil.sendApiPostRequestBodyByPass(restTemplate, res, apiUrl, param);
		}
	}

	/**
	 * 화해 상품조회
	 * 
	 * @param res
	 * @param param
	 * @throws JsonProcessingException
	 */
	public void searchIdForHwProduct(HttpServletResponse res, EsSearchDTO param) throws JsonProcessingException {
		//String apiUrl = getEsApiUrl("/api/es/search/hwProd.api");
		String apiUrl = getEsApiUrl("/api/v2/es/search/hwProd.api");

		if (isDummyTest) { // 로컬테스트용
			String filePath = String.format("classpath:/api/es/search/hwProd/one_text_ok.json");
			sendDummyResult(res, filePath);
		} else {
			// By-PASS 리턴
			log.debug("Call ES Api:{}", apiUrl);
			RestTemplateUtil.sendApiPostRequestBodyByPass(restTemplate, res, apiUrl, param);
		}
	}

	/**
	 * 원료 검색
	 * 
	 * @param res
	 * @param param
	 * @throws JsonProcessingException
	 */
	public void searchTextForRaws(HttpServletResponse res, EsSearchDTO param) throws JsonProcessingException {
		//String apiUrl = getEsApiUrl("/api/es/search/raws/text.api");
		String apiUrl = getEsApiUrl("/api/v2/es/search/raws/text.api");

		if (isDummyTest) { // 로컬테스트용
			String filePath = String.format("classpath:/api/es/search/raws/text_ok.json");
			sendDummyResult(res, filePath);
		} else {
			// By-PASS 리턴
			log.debug("Call ES Api:{}", apiUrl);
			RestTemplateUtil.sendApiPostRequestBodyByPass(restTemplate, res, apiUrl, param);
		}
	}

	/**
	 * 원료 조회
	 * 
	 * @param res
	 * @param param
	 * @throws JsonProcessingException
	 */
	public void searchIdForRaws(HttpServletResponse res, EsSearchDTO param) throws JsonProcessingException {
		//String apiUrl = getEsApiUrl("/api/es/search/raws.api");
		String apiUrl = getEsApiUrl("/api/v2/es/search/raws.api");

		if (isDummyTest) { // 로컬테스트용
			String filePath = String.format("classpath:/api/es/search/raws/text_ok.json");
			sendDummyResult(res, filePath);
		} else {
			// By-PASS 리턴
			log.debug("Call ES Api:{}", apiUrl);
			RestTemplateUtil.sendApiPostRequestBodyByPass(restTemplate, res, apiUrl, param);
		}
	}

	/**
	 * 성분 조회
	 * 
	 * @param res
	 * @param param
	 * @throws JsonProcessingException
	 */
	public void searchIngredient(HttpServletResponse res, EsIngredientSearchDTO param) throws JsonProcessingException {
		// boolean isTest = isDummyTest;
		boolean isTest = false;

		//String apiUrl = getEsApiUrl("/api/es/search/coching/ingredient/load.api");
		String apiUrl = getEsApiUrl("/api/v2/es/search/coching/ingredient/load.api");

		if (isTest) { // 로컬테스트용
			String filePath = String.format("classpath:/api/es/search/ingd/ingd_coching_ok.json");
			sendDummyResult(res, filePath);
		} else {
			// By-PASS 리턴
			log.debug("Call ES Api:{}", apiUrl);
			RestTemplateUtil.sendApiPostRequestBodyByPass(restTemplate, res, apiUrl, param);
		}
	}

	/**
	 * 성분명 조회
	 * 
	 * @param res
	 * @param param
	 * @throws JsonProcessingException
	 */
	public void searchIngredientNames(HttpServletResponse res, EsSearchDTO param) throws JsonProcessingException {
		// boolean isTest = isDummyTest;
		boolean isTest = false;

		//String apiUrl = getEsApiUrl("/api/es/search/coching/ingredient/names.api");
		String apiUrl = getEsApiUrl("/api/v2/es/search/coching/ingredient/names.api");

		if (isTest) { // 로컬테스트용
			String filePath = String.format("classpath:/api/es/search/ingd/ingd_coching_names_ok.json");
			sendDummyResult(res, filePath);
		} else {
			// By-PASS 리턴
			log.debug("Call ES Api:{}", apiUrl);
			RestTemplateUtil.sendApiPostRequestBodyByPass(restTemplate, res, apiUrl, param);
		}
	}

	/**
	 * 문서 변환 처리
	 * @param req
	 * @param res
	 * @throws JsonProcessingException
	 */
	public ResultPdfAllProcessDTO docPreProcess(DocProcessDTO param) throws JsonProcessingException {
		
		String apiUrl = getEsApiUrl("/api/doc/pdf/all.api");

		log.debug("Call ES Api:{}", apiUrl);
		ApiResult result = RestTemplateUtil.sendApiPostRequestBody(restTemplate, apiUrl, param);
		if(result.getResultCode().equals(ApiResultError.NO_ERROR.getCode())) {
			Object data = result.getResultData();
			String json = objectMapper.writeValueAsString(data);
			return objectMapper.readValue(json, ResultPdfAllProcessDTO.class);
		}

		return null;
	}

	/**
	 * 코칭TV 검색
	 * 
	 * @param res
	 * @param param
	 * @throws JsonProcessingException
	 */
	public void searchTextForTvs(HttpServletResponse res, EsSearchDTO param) throws JsonProcessingException {
		//String apiUrl = getEsApiUrl("/api/es/search/tvs/text.api");
		String apiUrl = getEsApiUrl("/api/v2/es/search/tvs/text.api");

		if (isDummyTest) { // 로컬테스트용
			String filePath = String.format("classpath:/api/es/search/tvs/text_ok.json");
			sendDummyResult(res, filePath);
		} else {
			// By-PASS 리턴
			log.debug("Call ES Api:{}", apiUrl);
			RestTemplateUtil.sendApiPostRequestBodyByPass(restTemplate, res, apiUrl, param);
		}
	}

	/**
	 * 코칭TV 조회
	 * 
	 * @param res
	 * @param param
	 * @throws JsonProcessingException
	 */
	public void searchIdForTvs(HttpServletResponse res, EsSearchDTO param) throws JsonProcessingException {
		//String apiUrl = getEsApiUrl("/api/es/search/tvs.api");
		String apiUrl = getEsApiUrl("/api/v2/es/search/tvs.api");

		if (isDummyTest) { // 로컬테스트용
			String filePath = String.format("classpath:/api/es/search/tvs/text_ok.json");
			sendDummyResult(res, filePath);
		} else {
			// By-PASS 리턴
			log.debug("Call ES Api:{}", apiUrl);
			RestTemplateUtil.sendApiPostRequestBodyByPass(restTemplate, res, apiUrl, param);
		}
	}

	/**
	 * 화해 브랜드 top 10000 조회
	 * 
	 * @param res
	 * @param param
	 * @throws JsonProcessingException
	 */
	public ApiResult searchHwBrandTop10000(EsSearchDTO param) throws JsonProcessingException {
		ApiResult result = new ApiResult(ApiResultError.ERROR_DEFAULT);
		String apiUrl = getEsApiUrl("/api/v2/es/search/hwProd/brand/aggregation.api");

		log.debug("Call ES Api:{}", apiUrl);
		result = RestTemplateUtil.sendApiPostRequestBody(restTemplate, apiUrl, param);

		return result;
	}
}
