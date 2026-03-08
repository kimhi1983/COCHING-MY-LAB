package com.erns.coching.raw.controller;

import com.erns.coching.common.aop.ApiLogging;
import com.erns.coching.common.controller.AbstractApiController;
import com.erns.coching.common.file.repository.FileRepository;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.model.PageInfo;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.NotificationType;
import com.erns.coching.login.domain.LoginUserDTO;
import com.erns.coching.notification.service.NotificationService;
import com.erns.coching.partner.service.PartnerService;
import com.erns.coching.raw.domain.RawRequestReplySetDTO;
import com.erns.coching.raw.domain.RawRequestSearchDTO;
import com.erns.coching.raw.domain.RawRequestSetDTO;
import com.erns.coching.raw.domain.vg.ValidRawRequest0001;
import com.erns.coching.raw.domain.vg.ValidRawRequest0003;
import com.erns.coching.raw.domain.vg.ValidSearchRawReply0001;
import com.erns.coching.raw.domain.vg.ValidSetRawReply0002;
import com.erns.coching.raw.domain.vg.ValidSetRawRequest0001;
import com.erns.coching.raw.domain.vg.ValidSetRawRequest0002;
import com.erns.coching.raw.domain.vg.ValidSetRawReply0001;
import com.erns.coching.raw.service.RawService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
*
* <p>Popup API Controller</p>
*
* @author Hunwoo Park
*
*/
@Slf4j
@RestController
@RequestMapping("/api/raw/request")
@PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_COCHING_USER')")
public class RequestApiContoller extends AbstractApiController{

	public static final String FILE_INPUT_NAME = "request_files";
	@Autowired
	private RawService rawService;

	@Autowired
	private FileRepository fileRepo;

	@Autowired
	private PartnerService partnerService;

	@Autowired
	private NotificationService notificationService;

	/**
	 * 요청 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging
	@PostMapping("/list.api")
	public ApiResult getRequestList(
            HttpServletRequest request,
            @RequestBody @Validated(ValidRawRequest0001.class) RawRequestSearchDTO param,
            Errors errors) {

        ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
        if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		PageInfo pi = new PageInfo(0, param);
		int totalItem = rawService.getRequestListCount(param);
		List<Map<String, Object>> list = null;
		if(totalItem <= 0) {
			list = new ArrayList<Map<String, Object>>();
		}else {
			list = rawService.getRequestList(param);
			pi.setCurrentPage(totalItem, param.getPage());
		}
		pi.setCurrentPage(totalItem, param.getPage());

		axRet.setSc(param).setPageInfo(pi).setList(list).set(ApiResultError.NO_ERROR, getLocale());


		axRet.setSc(param).setList(list);
    	return axRet.set(ApiResultError.NO_ERROR, getLocale());
	}

	/**
	 * 원료 요청 상세 조회
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging
	@PostMapping("/get.api")
	public ApiResult getRequest(
			HttpServletRequest request,
			@RequestBody @Validated(ValidRawRequest0003.class) RawRequestSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT, getLocale());
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		Map<String, Object> data = rawService.loadRequest(param);
		if(null != data) {
			List<Map<String, Object>> typeList = rawService.getRequestTypeList(param);
			data.put("typelist", typeList);

			List<Map<String, Object>> replyList = rawService.getRequestReplyList(param);
			for(Map<String, Object> map : replyList){
				long replySeq = Optional.ofNullable((Number) map.get("rawReplySeq"))
						.map(Number::longValue)
						.orElse(0L);
				if(replySeq > 0 ) {
					List<Map<String, Object>> fileList = fileRepo.getList("FT_REPLY", replySeq, null);
					map.put("filelist", fileList);
				}
			}
			data.put("replyList", replyList);

			axRet.setResultData(data).set(ApiResultError.NO_ERROR, getLocale());
		}else{
			axRet.set(ApiResultError.NO_AUTH_ERROR, getLocale());
		}

		return axRet;
	}

	/**
	 * 원료 요청 저장
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging
	@PostMapping("/set.api")
	public ApiResult setRequest(
			HttpServletRequest request,
			@RequestBody @Validated(ValidSetRawRequest0001.class) RawRequestSetDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT, getLocale());
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		LoginUserDTO userDTO = getLoginedUserObject();
		param.setRgtr(userDTO.getSeq());
		param.setMembName(userDTO.getUserName());
		axRet = rawService.addRequest(param);

 		return axRet.set(ApiResultError.NO_ERROR, getLocale());
	}

	/**
	 * 원료 요청 상태 업데이트
	 * @param request
	 * @param setDTO
	 * @param errors
	 * @return
	 */
	@ApiLogging
	@PostMapping("/updateStatus.api")
	public ApiResult updateStatus(
			HttpServletRequest request,
			@RequestBody @Validated(ValidSetRawRequest0002.class) RawRequestSetDTO setDTO,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT, getLocale());
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		LoginUserDTO userDTO = getLoginedUserObject();
		setDTO.setChnr(userDTO.getSeq());
		if(rawService.updateStatus(setDTO) <= 0){
			return axRet.set(ApiResultError.ERROR_DEFAULT, getLocale());
		}

		//알림 등록
		NotificationType notiType = NotificationType.REQUEST_CONFIRM;
		if("003".equals(setDTO.getStatus())){
			notiType = NotificationType.REQUEST_SEND;
		} else if("004".equals(setDTO.getStatus())){
			notiType = NotificationType.REQUEST_MAIL;
		}

		if(!"002".equals(setDTO.getStatus())) {
			notificationService.insertNoti(notiType,
					setDTO.getRawRequestSeq(), setDTO.getRgtr(), setDTO.getPtnName(), setDTO.getRawName(), userDTO.getSeq());
		}

		return axRet.set(ApiResultError.NO_ERROR, getLocale());
	}

	/**
	 * 원료 요청 자료 전달 내역 조회
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging
	@PostMapping("/getReply.api")
	public ApiResult getReply(
			HttpServletRequest request,
			@RequestBody @Validated(ValidSearchRawReply0001.class) RawRequestSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT, getLocale());
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		List<Map<String, Object>> replyList = rawService.getRequestReplyList(param);
		for(Map<String, Object> map : replyList){
			long replySeq = Optional.ofNullable((Number) map.get("rawReplySeq"))
					.map(Number::longValue)
					.orElse(0L);
			if(replySeq > 0 ) {
				List<Map<String, Object>> fileList = fileRepo.getList("FT_REPLY", replySeq, null);
				map.put("filelist", fileList);
			}
		}

		axRet.setList(replyList).set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * 원료 답변 저장
	 * @param request
	 * @param setDTO
	 * @param errors
	 * @return
	 */
	@ApiLogging
	@PostMapping("/setReply.api")
	public ApiResult setReply(
			HttpServletRequest request,
			@Validated(ValidSetRawReply0001.class) RawRequestReplySetDTO setDTO,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT, getLocale());
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			// 파일 등록
			fileRepo.printFilesInfo(request);
			List<MultipartFile> getUploadFiles = null;
			try {
				getUploadFiles = fileRepo.getUploadFiles(request, FILE_INPUT_NAME);
				log.debug("Files:{}", getUploadFiles.size());
			} catch (Exception e) {
				e.printStackTrace();
				return axRet.set(ApiResultError.ATTACH_ERROR, getLocale());
			}

			LoginUserDTO userDTO = getLoginedUserObject();
			setDTO.setRgtr(userDTO.getSeq());
			if (rawService.addRequestReply(setDTO, getUploadFiles) <= 0) {
				return axRet.set(ApiResultError.ERROR_DEFAULT, getLocale());
			}
		}catch (Exception e){
			e.printStackTrace();
			return axRet.set(ApiResultError.ERROR_DEFAULT, getLocale());
		}

		return axRet.set(ApiResultError.NO_ERROR, getLocale());
	}

	/**
	 * 원료 요청 자료 삭제여부 업데이트
	 * @param request
	 * @param setDTO
	 * @param errors
	 * @return
	 */
	@ApiLogging
	@PostMapping("/updateReplyDelYn.api")
	public ApiResult updateReplyDelYn(
			HttpServletRequest request,
			@RequestBody @Validated(ValidSetRawReply0002.class) RawRequestReplySetDTO setDTO,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT, getLocale());
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		LoginUserDTO userDTO = getLoginedUserObject();
		setDTO.setChnr(userDTO.getSeq());
		if(rawService.updateReplyDelYn(setDTO) <= 0){
			return axRet.set(ApiResultError.ERROR_DEFAULT, getLocale());
		}

		return axRet.set(ApiResultError.NO_ERROR, getLocale());
	}
}
