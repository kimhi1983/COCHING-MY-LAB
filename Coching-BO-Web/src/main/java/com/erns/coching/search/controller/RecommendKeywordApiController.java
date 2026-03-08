package com.erns.coching.search.controller;

import java.util.ArrayList;
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
import com.erns.coching.login.domain.LoginUserDTO;
import com.erns.coching.search.domain.RecommendKeywordMultipleSetDTO;
import com.erns.coching.search.domain.RecommendKeywordSearchDTO;
import com.erns.coching.search.domain.RecommendKeywordSetDTO;
import com.erns.coching.search.domain.RecommendKeywordVO;
import com.erns.coching.search.domain.vg.ValidRecommendKeyword0011;
import com.erns.coching.search.domain.vg.ValidRecommendKeyword0012;
import com.erns.coching.search.domain.vg.ValidRecommendKeyword0021;
import com.erns.coching.search.domain.vg.ValidRecommendKeyword0040;
import com.erns.coching.search.service.RecommendKeywordService;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 추천 키워드 API Controller
 * </p>
 * 
 * @author Hunwoo Park
 */
@Slf4j
@RestController
@RequestMapping("/api/search/recommendKeyword")
@PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_ADMIN')")
public class RecommendKeywordApiController extends AbstractApiController {

	@Autowired
	protected RecommendKeywordService recommendKeywordService;

	/**
	 * 추천 키워드 목록
	 * 
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

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		// API Call
		PageInfo pi = new PageInfo(0, param);
		int totalItem	=	recommendKeywordService.getListCount(param);
		List<Map<String, Object>> list = null;
		if(totalItem <=	0){
			list = new ArrayList<>();
		}else{
			list = recommendKeywordService.getList(param);
		}
		pi.setCurrentPage(totalItem, param.getPage());

		axRet.setSc(param)
				.setPageInfo(pi)
				.setList(list)
				.set(ApiResultError.NO_ERROR);

		return axRet;
	}

	/**
	 * 추천 키워드 로드
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RECOMMEND_KEYWORD_GET)
	@RequestMapping("/get.api")
	public ApiResult getItem(
			HttpServletRequest request,
			@RequestBody @Validated(ValidRecommendKeyword0012.class) RecommendKeywordSearchDTO param,
			Errors errors) {

		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) {// 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			// API Call
			RecommendKeywordVO retItem = recommendKeywordService.load(param.getSeq());
			axRet.setResultData(retItem)
				.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}

	/**
	 * 추천 키워드 등록/설정
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RECOMMEND_KEYWORD_ADD)
	@RequestMapping("/set.api")
	public ApiResult setItems(
			HttpServletRequest request,
			@RequestBody @Validated(ValidRecommendKeyword0021.class) RecommendKeywordSetDTO param,
			Errors errors) {

		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) {// 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			// API Call
			LoginUserDTO loginUser = getLoginedUserObject();
			param.setRgtr(loginUser.getSeq());
			param.setChnr(loginUser.getSeq());

			boolean regMode = param.getSeq() <= 0;
			RecommendKeywordVO mergeItemVo = null;
			
			if(regMode){
				mergeItemVo = RecommendKeywordVO.AddRecommendKeywordBuilder()
					.fromDto(param)
					.build();
			}else{
				mergeItemVo = RecommendKeywordVO.EditRecommendKeywordBuilder()
					.fromDto(param)
					.seq(param.getSeq())
					.build();
			}
			int ret = recommendKeywordService.merge(mergeItemVo);

			axRet.setResultData(ret)
				.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}

	/**
	 * 추천 키워드 등록/설정
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RECOMMEND_KEYWORD_UDT)
	@RequestMapping("/sets.api")
	public ApiResult multipleSetItems(
			HttpServletRequest request,
			@RequestBody @Validated(ValidRecommendKeyword0021.class) RecommendKeywordMultipleSetDTO param,
			Errors errors) {

		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) {// 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			// API Call
			LoginUserDTO loginUser = getLoginedUserObject();

			List<RecommendKeywordVO> setList = new ArrayList<>();
			for(RecommendKeywordSetDTO setDto : param.getList()){
				setDto.setRgtr(loginUser.getSeq());
				setDto.setChnr(loginUser.getSeq());

				boolean regMode = setDto.getSeq() <= 0;
				RecommendKeywordVO mergeItemVo = null;
				
				if(regMode){
					mergeItemVo = RecommendKeywordVO.AddRecommendKeywordBuilder()
						.fromDto(setDto)
						.build();
				}else{
					mergeItemVo = RecommendKeywordVO.EditRecommendKeywordBuilder()
						.fromDto(setDto)
						.seq(setDto.getSeq())
						.build();
				}
				setList.add(mergeItemVo);
			}
			int ret = recommendKeywordService.merge(setList);			
			axRet.setResultData(ret)
				.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}

	/**
	 * 추천 키워드 삭제
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RECOMMEND_KEYWORD_DEL)
	@RequestMapping("/del.api")
	public ApiResult deleteItem(
			HttpServletRequest request,
			@RequestBody @Validated(ValidRecommendKeyword0040.class) RecommendKeywordSetDTO param,
			Errors errors) {

		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) {// 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			// API Call
			int ret = recommendKeywordService.delete(param.getSeq());

			axRet.setResultData(ret)
				.set(ApiResultError.NO_ERROR);
			
		} catch (Exception e) {
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}

	/**
	 * 추천 키워드 삭제
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RECOMMEND_KEYWORD_DEL)
	@RequestMapping("/dels.api")
	public ApiResult deleteMultipleItems(
			HttpServletRequest request,
			@RequestBody @Validated(ValidRecommendKeyword0040.class) RecommendKeywordMultipleSetDTO param,
			Errors errors) {

		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) {// 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			// API Call
			List<Long> delList = new ArrayList<>();
			for(RecommendKeywordSetDTO setDto : param.getList()){
				delList.add(setDto.getSeq());
			}
			
			int ret = recommendKeywordService.delete(delList);
			axRet.setResultData(ret)
				.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}

	/**
	 * 추천 키워드 순서 저장
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RECOMMEND_KEYWORD_UDT_ORDER)
	@RequestMapping("/setOrders.api")
	public ApiResult multipleSetItemOrders(
			HttpServletRequest request,
			@RequestBody @Validated(ValidRecommendKeyword0021.class) RecommendKeywordMultipleSetDTO param,
			Errors errors) {

		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) {// 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			// API Call
			LoginUserDTO loginUser = getLoginedUserObject();

			List<RecommendKeywordVO> setList = new ArrayList<>();
			for(RecommendKeywordSetDTO setDto : param.getList()){
				setDto.setRgtr(loginUser.getSeq());
				setDto.setChnr(loginUser.getSeq());

				boolean regMode = setDto.getSeq() <= 0;
				RecommendKeywordVO mergeItemVo = null;
				
				if(regMode){
					continue; //기 등록만 수정한다.
				}else{
					mergeItemVo = RecommendKeywordVO.EditRecommendKeywordBuilder()
						.fromDto(setDto)
						.seq(setDto.getSeq())
						.build();
				}
				setList.add(mergeItemVo);
			}
			int ret = recommendKeywordService.updateSortOrder(setList);			
			axRet.setResultData(ret)
				.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}

}
