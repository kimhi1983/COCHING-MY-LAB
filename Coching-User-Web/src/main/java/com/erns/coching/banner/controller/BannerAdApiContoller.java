package com.erns.coching.banner.controller;

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

import com.erns.coching.banner.domain.BannerAdSearchDTO;
import com.erns.coching.banner.domain.vg.ValidBanner0011;
import com.erns.coching.banner.domain.vg.ValidBanner0012;
import com.erns.coching.banner.service.BannerAdService;
import com.erns.coching.common.aop.ApiLogging;
import com.erns.coching.common.controller.AbstractApiController;
import com.erns.coching.common.file.repository.FileRepository;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.model.PageInfo;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserLogType;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * <p>배너관리 API Controller</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/banner/ad")
@PreAuthorize("permitAll")
public class BannerAdApiContoller extends AbstractApiController{
	
	public static final String FILE_INPUT_NAME = "banner_files";
	
	@Autowired
	private BannerAdService bannerAdService;
	
	@Autowired
	private FileRepository fileRepo;
	
	/**
	 * 배너 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.BANNER_LIST)
	@PostMapping("/list.api")    
	public ApiResult getBannerList(
            HttpServletRequest request,
            @RequestBody @Validated(ValidBanner0011.class) BannerAdSearchDTO param,
            Errors errors) {
                
		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);        
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}
	
		// API Call
		PageInfo pi = new PageInfo(0, param);
		int totalItem = bannerAdService.getListCount(param);
		List<Map<String, Object>> list = null;
		if (totalItem <= 0) {
			list = new ArrayList<>();
		} else {
			list = bannerAdService.getList(param);
			pi.setCurrentPage(totalItem, param.getPage());
			
			//파일 정보 추가
			for (Map<String, Object> banner : list) {
				long bannerSeq = Long.parseLong(banner.get("bannerSeq").toString());
				param.setBannerSeq(bannerSeq);
				List<Map<String, Object>> fileList = bannerAdService.getFiles(param);
				if (fileList != null && fileList.size() > 0) {
					banner.put("file", fileList.get(0));
				}
			}
			
		}

		axRet.setPageInfo(pi)
			.setSc(param)
			.setList(list)
			.set(ApiResultError.NO_ERROR, getLocale());
		return axRet;
	}
	

	/**
	 * 배너 상세
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.BANNER_GET)
	@PostMapping("/get.api")    
	public ApiResult getBanner(
			HttpServletRequest request,
			@RequestBody @Validated(ValidBanner0012.class) BannerAdSearchDTO param,
			Errors errors) {
		
		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);        
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}
		
		//API Call
		Map<String, Object> banner = bannerAdService.load(param);
		List<Map<String, Object>> fileList = bannerAdService.getFiles(param);
		if(fileList != null && !fileList.isEmpty()) {
			banner.put("file", fileList.get(0));
		}
		
		axRet.setResultData(banner)
			.set(ApiResultError.NO_ERROR, getLocale());
		
		return axRet;
	}
	
	/**
	 * 배너 클릭
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.BANNER_CLICK)
	@PostMapping("/click.api")    
	public ApiResult setBannerClick(
			HttpServletRequest request,
			@RequestBody @Validated(ValidBanner0012.class) BannerAdSearchDTO param,
			Errors errors) {
		
		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);    
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}
		
		if(bannerAdService.updateClicks(param) > 0) {
			axRet.set(ApiResultError.NO_ERROR, getLocale());
		}
 		return axRet;
	}
	
}
