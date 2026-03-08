package com.erns.coching.search.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.service.AbstractCochingApiService;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.util.RestTemplateUtil;
import com.erns.coching.search.domain.CochingEsRawSearchDTO;
import com.erns.coching.search.domain.CochingEsRawVO;
import com.erns.coching.search.domain.EsManageDTO;
import com.erns.coching.search.mapper.CochingEsRawDAO;
import com.erns.coching.search.service.CochingEsRawService;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * <p>
 * 코칭 ES용 원료 Service
 * </p>
 *
 * @author Hunwoo Park
 *
 */
@Slf4j
@Transactional
@Service
public class CochingEsRawServiceImpl extends AbstractCochingApiService implements CochingEsRawService {

	// 테스트 모드
	@Value("${es.search.api.isDummyTest:true}")
	protected boolean isDummyTest;

	@Value("${es.search.protocal:http}")
	protected String esApiProtocal;

	@Value("${es.search.host:localhost}")
	protected String esApiHostname;

	@Value("${es.search.port:8280}")
	protected int esApiPort;

	private CochingEsRawDAO dao;

	@Autowired
	public CochingEsRawServiceImpl(RestTemplateBuilder builder, CochingEsRawDAO dao) {
		super();
		this.restTemplate = builder.build();
		this.dao = dao;
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

	@Override
	public List<Map<String, Object>> getList(CochingEsRawSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public List<CochingEsRawVO> getListVo(CochingEsRawSearchDTO param) {
		return dao.getListVo(param);
	}

	@Override
	public int getListCount(CochingEsRawSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public Map<String, Object> load(long rawSeq) {
		return dao.load(rawSeq);
	}

	/**
	 * 인덱스 목록 조회
	 * 
	 * @param res
	 * @param param
	 * @throws JsonProcessingException
	 */
	@Override
	public ApiResult indices(EsManageDTO param) {
		String apiUrl = getEsApiUrl("/api/es/manage/indices.api");

		// By-PASS 리턴
		log.debug("Call ES Api:{}", apiUrl);
		ApiResult apiResult = RestTemplateUtil.sendApiPostRequestBody(restTemplate, apiUrl, param);
		return apiResult;
	}

	/**
	 * 코칭 원료데이터 Bulk Index/delete
	 * @param indexName
	 * @param rawSeq
	 * @return
	 */
	@Override
	public int bulkSyncDocToEs(String indexName, long[] rawSeqs) {
		if(rawSeqs == null || rawSeqs.length == 0){
			return 0;
		}

		CochingEsRawSearchDTO param = new CochingEsRawSearchDTO();
		param.setRowSize(-1);
		param.setRawSeqs(rawSeqs);

		//index 대상
		List<CochingEsRawVO> esDocList = dao.getListVoForEsRenewIndexItems(param);
		//Note: 임시요청, view_cnt 이 없는 데이터는 5~10 랜덤수 강제표시
		for(CochingEsRawVO vo : esDocList){
			if(vo.getViewSumCnt() <= 0){
				int randNum = new Random().nextInt(5);
				randNum += 5;
				vo.setViewSumCnt(randNum);
			}
		}


		//delete 대상
		List<CochingEsRawVO> esDocDelList = dao.getListVoForEsRenewDeleteItems(param);

		esDocList.addAll(esDocDelList);
		// NDJSON 형식으로 변환
		String ndJson = convertToNDJSON(esDocList, indexName);
		//log.debug("NDJSON:{}", ndJson);

		// API 호출
		ApiResult apiResult = sendBulkDataRequest(ndJson);
		log.info("resultCode:{}, messge:{}", apiResult.getResultCode(), apiResult.getResultMessage());

		return esDocList.size();
	}

	/**
	 * INDEX NDJSON 형식으로 변환
	 * @param dataList
	 * @param indexName
	 * @param idFieldName
	 * @return
	 */
	private String convertToNDJSON(List<CochingEsRawVO> dataList, String indexName) {
		StringBuilder ndJsonBuilder = new StringBuilder();

		for (CochingEsRawVO data : dataList) {
			String id = String.valueOf(data.getRawSeq());

			String job = data.getJob();
			if(!StringUtils.hasText(job)) {
				job = "index"; //기본값
			}

			try {
				Map<String, Object> indexData = new HashMap<>();
				indexData.put("_index", indexName);
				indexData.put("_id", id);

				Map<String, Object> indexMap = new HashMap<>();
				indexMap.put(job, indexData);

				// JSON 문자열 변환
				String indexMeta = objectMapper.writeValueAsString(indexMap);
				ndJsonBuilder.append(indexMeta).append("\n");

				if("delete".equals(job)) {
					//Do nothing
				}else{
					String dataJson = objectMapper.writeValueAsString(data);
					ndJsonBuilder.append(dataJson).append("\n");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return ndJsonBuilder.toString();
	}

	/**
	 * Bulk data 요청
	 * 
	 * @param jsonData
	 * @return
	 */
	private ApiResult sendBulkDataRequest(String jsonData) {		
		ApiResult apiResult = new ApiResult(ApiResultError.ERROR_DEFAULT);

		String apiUrl = getEsApiUrl("/api/es/manage/bulkEs.api");

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");

			HttpEntity<String> requestEntity = new HttpEntity<>(jsonData, headers);
			ResponseEntity<ApiResult> response = restTemplate.postForEntity(apiUrl, requestEntity, ApiResult.class);

			// HTTP 상태 코드 확인
			if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
				return response.getBody();				
			}
			
		} catch (Exception e) {
			apiResult.setResultMessage(e.getMessage());
			log.error("❌ 실패: HTTP " + e.getMessage());
		}

		return apiResult;
	}
}
