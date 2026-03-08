package com.erns.coching.banner.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.erns.coching.banner.domain.BannerAdMultipleSetDTO;
import com.erns.coching.banner.domain.BannerAdSearchDTO;
import com.erns.coching.banner.domain.BannerAdSetDTO;
import com.erns.coching.banner.domain.BannerAdVO;
import com.erns.coching.banner.domain.vg.ValidBanner0011;
import com.erns.coching.banner.domain.vg.ValidBanner0012;
import com.erns.coching.banner.domain.vg.ValidBanner0020;
import com.erns.coching.banner.domain.vg.ValidBanner0030;
import com.erns.coching.banner.domain.vg.ValidBanner0031;
import com.erns.coching.banner.domain.vg.ValidBanner0032;
import com.erns.coching.banner.domain.vg.ValidBanner0040;
import com.erns.coching.banner.service.BannerAdService;
import com.erns.coching.common.aop.ApiLogging;
import com.erns.coching.common.controller.AbstractApiController;
import com.erns.coching.common.file.repository.FileRepository;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.model.PageInfo;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.login.domain.LoginUserDTO;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * <p>
 * 배너 광고 관리 API Controller
 * </p>
 *
 * @author Hunwoo Park
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/banner/ad")
@PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_ADMIN')")
public class BannerAdApiContoller extends AbstractApiController {

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
		}

		axRet.setPageInfo(pi)
			.setSc(param)
			.setList(list)
			.set(ApiResultError.NO_ERROR);
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
			.set(ApiResultError.NO_ERROR);
		
		return axRet;
	}

	/**
	 * 배너 등록
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.BANNER_ADD)
	@PostMapping("/new.api")
	public ApiResult insertBanner(
			HttpServletRequest request,
			@Validated(ValidBanner0020.class) BannerAdSetDTO param,
			Errors errors) {

		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			LoginUserDTO loginUser = getLoginedUserObject();

			fileRepo.printFilesInfo(request);
			List<MultipartFile> getUploadFiles = fileRepo.getUploadFiles(request, FILE_INPUT_NAME);
			log.debug("Files:{}", getUploadFiles.size());

			String paramDelFileIds = ServletRequestUtils.getStringParameter(request, "delFileIds", "");
			log.debug("delFiles:{}", paramDelFileIds);

			BannerAdVO banner = BannerAdVO.AddBannerBuilder()
					.addUser(loginUser)
					.fromDto(param)
					.build();

			Map<String, Object> retModel = bannerAdService.insert(banner, getUploadFiles);

			if (retModel != null) {
				axRet.setResultData(retModel)
						.set(ApiResultError.NO_ERROR);
			}

		} catch (Exception e) {
			log.error("error", e);
			return axRet.set(ApiResultError.ATTACH_ERROR);
		}

		return axRet;
	}
	
	/**
	 * 배너 수정
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.BANNER_UDT)
	@PostMapping("/set.api")
	public ApiResult updateBanner(
			HttpServletRequest request,
			@Validated(ValidBanner0030.class) BannerAdSetDTO param,
			Errors errors) {

		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			fileRepo.printFilesInfo(request);
			List<MultipartFile> getUploadFiles = fileRepo.getUploadFiles(request, FILE_INPUT_NAME);
			log.debug("Files:{}", getUploadFiles.size());

			String paramDelFileIds = ServletRequestUtils.getStringParameter(request, "delFileIds", "");
			log.debug("delFiles:{}", paramDelFileIds);

			LoginUserDTO loginUser = getLoginedUserObject();

			BannerAdVO banner = BannerAdVO.SetBannerBuilder()
					.updateUser(loginUser)
					.fromDto(param)
					.bannerSeq(param.getBannerSeq())
					.build();

			Map<String, Object> retModel = bannerAdService.update(banner, getUploadFiles, paramDelFileIds);

			if (retModel != null) {
				axRet.setResultData(retModel)
						.set(ApiResultError.NO_ERROR);
			}
		} catch (Exception e) {
			log.error("error", e);
			return axRet.set(ApiResultError.ATTACH_ERROR);
		}

		return axRet;
	}
	
	/**
	 * 배너 삭제
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.BANNER_DEL)
	@PostMapping("/del.api")
	public ApiResult deleteBanner(
			HttpServletRequest request,
			@RequestBody @Validated(ValidBanner0040.class) BannerAdSearchDTO param,
			Errors errors) {

		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			// API Call
			LoginUserDTO loginUser = getLoginedUserObject();
			BannerAdVO bannerVo = BannerAdVO.builder()
					.bannerMstCd(param.getBannerMstCd())
					.bannerSeq(param.getBannerSeq())
					.delYn(param.getDelYn())
					.chnr(loginUser.getSeq())
					.build();
			
			if (bannerAdService.updateDelYn(bannerVo) > 0) {
				axRet.set(ApiResultError.NO_ERROR);
			}

		} catch (Exception e) {
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}
	
	/**
	 * 배너 상태 설정
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.BANNER_UDT_STATE)
	@PostMapping("/setState.api")
	public ApiResult setBannerState(
			HttpServletRequest request,
			@RequestBody @Validated(ValidBanner0032.class) BannerAdMultipleSetDTO param,
			Errors errors) {

		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			// API Call
			LoginUserDTO loginUser = getLoginedUserObject();

			List<BannerAdVO> setList = new ArrayList<>();
			for (BannerAdSetDTO setDto : param.getList()) {
				if (setDto.getBannerSeq() <= 0) {
					continue;
				}

				BannerAdVO bannerVo = BannerAdVO.builder()
						.bannerMstCd(setDto.getBannerMstCd())
						.bannerSeq(setDto.getBannerSeq())
						.useYn(setDto.getUseYn())
						.dispYn(setDto.getDispYn())
						.chnr(loginUser.getSeq())
						.build();

				setList.add(bannerVo);
			}

			int ret = bannerAdService.updateState(setList);
			axRet.setResultData(ret)
					.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}

	/**
	 * 배너 순서 설정
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.BANNER_UDT_ORDER)
	@PostMapping("/setOrders.api")
	public ApiResult setBannerOrders(
			HttpServletRequest request,
			@RequestBody @Validated(ValidBanner0031.class) BannerAdMultipleSetDTO param,
			Errors errors) {

		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			// API Call
			LoginUserDTO loginUser = getLoginedUserObject();

			List<BannerAdVO> setList = new ArrayList<>();
			for (BannerAdSetDTO setDto : param.getList()) {
				if (setDto.getBannerSeq() <= 0) {
					continue;
				}

				BannerAdVO bannerVo = BannerAdVO.builder()
						.bannerMstCd(setDto.getBannerMstCd())
						.bannerSeq(setDto.getBannerSeq())
						.sortOrd(setDto.getSortOrd())
						.chnr(loginUser.getSeq())
						.build();

				setList.add(bannerVo);
			}

			int ret = bannerAdService.updateOrder(setList);
			axRet.setResultData(ret)
					.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}
	
	/**
	 * 배너 삭제
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.BANNER_DEL)	
	@PostMapping("/dels.api")
	public ApiResult deleteBanners(
			HttpServletRequest request,
			@RequestBody @Validated(ValidBanner0040.class) BannerAdMultipleSetDTO param,
			Errors errors) {

		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			// API Call
			LoginUserDTO loginUser = getLoginedUserObject();
			
			List<BannerAdVO> delList = new ArrayList<>();
			for (BannerAdSetDTO setDto : param.getList()) {
				BannerAdVO bannerVo = BannerAdVO.builder()
						.bannerMstCd(setDto.getBannerMstCd())
						.bannerSeq(setDto.getBannerSeq())
						.delYn(setDto.getDelYn())
						.chnr(loginUser.getSeq())
						.build();
				delList.add(bannerVo);
			}

			int ret = bannerAdService.updateDelYn(delList);
			axRet.setResultData(ret)
					.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}
	
}
