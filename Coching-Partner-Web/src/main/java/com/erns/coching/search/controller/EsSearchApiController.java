package com.erns.coching.search.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
import com.erns.coching.search.domain.vg.ValidEsIngredientSearch0012;
import com.erns.coching.search.domain.vg.ValidEsSearch0011;
import com.erns.coching.search.domain.vg.ValidEsSearch1011;
import com.erns.coching.search.domain.vg.ValidEsSearch1012;
import com.erns.coching.search.domain.vg.ValidEsSearch2011;
import com.erns.coching.search.domain.vg.ValidEsSearch2012;
import com.erns.coching.search.service.CochingEsIngredientDicService;
import com.erns.coching.search.service.EsSearchService;
import com.erns.coching.sysProp.domain.SysPropVO;
import com.erns.coching.sysProp.service.SysPropService;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/search")
@PreAuthorize("isAuthenticated()")
public class EsSearchApiController extends AbstractApiController{

	@Autowired
	private EsSearchService esSearchService;

	@Autowired
	private CochingEsIngredientDicService cochingEsIngredientDicService;

	@Autowired
	private SysPropService sysPropService;

	@Autowired
	protected ResourceLoader resourceLoader;

	@Value("${system.file.uploadpath}")
	private String uploadPath;

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
	 * [ES]성분명 DB
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.SEARCH_INGREDIENT_ALL)
	@PostMapping("/ingredientAll.api")
	public ApiResult getIngredientAll(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody @Validated(ValidEsSearch0011.class) EsIngredientSearchDTO param,
			Errors errors) {

		log.debug("param:{}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		PageInfo pi = new PageInfo(0, param);
		int totalItem = cochingEsIngredientDicService.getListCount(param);
		List<Map<String, Object>> list = null;
		if(totalItem <= 0) {
			list = new ArrayList<Map<String, Object>>();
		}else {
			list = cochingEsIngredientDicService.getList(param);
		}
    pi.setCurrentPage(totalItem, param.getPage());

		axRet.setSc(param)
			.setPageInfo(pi)
			.setList(list)
			.set(ApiResultError.NO_ERROR, getLocale());

    return axRet;
	}

	/**
	 * 성분사전
	 * @param request
	 * @param response
	 * @param locale
	 * @return
	 * @throws IOException
	 */	
	@ApiLogging(logType = UserLogType.SEARCH_INGREDIENT_S_ALL)
	@PreAuthorize("permitAll")
	@GetMapping(value = "/static/ingredientAll.api", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<InputStreamResource> getStaticIngredientAll(
			HttpServletRequest request, HttpServletResponse response) throws IOException {

		// 캐시 헤더 설정
		HttpHeaders headers = new HttpHeaders();
		// n시간 캐시
		int cacheHour = 12;
		headers.setCacheControl(CacheControl.maxAge(cacheHour, TimeUnit.HOURS).getHeaderValue());
		headers.setContentType(MediaType.APPLICATION_JSON); // JSON 컨텐츠 타입 설정
		
		String filePath = String.format("classpath:/api/es/search/ingd/coching_ingredient.json");
		log.info("load file:{}", filePath);
		Resource res = resourceLoader.getResource(filePath);
		

		return ResponseEntity.ok().headers(headers).body(new InputStreamResource(res.getInputStream()));
	}
}
