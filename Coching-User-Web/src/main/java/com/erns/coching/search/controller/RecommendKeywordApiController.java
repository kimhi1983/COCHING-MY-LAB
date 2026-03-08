package com.erns.coching.search.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erns.coching.common.aop.ApiLogging;
import com.erns.coching.common.controller.AbstractApiController;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.model.PageInfo;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.search.domain.RecommendKeywordSearchDTO;
import com.erns.coching.search.domain.vg.ValidRecommendKeyword0011;
import com.erns.coching.search.service.RecommendKeywordService;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>추천 키워드 API Controller</p>
 * 
 * @author Hunwoo Park
 */
@Slf4j
@RestController
@RequestMapping("/api/search/recommendKeyword")
@PreAuthorize("permitAll")
public class RecommendKeywordApiController extends AbstractApiController {

	@Autowired
	protected RecommendKeywordService recommendKeywordService;

	/**
	 * 추천 키워드 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RECOMMEND_KEYWORD_LIST)
	@RequestMapping(value = "/list.api")
	public ApiResult list(
			HttpServletRequest request,
			@RequestBody @Validated(ValidRecommendKeyword0011.class) RecommendKeywordSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT, getLocale());
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		//요청과 상관없이 무조건 1페이지만 조회
		param.setPage(1);
		PageInfo pi = new PageInfo(0, param);
		List<Map<String, Object>> list = recommendKeywordService.getList(param);
		pi.setCurrentPage(list.size(), param.getPage());
		axRet.setSc(param)
			.setPageInfo(pi)
			.setList(list)
			.set(ApiResultError.NO_ERROR, getLocale());

		return axRet;		
	}	
}
