package com.erns.es.search.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erns.es.common.controller.AbstractApiController;
import com.erns.es.common.model.ApiResult;
import com.erns.es.common.type.ApiResultError;
import com.erns.es.search.domain.EsManageDTO;
import com.erns.es.search.domain.vg.ValidEsManage0101;
import com.erns.es.search.domain.vg.ValidEsManage0102;
import com.erns.es.search.domain.vg.ValidEsManage0104;
import com.erns.es.search.service.EsManageService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/es/manage")
public class EsManageController extends AbstractApiController {

	private final EsManageService esManageService;

	@Autowired
	public EsManageController(EsManageService esManageService) {
		this.esManageService = esManageService;
	}

	@PostMapping("/indices.api")
	public ApiResult getIndices(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody @Validated(ValidEsManage0101.class) EsManageDTO param,
			Errors errors) {
		log.debug("param:{}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			List<Map<String, Object>> data = esManageService.getIndices(param);
			axRet.setResultData(data).set(ApiResultError.NO_ERROR);
		} catch (Exception e) {
			log.error("Es Search Error", e);
		}

		return axRet;
	}

	@PostMapping("/index/create.api")
	public ApiResult createIndex(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody @Validated(ValidEsManage0102.class) EsManageDTO param,
			Errors errors) {
		log.debug("param:{}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			Map<String, Object> retMap = esManageService.createIndexWithMapping(param.getIndexName(), param.getMappingJson());
			axRet.setResultData(retMap).set(ApiResultError.NO_ERROR);
		} catch (Exception e) {
			log.error("Es createIndex Error", e);
		}

		return axRet;
	}

	@PostMapping("/index/delete.api")
	public ApiResult deleteIndex(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody @Validated(ValidEsManage0104.class) EsManageDTO param,
			Errors errors) {
		log.debug("param:{}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			Map<String, Object> retMap = esManageService.deleteIndex(param.getIndexName());
			axRet.setResultData(retMap).set(ApiResultError.NO_ERROR);
		} catch (Exception e) {
			log.error("Es deleteIndex Error", e);
		}

		return axRet;
	}

	@PostMapping({
		"/bulkEs.api",
		"/bulkInsert.api" //TODO : 추후 삭제 예정
	})
	public ApiResult bulkEsApi(@RequestBody String jsonData) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		
		//log.debug(jsonData);

		try {
			Response bulkResponse = esManageService.bulkEsApi(jsonData);

			// HTTP 상태 코드 확인
			int statusCode = bulkResponse.getStatusLine().getStatusCode();
			if (statusCode < 200 || statusCode >= 300) {
				axRet.setResultMessage("Bulk esApi failed: HTTP " + statusCode)
					.set(ApiResultError.ERROR_DEFAULT);
				return axRet;
			}

			// 응답 본문을 가져와 JSON 파싱
			String responseBody = EntityUtils.toString(bulkResponse.getEntity());
			JsonNode jsonResponse = new ObjectMapper().readTree(responseBody);

			// Elasticsearch 응답에서 `errors` 필드 확인
			boolean hasErrors = jsonResponse.get("errors").asBoolean();
			if (hasErrors) {
				axRet.setResultMessage("Partial failure: " + responseBody) // 일부 실패한 데이터 포함
					.set(ApiResultError.ERROR_DEFAULT);
			} else {
				axRet.setResultMessage("Bulk insert successful")
					.set(ApiResultError.NO_ERROR);
			}

		} catch (Exception e) {
			log.error("bulkEsApi Error", e);
			axRet.setResultMessage(e.getMessage())
					.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}
}
