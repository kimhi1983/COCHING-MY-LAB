package com.erns.es.search.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erns.es.common.controller.AbstractApiController;
import com.erns.es.common.model.ApiResult;
import com.erns.es.common.type.ApiResultError;
import com.erns.es.search.domain.EsResultDTO;
import com.erns.es.search.domain.EsSearchDTO;
import com.erns.es.search.domain.vg.ValidEsSearch0011;
import com.erns.es.search.domain.vg.ValidEsSearch1011;
import com.erns.es.search.domain.vg.ValidEsSearch1012;
import com.erns.es.search.domain.vg.ValidEsSearch2011;
import com.erns.es.search.domain.vg.ValidEsSearch2012;
import com.erns.es.search.domain.vg.ValidEsSearch3011;
import com.erns.es.search.domain.vg.ValidEsSearch3012;
import com.erns.es.search.service.EsSearchV2Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v2/es/search")
// @PreAuthorize("isAuthenticated()")
public class EsSearchV2Controller extends AbstractApiController {

	private final EsSearchV2Service searchService;

	private final int API_VERSION = 2;

	@Autowired
	public EsSearchV2Controller(EsSearchV2Service searchService) {
		this.searchService = searchService;
	}

	/**
	 * 원료 목록 검색
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @param errors
	 * @return
	 */
	@RequestMapping("/raws/text.api")
	public ApiResult searchTextForCochingRaw(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody @Validated(ValidEsSearch1011.class) EsSearchDTO param,
			Errors errors) {
		log.debug("param:{}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			EsResultDTO data = searchService.searchCochingRaw(param);
			data.setVersion(API_VERSION);
			axRet.setResultData(data).set(ApiResultError.NO_ERROR);
		} catch (Exception e) {
			log.error("Es Search Error", e);
		}

		return axRet;
	}

	/**
	 * 원료 단일 검색
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @param errors
	 * @return
	 */
	@RequestMapping("/raws.api")
	public ApiResult searchCochingRaw(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody @Validated(ValidEsSearch1012.class) EsSearchDTO param,
			Errors errors) {
		log.debug("param:{}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			EsResultDTO data = searchService.searchCochingRawById(param);
			data.setVersion(API_VERSION);
			axRet.setResultData(data).set(ApiResultError.NO_ERROR);
		} catch (Exception e) {
			log.error("Es Search Error", e);
		}

		return axRet;
	}

	/**
	 * 제품 목록 검색
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @param errors
	 * @return
	 */
	@RequestMapping("/hwProd/text.api")
	public ApiResult searchTextForHwProduct(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody @Validated(ValidEsSearch2011.class) EsSearchDTO param,
			Errors errors) {
		log.debug("param:{}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			EsResultDTO data = searchService.searchHwProduct(param);
			data.setVersion(API_VERSION);
			axRet.setResultData(data).set(ApiResultError.NO_ERROR);
		} catch (Exception e) {
			log.error("Es Search Error", e);
		}

		return axRet;
	}

	/**
	 * 제품 단건 검색
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @param errors
	 * @return
	 */
	@RequestMapping("/hwProd.api")
	public ApiResult searchHwProduct(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody @Validated(ValidEsSearch2012.class) EsSearchDTO param,
			Errors errors) {
		log.debug("param:{}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			EsResultDTO data = searchService.searchProductById(param);
			data.setVersion(API_VERSION);
			axRet.setResultData(data).set(ApiResultError.NO_ERROR);
		} catch (Exception e) {
			log.error("Es Search Error", e);
		}

		return axRet;
	}

	/**
	 * 성분명 검색
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @param errors
	 * @return
	 */
	@RequestMapping("/coching/ingredient/names.api")
	public ApiResult searchCochingIngredientNames(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody @Validated(ValidEsSearch3011.class) EsSearchDTO param,
			Errors errors) {
		log.debug("param:{}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			EsResultDTO data = searchService.searchCochingIngredientForSuggest(param);
			data.setVersion(API_VERSION);
			axRet.setResultData(data).set(ApiResultError.NO_ERROR);
		} catch (Exception e) {
			log.error("Es Search Error", e);
		}

		return axRet;
	}

	/**
	 * 성분 단건 조회
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @param errors
	 * @return
	 */
	@RequestMapping("/coching/ingredient/load.api")
	public ApiResult loadCochingIngredientById(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody @Validated(ValidEsSearch3012.class) EsSearchDTO param,
			Errors errors) {
		log.debug("param:{}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			EsResultDTO data = searchService.loadCochingIngredient(param);
			data.setVersion(API_VERSION);
			axRet.setResultData(data).set(ApiResultError.NO_ERROR);
		} catch (Exception e) {
			log.error("Es Search Error", e);
		}

		return axRet;
	}

	/**
	 * 필드검색
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @param errors
	 * @return
	 */
	@RequestMapping("/field.api")
	public ApiResult search(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody @Validated(ValidEsSearch0011.class) EsSearchDTO param,
			Errors errors) {
		log.debug("param:{}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			EsResultDTO data = searchService.searchByField(param);
			data.setVersion(API_VERSION);
			axRet.setResultData(data).set(ApiResultError.NO_ERROR);
		} catch (Exception e) {
			log.error("Es Search Error", e);
		}

		return axRet;
	}
}
