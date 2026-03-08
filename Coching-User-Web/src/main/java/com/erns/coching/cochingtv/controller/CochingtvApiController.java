package com.erns.coching.cochingtv.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erns.coching.cochingtv.domain.CochingtvSearchDTO;
import com.erns.coching.cochingtv.domain.vg.ValidCochingtv0011;
import com.erns.coching.cochingtv.domain.vg.ValidCochingtv0012;
import com.erns.coching.cochingtv.service.CochingtvService;
import com.erns.coching.common.aop.ApiLogging;
import com.erns.coching.common.controller.AbstractApiController;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.model.PageInfo;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserLogType;

import lombok.extern.slf4j.Slf4j;

/**
*
* <p>코칭TV관리 API Controller</p>
*
* @author Hunwoo Park
*
*/
@Slf4j
@RestController
@RequestMapping("/api/cochingtv")
@PreAuthorize("permitAll")
public class CochingtvApiController extends AbstractApiController {
	
	@Autowired
	protected CochingtvService cochingtvService;
	
	/**
	 * 코칭TV 상세
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.COCHINGTV_LIST)
	@PostMapping("/get.api")
	public ApiResult getCochingtv(
			HttpServletRequest request,
			@RequestBody @Validated(ValidCochingtv0012.class) CochingtvSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT, getLocale());
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		Map<String, Object> cochingtv = cochingtvService.load(param);
		axRet.setResultData(cochingtv)
			.set(ApiResultError.NO_ERROR, getLocale());
		return axRet;
	}
	
	/**
	 * 코칭TV 추천영상 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.COCHINGTV_LIST)
	@PostMapping("/recommendedList.api")
	public ApiResult getCochingtvList(
            HttpServletRequest request,
            @RequestBody @Validated(ValidCochingtv0011.class) CochingtvSearchDTO param,
            Errors errors) {

        ApiResult axRet = new ApiResult();
        if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}
        
		//API Call
		PageInfo pi = new PageInfo(0, param);
		int totalItem = cochingtvService.getListCount(param);
		List<Map<String, Object>> list = null;
		if(totalItem <= 0) {
			list = new ArrayList<>();
		}else {
			list = cochingtvService.getList(param);
		}
		
        pi.setCurrentPage(totalItem, param.getPage());
		axRet.setSc(param)
			.setPageInfo(pi)
			.setList(list)
			.set(ApiResultError.NO_ERROR, getLocale());

        return axRet;
	}
	
	/**
	 * Youtube API로 정보 가져오기
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.COCHINGTV_YOUTUBE_GET)
	@PostMapping("/getYoutubeInfo.api")
	public ApiResult getYoutubeInfo(
            HttpServletRequest request,
            @RequestBody @Validated(ValidCochingtv0011.class) CochingtvSearchDTO param,
            Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT, getLocale());
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		Map<String, Object> ytInfo = cochingtvService.getYoutubeInfo(param);
		axRet.setResultData(ytInfo)
			.set(ApiResultError.NO_ERROR, getLocale());
		return axRet;
	}

}
