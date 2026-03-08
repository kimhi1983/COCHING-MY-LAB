package com.erns.coching.search.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erns.coching.common.Const;
import com.erns.coching.common.aop.ApiLogging;
import com.erns.coching.common.controller.AbstractApiController;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.model.PageInfo;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.search.domain.EsIngredientSearchDTO;
import com.erns.coching.search.domain.EsSearchDTO;
import com.erns.coching.search.domain.IngredientNationLimitSearchDTO;
import com.erns.coching.search.domain.SuggestionSearchDTO;
import com.erns.coching.search.domain.vg.ValidEsIngredientSearch0012;
import com.erns.coching.search.domain.vg.ValidEsSearch0011;
import com.erns.coching.search.domain.vg.ValidEsSearch1011;
import com.erns.coching.search.domain.vg.ValidEsSearch1012;
import com.erns.coching.search.domain.vg.ValidEsSearch2011;
import com.erns.coching.search.domain.vg.ValidEsSearch2012;
import com.erns.coching.search.domain.vg.ValidEsSearch4011;
import com.erns.coching.search.domain.vg.ValidEsSearch4012;
import com.erns.coching.search.domain.vg.ValidIngredientNationLimit0011;
import com.erns.coching.search.domain.vg.ValidSuggetion0011;
import com.erns.coching.search.service.EsSearchService;
import com.erns.coching.search.service.SearchService;
import com.erns.coching.sysProp.domain.SysPropVO;
import com.erns.coching.sysProp.service.SysPropService;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/search")
@PreAuthorize("permitAll")
public class SearchApiController extends AbstractApiController{

	@Autowired
	private EsSearchService esSearchService;

	@Autowired
	private SysPropService sysPropService;

	@Autowired
	private SearchService searchService;

	/**
	 * [ES] 환경설정에서 ES index 명을 가져온다.
	 * @param syskey
	 * @return
	 */
	private String getIndexName(String syskey) {
		SysPropVO loadProp = sysPropService.loadVo(syskey);
		if(null != loadProp){
			return loadProp.getSysValue();
		}

		return "unknownIndexName";
	}
	
	/**
	 * [ES]원료 검색
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.SEARCH_RAWS)
	@PostMapping("/raws.api")
	public void searchRaws(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody @Validated(ValidEsSearch1011.class) EsSearchDTO param,
			Errors errors) {

		param.setIndex(getIndexName(Const.ES_COCHING_RAW_ACTIVE_INDEX_SYSKEY));
		log.debug("param:{}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			sendBindError(response, errors, axRet);
			return;
		}

		// API Call
		try {
			esSearchService.searchTextForRaws(response, param);
		} catch (JsonProcessingException e) {
			log.error("searchRaws Error", e);
			sendError(response, axRet);
		}
	}

	/**
	 * [ES]원료 검색
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.SEARCH_RAWS)
	@PostMapping("/raws/main.api")
	public void searchRawsMain(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody @Validated(ValidEsSearch1011.class) EsSearchDTO param,
			Errors errors) {

		param.setIndex(getIndexName(Const.ES_COCHING_RAW_ACTIVE_INDEX_SYSKEY));
		log.debug("param:{}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			sendBindError(response, errors, axRet);
			return;
		}

		// API Call
		try {
			esSearchService.searchTextForRawsRandom(response, param);
		} catch (JsonProcessingException e) {
			log.error("searchRaws Error", e);
			sendError(response, axRet);
		}
	}

	/**
	 * [ES]원료 조회
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.SEARCH_RAW)
	@PostMapping("/raw.api")
	public void searchRaw(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody @Validated(ValidEsSearch1012.class) EsSearchDTO param,
			Errors errors) {

		param.setIndex(getIndexName(Const.ES_COCHING_RAW_ACTIVE_INDEX_SYSKEY));
		log.debug("param:{}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			sendBindError(response, errors, axRet);
			return;
		}

		// API Call
		try {
			esSearchService.searchIdForRaws(response, param);
		} catch (JsonProcessingException e) {
			log.error("searchRaw Error", e);
			sendError(response, axRet);
		}
	}

	/**
	 * [ES]상품 검색
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.SEARCH_PRODUCTS)
	@PostMapping("/products.api")
	public void searchProducts(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody @Validated(ValidEsSearch2011.class) EsSearchDTO param,
			Errors errors) {

		param.setIndex(getIndexName(Const.ES_HW_PROD_ACTIVE_INDEX_SYSKEY));
		log.debug("param:{}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			sendBindError(response, errors, axRet);
			return;
		}

		// API Call
		try {
			esSearchService.searchTextForHwProducts(response, param);
		} catch (JsonProcessingException e) {
			log.error("searchProducts Error", e);
			sendError(response, axRet);
		}
	}

	/**
	 * [ES]상품 조회
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.SEARCH_PRODUCT)
	@PostMapping("/product.api")
	public void searchProduct(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody @Validated(ValidEsSearch2012.class) EsSearchDTO param,
			Errors errors) {

		param.setIndex(getIndexName(Const.ES_HW_PROD_ACTIVE_INDEX_SYSKEY));
		log.debug("param:{}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			sendBindError(response, errors, axRet);
			return;
		}

		// API Call
		try {
			esSearchService.searchIdForHwProduct(response, param);
		} catch (JsonProcessingException e) {
			log.error("searchProduct Error", e);
			sendError(response, axRet);
		}
	}

	/**
	 * [ES]성분명 조회
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.SEARCH_INGREDIENTS)
	@PostMapping("/ingredients.api")
	public void searchIngredientNames(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody @Validated(ValidEsSearch0011.class) EsSearchDTO param,
			Errors errors) {

		param.setIndex(getIndexName(Const.ES_INGREDIENT_ACTIVE_INDEX_SYSKEY));
		log.debug("param:{}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			sendBindError(response, errors, axRet);
			return;
		}

		// API Call
		try {
			esSearchService.searchIngredientNames(response, param);
		} catch (JsonProcessingException e) {
			log.error("searchIngredientNames Error", e);
			sendError(response, axRet);
		}
	}

	/**
	 * [ES]성분 단건 조회
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.SEARCH_INGREDIENT)
	@PostMapping("/ingredient.api")
	public void searchIngredient(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody @Validated(ValidEsIngredientSearch0012.class) EsIngredientSearchDTO param,
			Errors errors) {

		param.setIndex(getIndexName(Const.ES_INGREDIENT_ACTIVE_INDEX_SYSKEY));
		log.debug("param:{}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			sendBindError(response, errors, axRet);
			return;
		}

		// API Call
		try {
			esSearchService.searchIngredient(response, param);
		} catch (JsonProcessingException e) {
			log.error("searchIngredient Error", e);
			sendError(response, axRet);
		}
	}


	/**
	 * [ES]코칭TV 검색
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.SEARCH_TVS)
	@PostMapping("/tvs.api")
	public void searchCochingTvs(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody @Validated(ValidEsSearch4011.class) EsSearchDTO param,
			Errors errors) {

		param.setIndex(getIndexName(Const.ES_COCHING_TV_ACTIVE_INDEX_SYSKEY));
		log.debug("param:{}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			sendBindError(response, errors, axRet);
			return;
		}

		// API Call
		try {
			esSearchService.searchTextForTvs(response, param);
		} catch (JsonProcessingException e) {
			log.error("searchRaws Error", e);
			sendError(response, axRet);
		}
	}

	/**
	 * [ES]코칭TV 조회
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.SEARCH_TV)
	@PostMapping("/tv.api")
	public void searchCochingTv(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody @Validated(ValidEsSearch4012.class) EsSearchDTO param,
			Errors errors) {

		param.setIndex(getIndexName(Const.ES_COCHING_TV_ACTIVE_INDEX_SYSKEY));
		log.debug("param:{}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			sendBindError(response, errors, axRet);
			return;
		}

		// API Call
		try {
			esSearchService.searchIdForTvs(response, param);
		} catch (JsonProcessingException e) {
			log.error("searchRaw Error", e);
			sendError(response, axRet);
		}
	}

	/**
	 * [DB] 검색제안어 검색
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.SEARCH_SUGGESTION)
	@PostMapping("/suggestion.api")
	public ApiResult searchSuggestion(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody @Validated(ValidSuggetion0011.class) SuggestionSearchDTO param,
			Errors errors) {

		log.debug("param:{}", param);

		final int LIMIT_MAX = 50;

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT, getLocale());
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		{	// 제안검색어 결과 수 제한
			int ingdLimit = param.getLimitIngd();
			int rawLimit = param.getLimitRaw();
			int prodBrandLimit = param.getLimitProdBrand();
			int ptNameLimit = param.getLimitPtName();

			if(ingdLimit > LIMIT_MAX) ingdLimit = LIMIT_MAX;
			if(rawLimit > LIMIT_MAX) rawLimit = LIMIT_MAX;			
			if(prodBrandLimit > LIMIT_MAX) prodBrandLimit = LIMIT_MAX;
			if(ptNameLimit > LIMIT_MAX) ptNameLimit = LIMIT_MAX;

			if(ingdLimit <= 0) ingdLimit = 10;
			if(rawLimit <= 0) rawLimit = 10;			
			if(prodBrandLimit <= 0) prodBrandLimit = 10;
			if(ptNameLimit <= 0) ptNameLimit = 10;

			param.setLimitIngd(ingdLimit);
			param.setLimitRaw(ingdLimit);
			param.setLimitProdBrand(ingdLimit);
			param.setLimitPtName(ingdLimit);
		}

		//API Call
		//요청과 상관없이 무조건 1페이지만 조회
		param.setPage(1);
		PageInfo pi = new PageInfo(0, param);
		List<Map<String, Object>> list = searchService.getSuggetionList(param);
		pi.setCurrentPage(list.size(), param.getPage());
		axRet.setSc(param)
			.setPageInfo(pi)
			.setList(list)
			.set(ApiResultError.NO_ERROR, getLocale());

		return axRet;		
	}

	/**
	 * [DB] 성분 국가별 제약사항
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.SEARCH_INGREDIENT_NATION_LIMIT)
	@PostMapping("/ingd/nationLimits.api")
	public ApiResult searchIngredientNationLimits(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody @Validated(ValidIngredientNationLimit0011.class) IngredientNationLimitSearchDTO param,
			Errors errors) {

		log.debug("param:{}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT, getLocale());
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		PageInfo pi = new PageInfo(0, param);
		Map<String, Object> limit = searchService.getNationLmitListForIngredient(param);
		axRet.setResultData(limit)
			.set(ApiResultError.NO_ERROR, getLocale());

		return axRet;	
	}
	
	/**
	 * [DB] 성분 국가별 수출 정보
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.SEARCH_INGREDIENT_NATION_EXP_LIMIT)
	@PostMapping("/ingd/nationExpLimits.api")
	public ApiResult searchIngredientNationExpLimits(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody @Validated(ValidIngredientNationLimit0011.class) IngredientNationLimitSearchDTO param,
			Errors errors) {

		log.debug("param:{}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT, getLocale());
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		PageInfo pi = new PageInfo(0, param);
		Map<String, Object> limit = searchService.getNationExpLmitListForIngredient(param);
		
		axRet.setResultData(limit)
			.set(ApiResultError.NO_ERROR, getLocale());

		return axRet;	
	}
}
