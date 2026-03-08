package com.erns.es.coching.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erns.es.coching.domain.EsCochingIngredientVO;
import com.erns.es.coching.domain.EsIngredientDTO;
import com.erns.es.coching.domain.EsIngredientSearchDTO;
import com.erns.es.coching.domain.vg.ValidEsIngredientSearch0011;
import com.erns.es.coching.domain.vg.ValidEsIngredientSearch0012;
import com.erns.es.coching.service.EsCochingIngredientService;
import com.erns.es.coching.service.EsIngredientService;
import com.erns.es.common.controller.AbstractApiController;
import com.erns.es.common.model.ApiResult;
import com.erns.es.common.model.PageInfo;
import com.erns.es.common.type.ApiResultError;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/es/ingredient")
//@PreAuthorize("isAuthenticated()")
public class SearchIngredientController extends AbstractApiController {
	
	@Autowired
	private EsIngredientService esIngredientService;
	
	@Autowired
	private EsCochingIngredientService esCochingIngredientService;
	
	
	/**
	 * 성분 매핑 상세
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
//	@ApiLogging
	@PostMapping("/mapping/load.api")
	public ApiResult loadMappingIngredient(
			HttpServletRequest request,
			@RequestBody @Validated(ValidEsIngredientSearch0012.class) EsIngredientSearchDTO param,
			Errors errors) {
		log.debug("param:{}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		EsIngredientDTO ingdData = esIngredientService.loadRawMapping(param);

		return axRet.setResultData(ingdData).set(ApiResultError.NO_ERROR);
	}
	
	/**
	 * 성분 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
//	@ApiLogging
	@PostMapping("/coching/list.api")
	public ApiResult loadIngredientList(
			HttpServletRequest request,
			@RequestBody @Validated(ValidEsIngredientSearch0011.class) EsIngredientSearchDTO param,
			Errors errors) {
		log.debug("param:{}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		PageInfo pi = new PageInfo(0, param);
    	int totalItem = esCochingIngredientService.getListCount(param);
    	List<EsCochingIngredientVO> list = null;
    	if(totalItem <= 0) {
    		list = new ArrayList<EsCochingIngredientVO>();
    	}else {
    		list = esCochingIngredientService.getList(param);
    		pi.setCurrentPage(totalItem, param.getPage());
    	}

		axRet.setPageInfo(pi).setSc(param).setList(list);
    	return axRet.set(ApiResultError.NO_ERROR);
	}
	
	/**
	 * 성분 상세
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
//	@ApiLogging
	@PostMapping("/coching/load.api")
	public ApiResult loadIngredient(
			HttpServletRequest request,
			@RequestBody @Validated(ValidEsIngredientSearch0012.class) EsIngredientSearchDTO param,
			Errors errors) {
		log.debug("param:{}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		EsCochingIngredientVO ingdData = esCochingIngredientService.load(param);

		return axRet.setResultData(ingdData).set(ApiResultError.NO_ERROR);
	}

}
