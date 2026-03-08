package com.erns.coching.search.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
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

import com.erns.coching.common.Const;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.service.AbstractCochingApiService;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.util.RestTemplateUtil;
import com.erns.coching.search.domain.CochingEsRawSearchDTO;
import com.erns.coching.search.domain.CochingEsRawVO;
import com.erns.coching.search.domain.EsManageDTO;
import com.erns.coching.search.mapper.CochingEsRawDAO;
import com.erns.coching.search.service.CochingEsRawService;
import com.erns.coching.sysProp.domain.SysPropVO;
import com.erns.coching.sysProp.service.SysPropService;
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
	private SysPropService sysPropService;

	private static final int BULK_INSERT_CHUNK_SIZE = 500;
  private static final DateTimeFormatter indexNameTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

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

	@Override
	public int resetTable() {
		dao.dropTable();

		return dao.createTable();
	}

	/**
	 * 배치용 코칭 원료 인덱스 초기화
	 */
	@Override
	public boolean resetCochingRawIndex(){
		SysPropVO indexPrefixProp = sysPropService.loadVo(Const.ES_COCHING_RAW_INDEX_PREFIX_SYSKEY);

		String newIndexName = "";
		{	//새로 생성할 인덱스 명 생성			
			if(null == indexPrefixProp){
				log.error("시스템설정 키 : {} 존재하지 않습니다.", Const.ES_COCHING_RAW_INDEX_PREFIX_SYSKEY);
				return false;
			}

			newIndexName = indexPrefixProp.getSysValue() + "_" +LocalDateTime.now().format(indexNameTimeFormatter);
		}

		//T_ES_RAW 초기화
		resetTable();
		
		//인덱스 생성
		boolean ret = false;
		try {
			ret = createEsIndex(newIndexName);
		} catch (JsonProcessingException e) {
			log.error("createEsIndex Error", e);
		}
		if (!ret) {
			return false;
		}

		{	//active properties, inactive properties 를 제외한 인덱스 삭제
			SysPropVO activeProp = sysPropService.loadVo(Const.ES_COCHING_RAW_ACTIVE_INDEX_SYSKEY);
			SysPropVO inactiveProp = sysPropService.loadVo(Const.ES_COCHING_RAW_INACTIVE_INDEX_SYSKEY);

			EsManageDTO param = EsManageDTO.builder()
					.commands("/"+indexPrefixProp.getSysValue()+"*?format=json")
					.build();

			ApiResult indicesRet = indices(param);
			if(!ApiResultError.isOk(indicesRet.getResultCode())){				
				log.error("fail indices : {} ", indicesRet);
				return true;
			}

			List<Map<String, Object>> indicesList = toListOfMaps(indicesRet.getResultData());
			for(Map<String, Object> indexMap : indicesList){
				String indexName = (String)indexMap.get("index");
				
				if(indexName.equals(activeProp.getSysValue())){
					continue;
				}

				if(indexName.equals(inactiveProp.getSysValue())){
					continue;
				}
				
				ApiResult deleteRet = deleteEsIndex(indexName);
				if(!ApiResultError.isOk(deleteRet.getResultCode())){
					//ignore
					log.error("fail deleteEsIndex : {} ", deleteRet);										
				}
			}
		}

		return true;
	}

	 // 항상 List<Map<String, Object>> 로 변환하는 메서드
	 @SuppressWarnings("unchecked")
	private static List<Map<String, Object>> toListOfMaps(Object obj) {
		if (obj instanceof List) {
				List<?> tempList = (List<?>) obj;
				if (!tempList.isEmpty() && tempList.get(0) instanceof Map) {
						return (List<Map<String, Object>>) obj;
				}
		} else if (obj instanceof Map) {
				return Collections.singletonList((Map<String, Object>) obj);
		}
		return Collections.emptyList();
}

	/**
	 * 주어진 이름으로 ES 인덱스 생성
	 * @param indexName
	 */
	@Override
	public boolean createEsIndex(String indexName) throws JsonProcessingException {
		log.info("createEsIndex : " + indexName);

		{	//active properties => inactive properties
			SysPropVO activeProp = sysPropService.loadVo(Const.ES_COCHING_RAW_ACTIVE_INDEX_SYSKEY);
			if(null != activeProp){				
				SysPropVO inactiveProp = SysPropVO.builder()
						.sysKey(Const.ES_COCHING_RAW_INACTIVE_INDEX_SYSKEY)
						.sysValue(activeProp.getSysValue())
						.build();

				sysPropService.setSysProp(inactiveProp);
			}		
		}

		{	// 이미 존재하는 인덱스를 삭제한다.
			ApiResult delApiResult = deleteEsIndex(indexName);
			log.info("delApiResult : {} ", delApiResult);
		}
		
		// 인덱스 생성
		String filePath = String.format("classpath:/es/mapping/coching_raw.json");
		Map<String, Object> map = loadDummyData(filePath);
		String mappingJson = objectMapper.writeValueAsString(map);

		EsManageDTO param = EsManageDTO.builder()
			.indexName(indexName)
			.mappingJson(mappingJson)
			.build();

		ApiResult createApiResult = createEsIndex(param);	
		
		if(!ApiResultError.isOk(createApiResult.getResultCode())) {
			log.error("fail createApiResult : {} ", createApiResult);			
			return false;
		}else{
			log.info("success createApiResult : {} ", createApiResult);
		}

		//데이터 삽입
		int dataInsertRet =	bulkInsertToEs(indexName, BULK_INSERT_CHUNK_SIZE);

		// if(dataInsertRet <= 0){
		// 	return false;
		// }

		{	//set new active properties
			SysPropVO activeProp = SysPropVO.builder()
					.sysKey(Const.ES_COCHING_RAW_ACTIVE_INDEX_SYSKEY)
					.sysValue(indexName)
					.build();
					
			sysPropService.setSysProp(activeProp);			
		}

		return true;
	}

	public ApiResult deleteEsIndex(String indexName) {
		String apiUrl = getEsApiUrl("/api/es/manage/index/delete.api");
		EsManageDTO param = EsManageDTO.builder()
				.indexName(indexName)
				.build();

		// API 호출
		log.debug("Call ES Api:{}", apiUrl);
		
		ApiResult apiResult = RestTemplateUtil.sendApiPostRequestBody(restTemplate, apiUrl, param);
		return apiResult;
	}

	public ApiResult createEsIndex(EsManageDTO param) {
		String apiUrl = getEsApiUrl("/api/es/manage/index/create.api");
		
		// API 호출
		log.debug("Call ES Api:{}", apiUrl);
		
		ApiResult apiResult = RestTemplateUtil.sendApiPostRequestBody(restTemplate, apiUrl, param);
		return apiResult;
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
	 * 코칭 원료데이터 Bulk Index
	 * @param indexName
	 * @param chunkSize
	 * @return
	 */
	@Override
	public int bulkInsertToEs(String indexName, int chunkSize) {
		CochingEsRawSearchDTO param = new CochingEsRawSearchDTO();
		param.setRowSize(chunkSize);		

		int totalCount = getListCount(param);
		int pageCount = (int) Math.ceil((double) totalCount / chunkSize);

		for (int page = 0; page < pageCount; page++) {
			// 데이터 청크 가져오기
			param.setPage((page+1));
			List<CochingEsRawVO> chunkData = getListVo(param);

			//Note: 임시요청, view_cnt 이 없는 데이터는 5~10 랜덤수 강제표시
			for (CochingEsRawVO vo : chunkData) {
				if(vo.getViewSumCnt() <= 0){
					int randNum = new Random().nextInt(5);
					randNum += 5;
					vo.setViewSumCnt(randNum);
				}
			}

			// NDJSON 형식으로 변환
			String ndJson = convertToNDJSON(chunkData, indexName);

			//log.debug("NDJSON:{}", ndJson);

			// API 호출
			ApiResult apiResult = sendBulkDataRequest(ndJson);
			log.info("resultCode:{}, messge:{}", apiResult.getResultCode(), apiResult.getResultMessage());
		}

		return totalCount;
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
