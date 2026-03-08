package com.erns.coching.popup.controller;

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

import com.erns.coching.common.aop.ApiLogging;
import com.erns.coching.common.controller.AbstractApiController;
import com.erns.coching.common.file.repository.FileRepository;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.model.PageInfo;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.login.domain.LoginUserDTO;
import com.erns.coching.popup.domain.PopupMasterMultipleSetDTO;
import com.erns.coching.popup.domain.PopupMasterSearchDTO;
import com.erns.coching.popup.domain.PopupMasterSetDTO;
import com.erns.coching.popup.domain.PopupMasterVO;
import com.erns.coching.popup.domain.PopupMultipleSetDTO;
import com.erns.coching.popup.domain.PopupSearchDTO;
import com.erns.coching.popup.domain.PopupSetDTO;
import com.erns.coching.popup.domain.PopupVO;
import com.erns.coching.popup.domain.vg.ValidPopup0011;
import com.erns.coching.popup.domain.vg.ValidPopup0012;
import com.erns.coching.popup.domain.vg.ValidPopup0020;
import com.erns.coching.popup.domain.vg.ValidPopup0030;
import com.erns.coching.popup.domain.vg.ValidPopup0031;
import com.erns.coching.popup.domain.vg.ValidPopup0032;
import com.erns.coching.popup.domain.vg.ValidPopup0040;
import com.erns.coching.popup.domain.vg.ValidPopupMaster0011;
import com.erns.coching.popup.domain.vg.ValidPopupMaster0012;
import com.erns.coching.popup.domain.vg.ValidPopupMaster0032;
import com.erns.coching.popup.service.PopupMasterService;
import com.erns.coching.popup.service.PopupService;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * <p>
 * 팝업관리 API Controller
 * </p>
 *
 * @author Hunwoo Park
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/popup")
@PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_ADMIN')")
public class PopupApiContoller extends AbstractApiController {

	public static final String FILE_INPUT_NAME = "popup_files";

	@Autowired
	private PopupMasterService popupMasterService;

	@Autowired
	private PopupService popupService;

	@Autowired
	private FileRepository fileRepo;

	/**
	 * 팝업 마스터 목록
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.POPUP_MST_LIST)
	@PostMapping("/mst/list.api")
	public ApiResult getPopupMasterList(
			HttpServletRequest request,
			@RequestBody @Validated(ValidPopupMaster0011.class) PopupMasterSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		// API Call
		PageInfo pi = new PageInfo(0, param);
		int totalItem = popupMasterService.getListCount(param);
		List<Map<String, Object>> list = null;
		if (totalItem <= 0) {
			list = new ArrayList<>();
		} else {
			list = popupMasterService.getList(param);			
		}
		pi.setCurrentPage(totalItem, param.getPage());

		axRet.setPageInfo(pi)
			.setSc(param)
			.setList(list)
			.set(ApiResultError.NO_ERROR);

		return axRet;
	}

	/**
	 * 팝업 마스터 상세
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.POPUP_MST_GET)
	@PostMapping("/mst/get.api")
	public ApiResult getMasterPopup(
			HttpServletRequest request,
			@RequestBody @Validated(ValidPopupMaster0012.class) PopupSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		// API Call
		Map<String, Object> popupMaster = popupMasterService.load(param);
		axRet.setResultData(popupMaster)
			.set(ApiResultError.NO_ERROR);

		return axRet;
	}
	
	
	/**
	 * 팝업마스터 상태 설정
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.POPUP_MST_UDT_STATE)
	@PostMapping("/mst/setState.api")
	public ApiResult setPopupState(
			HttpServletRequest request,
			@RequestBody @Validated(ValidPopupMaster0032.class) PopupMasterMultipleSetDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}
			
		try {
			LoginUserDTO loginUser = getLoginedUserObject();

			List<PopupMasterVO> setList = new ArrayList<>();
			for (PopupMasterSetDTO setDto : param.getList()) {
				if ("".equals(setDto.getPopupMstCd())){
					continue;
				}
				
				PopupMasterVO popupMaster = PopupMasterVO.builder()
						.popupMstCd(setDto.getPopupMstCd())
						.useYn(setDto.getUseYn())
						.popupMstDesc(setDto.getPopupMstDesc())
						.chnr(loginUser.getSeq())
						.build();

				setList.add(popupMaster);
			}

			int ret = popupMasterService.updateState(setList);
			axRet.setResultData(ret)
				.set(ApiResultError.NO_ERROR);
		} catch (Exception e) {
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}

	/**
	 * 팝업 목록
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.POPUP_LIST)
	@PostMapping("/list.api")
	public ApiResult getPopupList(
			HttpServletRequest request,
			@RequestBody @Validated(ValidPopup0011.class) PopupSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		// API Call
		PageInfo pi = new PageInfo(0, param);
		int totalItem = popupService.getListCount(param);
		List<Map<String, Object>> list = null;
		if (totalItem <= 0) {
			list = new ArrayList<>();
		} else {
			list = popupService.getList(param);			
		}
		pi.setCurrentPage(totalItem, param.getPage());

		axRet.setPageInfo(pi)
			.setSc(param)
			.setList(list)
			.set(ApiResultError.NO_ERROR);

		return axRet;
	}

	/**
	 * 팝업 상세
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.POPUP_GET)
	@PostMapping("/get.api")
	public ApiResult getPopup(
			HttpServletRequest request,
			@RequestBody @Validated(ValidPopup0012.class) PopupSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			// API Call
			Map<String, Object> popup = popupService.load(param);
			List<Map<String, Object>> fileList = popupService.getFiles(param);
			if (fileList != null && fileList.size() > 0) {
				popup.put("file", fileList.get(0));
			}

			axRet.setResultData(popup)
				.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}

	/**
	 * 팝업 등록
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.POPUP_ADD)
	@PostMapping("/new.api")
	public ApiResult insertPopup(
			HttpServletRequest request,
			@Validated(ValidPopup0020.class) PopupSetDTO param,
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

			PopupVO popup = PopupVO.AddPopupBuilder()
					.addUser(loginUser)
					.fromDto(param)
					.build();

			Map<String, Object> retModel = popupService.insert(popup, getUploadFiles);

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
	 * 팝업 수정
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.POPUP_UDT)
	@PostMapping("/set.api")
	public ApiResult updatePopup(
			HttpServletRequest request,
			@Validated(ValidPopup0030.class) PopupSetDTO param,
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

			PopupVO popup = PopupVO.SetPopupBuilder()
					.updateUser(loginUser)
					.fromDto(param)
					.popupSeq(param.getPopupSeq())
					.build();

			Map<String, Object> retModel = popupService.update(popup, getUploadFiles, paramDelFileIds);

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
	 * 팝업 상태태 설정
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.POPUP_UDT_STATE)
	@PostMapping("/setState.api")
	public ApiResult setPopupState(
			HttpServletRequest request,
			@RequestBody @Validated(ValidPopup0032.class) PopupMultipleSetDTO param,
			Errors errors) {
		
		//log.debug("param : {}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			LoginUserDTO loginUser = getLoginedUserObject();

			List<PopupVO> setList = new ArrayList<>();
			for (PopupSetDTO setDto : param.getList()) {
				if (setDto.getPopupSeq() <= 0){
					continue;
				}

				PopupVO popup = PopupVO.builder()
						.popupMstCd(setDto.getPopupMstCd())
						.popupSeq(setDto.getPopupSeq())
						.useYn(setDto.getUseYn())
						.delYn(setDto.getDelYn())
						.chnr(loginUser.getSeq())
						.build();

				setList.add(popup);
			}

			int ret = popupService.updateState(setList);
			axRet.setResultData(ret)
				.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}

	/**
	 * 팝업 순서 설정
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.POPUP_UDT_ORDER)
	@PostMapping("/setOrders.api")
	public ApiResult setPopupOrders(
			HttpServletRequest request,
			@RequestBody @Validated(ValidPopup0031.class) PopupMultipleSetDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			LoginUserDTO loginUser = getLoginedUserObject();

			List<PopupVO> setList = new ArrayList<>();
			for (PopupSetDTO setDto : param.getList()) {
				if (setDto.getPopupSeq() <= 0){
					continue;
				}

				PopupVO popup = PopupVO.builder()
						.popupMstCd(setDto.getPopupMstCd())
						.popupSeq(setDto.getPopupSeq())
						.sortOrd(setDto.getSortOrd())
						.chnr(loginUser.getSeq())
						.build();

				setList.add(popup);
			}

			int ret = popupService.updateOrder(setList);
			axRet.setResultData(ret)
				.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}

	/**
	 * 팝업 삭제 설정
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.POPUP_DEL)
	@PostMapping("/del.api")
	public ApiResult deletePopup(
			HttpServletRequest request,
			@RequestBody @Validated(ValidPopup0040.class) PopupSetDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			// API Call		
			if(popupService.delete(param.getPopupSeq()) > 0){
				axRet.set(ApiResultError.NO_ERROR);
			}
		} catch (Exception e){
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}

	/**
	 * 팝업 삭제
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.POPUP_DEL)
	@PostMapping("/dels.api")
	public ApiResult deletePopups(
			HttpServletRequest request,
			@RequestBody @Validated(ValidPopup0040.class) PopupMultipleSetDTO param,
			Errors errors) {

		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			// API Call
			List<Long> delList = new ArrayList<>();
			for (PopupSetDTO setDto : param.getList()) {
				delList.add(setDto.getPopupSeq());
			}

			int ret = popupService.delete(delList);
			axRet.setResultData(ret)
					.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}

}
