package com.erns.coching.raw.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.erns.coching.common.file.domain.FileSearchDTO;
import com.erns.coching.common.file.domain.FileSetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.erns.coching.common.aop.ApiLogging;
import com.erns.coching.common.controller.AbstractApiController;
import com.erns.coching.common.file.repository.FileRepository;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.model.PageInfo;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.login.domain.LoginUserDTO;
import com.erns.coching.raw.domain.RawDetailSearchDTO;
import com.erns.coching.raw.domain.RawDetailSetDTO;
import com.erns.coching.raw.domain.RawDetailVO;
import com.erns.coching.raw.domain.RawManagerSearchDTO;
import com.erns.coching.raw.domain.RawManagerSetDTO;
import com.erns.coching.raw.domain.RawMasterSearchDTO;
import com.erns.coching.raw.domain.RawMasterSetDTO;
import com.erns.coching.raw.domain.RawMasterVO;
import com.erns.coching.raw.domain.vg.ValidMasterRaw0001;
import com.erns.coching.raw.domain.vg.ValidSearchRawDetail0001;
import com.erns.coching.raw.domain.vg.ValidSearchRawManager0001;
import com.erns.coching.raw.domain.vg.ValidSetRaw0001;
import com.erns.coching.raw.domain.vg.ValidSetRaw0002;
import com.erns.coching.raw.domain.vg.ValidSetRaw0003;
import com.erns.coching.raw.domain.vg.ValidSetRaw0004;
import com.erns.coching.raw.domain.vg.ValidSetRaw0005;
import com.erns.coching.raw.domain.vg.ValidSetRaw0006;
import com.erns.coching.raw.domain.vg.ValidSetRawDetail0001;
import com.erns.coching.raw.domain.vg.ValidSetRawDetail0002;
import com.erns.coching.raw.service.RawService;
import com.nimbusds.jose.JOSEException;

import lombok.extern.slf4j.Slf4j;

/**
*
* <p>원료 API Controller</p>
*
* @author Hunwoo Park
*
*/
@Slf4j
@RestController
@RequestMapping("/api/raw")
public class RawApiController extends AbstractApiController{

	public static final String FILE_INPUT_NAME = "raw_files";
	@Autowired
	protected RawService rawService;

	@Autowired
	private FileRepository fileRepo;

	/**
	 * 원료 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RAW_LIST)
	@PostMapping("/list.api")
	public ApiResult getRawList(
            HttpServletRequest request,
            @RequestBody RawMasterSearchDTO param,
            Errors errors) {

        ApiResult axRet = new ApiResult();
        if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		PageInfo pi = new PageInfo(0, param);
		int totalItem = rawService.getListCount(param);
		List<Map<String, Object>> list = null;
		if(totalItem <= 0) {
			list = new ArrayList<Map<String, Object>>();
		}else {
			list = rawService.getList(param);
			pi.setCurrentPage(totalItem, param.getPage());
		}
        pi.setCurrentPage(totalItem, param.getPage());

		axRet.setSc(param).setPageInfo(pi).setList(list).set(ApiResultError.NO_ERROR, getLocale());


        return axRet;
	}

	/**
	 * 원료 상세 조회
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RAW_GET)
	@PostMapping("/get.api")
	public ApiResult getRaw(
			HttpServletRequest request,
			@RequestBody @Validated(ValidMasterRaw0001.class) RawMasterSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		Map<String, Object> data = rawService.load(param);
		axRet.setResultData(data).set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * <p>원료 등록</p>
	 * @param request
	 * @param setDTO
	 * @param errors 파라미터 바인딩 오류 내역
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RAW_ADD)
	@PostMapping("/set.api")
	public ApiResult setRaw(
			HttpServletRequest request,
			@RequestBody @Validated(ValidSetRaw0001.class) RawMasterSetDTO setDTO,
			Errors errors) throws JOSEException {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		LoginUserDTO loginUserDTO = getLoginedUserObject();
		setDTO.setRgtr(loginUserDTO.getSeq());
		//검색엔진 반영은 서비스 내부에서 처리
		axRet = rawService.addRaw(setDTO);

		return axRet;
	}

	/**
	 * <p>원료 excel 업로드</p>
	 * @param request
	 * @param setDTO
	 * @param errors 파라미터 바인딩 오류 내역
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RAW_EXCEL_UPLOAD)
	@PostMapping("/uploadRawExcel.api")
	public ApiResult uploadRawExcel(
			HttpServletRequest request,
			@Validated(ValidSetRaw0006.class) RawMasterSetDTO setDTO,
			Errors errors) throws JOSEException {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		LoginUserDTO loginUserDTO = getLoginedUserObject();
		setDTO.setRgtr(loginUserDTO.getSeq());

		try {
			// MultipartHttpServletRequest 변환
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;

			// 파일 추출
			MultipartFile file = multiRequest.getFile("file");
			if (file == null || file.isEmpty()) {

				return axRet.set(ApiResultError.RAW_ERR_NOT_EXCEL_DATA, getLocale());
			}
			//검색엔진 반영은 서비스 내부에서 처리
			axRet = rawService.uploadRawExcel(setDTO, file);
		}catch(Exception e){
			e.printStackTrace();
		}

		return axRet;
	}

	/**
	 * 원료 자료 등록
	 * @param request
	 * @param paramDto
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RAW_ADD_DETAIL)
	@PostMapping("/setDetail.api")
	public ApiResult setDetail(
			HttpServletRequest request,
			@Validated(ValidSetRawDetail0001.class) RawDetailSetDTO paramDto,
			Errors errors) {

		log.debug("param : {}", paramDto);

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}
		LoginUserDTO loginInfo = getLoginedUserObject();
		if(!"002".equals(loginInfo.getUserType())) {
			paramDto.setMembSeq(loginInfo.getSeq());
		}
		try{
			//담당자 확인
			RawManagerSearchDTO search = new RawManagerSearchDTO();
			search.setRawSeq(paramDto.getRawSeq());
			search.setMembSeq(paramDto.getMembSeq());
			Map<String, Object> checkManager = rawService.getManager(search);
			if(null == checkManager && !"002".equals(loginInfo.getUserType())){
				log.debug("[Raw Detail Error] RAW_ERR_NOT_MANAGER: {}", ApiResultError.RAW_ERR_NOT_MANAGER.getMessage());
				return new ApiResult(ApiResultError.RAW_ERR_NOT_MANAGER, getLocale());
			}

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

			if(rawService.addRawDetail(paramDto, getUploadFiles) <= 0){
				return axRet.set(ApiResultError.ERROR_DEFAULT, getLocale());
			}

			{	//파일 PDF 처리
				long rawDetailSeq = paramDto.getRawDetailSeq();
				List<Map<String, Object>> fileList = fileRepo.getList("FT_RAW", rawDetailSeq, null);
				if (null != fileList) {
					List<String> fileIdList = fileList.stream().map(item -> (String) item.get("fileId")).collect(Collectors.toList());
					rawService.addPdfProcess(fileIdList);
				}
			}

			axRet.set(ApiResultError.NO_ERROR, getLocale());
		}catch (Exception e){
			e.printStackTrace();
			return axRet.set(ApiResultError.ERROR_DEFAULT, getLocale());
		}

		//검색엔진 반영
		try{
			rawService.syncEs(new long[]{paramDto.getRawSeq()});
		}catch (Exception e){
			log.error("ES Sync Error", e);
		}

		return axRet;
	}

	/**
	 * <p>원료 수정</p>
	 * @param request
	 * @param setDTO
	 * @param errors 파라미터 바인딩 오류 내역
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RAW_SET)
	@PostMapping("/update.api")
	public ApiResult updateRaw(
			HttpServletRequest request,
			@RequestBody @Validated(ValidSetRaw0002.class) RawMasterSetDTO setDTO,
			Errors errors) throws JOSEException {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}
		LoginUserDTO loginUserDTO = getLoginedUserObject();
		setDTO.setRgtr(loginUserDTO.getSeq());
		axRet = rawService.updateRaw(setDTO);

		//검색엔진 반영
		try{
			rawService.syncEs(new long[]{setDTO.getRawSeq()});
		}catch (Exception e){
			log.error("ES Sync Error", e);
		}

		return axRet;
	}

	/**
	 * <p>원료 사용여부 업데이트</p>
	 * @param request
	 * @param setDTO
	 * @param errors 파라미터 바인딩 오류 내역
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RAW_SET_USE_YN)
	@PostMapping("/updateUseYn.api")
	public ApiResult updateUseYn(
			HttpServletRequest request,
			@RequestBody @Validated(ValidSetRaw0003.class) RawMasterSetDTO setDTO,
			Errors errors) throws JOSEException {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}
		LoginUserDTO loginUserDTO = getLoginedUserObject();
		RawMasterVO masterVO = RawMasterVO
				.UpdateRawMasterUseBuilder()
				.fromDto(setDTO)
				.membSeq(loginUserDTO.getSeq())
				.ptnSeq(loginUserDTO.getPtnSeq())
				.build();
		rawService.updateUseYn(masterVO);

		//검색엔진 반영
		try{
			rawService.syncEs(new long[]{setDTO.getRawSeq()});
		}catch (Exception e){
			log.error("ES Sync Error", e);
		}

		axRet.set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * <p>원료 삭제여부 업데이트</p>
	 * @param request
	 * @param setDTO
	 * @param errors 파라미터 바인딩 오류 내역
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RAW_SET_DEL_YN)
	@PostMapping("/updateDelYn.api")
	public ApiResult updateDelYn(
			HttpServletRequest request,
			@RequestBody @Validated(ValidSetRaw0004.class) RawMasterSetDTO setDTO,
			Errors errors) throws JOSEException {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}
		LoginUserDTO loginUserDTO = getLoginedUserObject();
		RawMasterVO masterVO = RawMasterVO
				.UpdateRawMasterUseBuilder()
				.fromDto(setDTO)
				.membSeq(loginUserDTO.getSeq())
				.ptnSeq(loginUserDTO.getPtnSeq())
				.build();
		rawService.updateDelYn(masterVO) ;

		//검색엔진 반영
		try{
			rawService.syncEs(new long[]{setDTO.getRawSeq()});
		}catch (Exception e){
			log.error("ES Sync Error", e);
		}

		axRet.set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * <p>자료 사용여부 업데이트</p>
	 * @param request
	 * @param setDTO
	 * @param errors 파라미터 바인딩 오류 내역
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RAW_SET_USE_YN)
	@PostMapping("/updateDetailUseYn.api")
	public ApiResult updateDetailUseYn(
			HttpServletRequest request,
			@RequestBody @Validated(ValidSetRawDetail0002.class) RawDetailSetDTO setDTO,
			Errors errors) throws JOSEException {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}
		LoginUserDTO loginUserDTO = getLoginedUserObject();
		RawDetailVO detailVO = RawDetailVO
				.UpdateRawDetailYnBuilder()
				.fromDto(setDTO)
				.seq(loginUserDTO.getSeq())
				.build();
		rawService.updateDetailUseYn(detailVO);

		//검색엔진 반영
		try{
			rawService.syncEs(new long[]{setDTO.getRawSeq()});
		}catch (Exception e){
			log.error("ES Sync Error", e);
		}

		axRet.set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * 미리보기 자료 등록
	 * @param request
	 * @param paramDto
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RAW_PREVIEW_UPLOAD_FILE)
	@PostMapping("/uploadFile.api")
	public ApiResult uploadFile(
			HttpServletRequest request,
			@Validated() RawDetailSetDTO paramDto,
			Errors errors) {

		log.debug("param : {}", paramDto);

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}
		LoginUserDTO loginInfo = getLoginedUserObject();
		paramDto.setMembSeq(loginInfo.getSeq());
		List<Map<String, Object>> list = null;
		try{
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

			list = rawService.procAttachFiles(paramDto, getUploadFiles, null, true);

		}catch (Exception e){
			e.printStackTrace();
			return axRet.set(ApiResultError.ERROR_DEFAULT, getLocale());
		}

		axRet.setSc(paramDto).setList(list).set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * 성분 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RAW_ELM_LIST)
	@PostMapping("/elmList.api")
	public ApiResult getElementList(
			HttpServletRequest request,
			@RequestBody RawMasterSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}
		List<Map<String, Object>> list = rawService.getElmList(param);
		axRet.setList(list).set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * 성분 구분 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RAW_TYPE_LIST)
	@PostMapping("/typeList.api")
	public ApiResult getTypeList(
			HttpServletRequest request,
			@RequestBody RawMasterSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}
		List<Map<String, Object>> list = rawService.getTypeList(param);
		axRet.setList(list).set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * 구비 서류 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RAW_DOC_LIST)
	@PostMapping("/docList.api")
	public ApiResult getDocList(
			HttpServletRequest request,
			@RequestBody RawMasterSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}
		List<Map<String, Object>> list = rawService.getDocList(param);
		axRet.setList(list).set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * 담당자 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RAW_MANAGER_LIST)
	@PostMapping("/managerList.api")
	public ApiResult getManagerList(
			HttpServletRequest request,
			@RequestBody @Validated(ValidSearchRawManager0001.class) RawManagerSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}
		List<Map<String, Object>> list = rawService.getManagerList(param);
		axRet.setList(list).set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * 원료 자료 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RAW_DETAIL)
	@PostMapping("/getDetail.api")
	public ApiResult getDetail(
			HttpServletRequest request,
			@RequestBody @Validated(ValidSearchRawDetail0001.class) RawDetailSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}
		Map<String, Object> data = rawService.getDetail(param);
		if(null != data) {
			long detailSeq = ((Number) data.get("rawDetailSeq")).longValue();
			List<Map<String, Object>> fileList = fileRepo.getList("FT_RAW", detailSeq, null);
			if (null != fileList) {
				data.put("fileList", fileList);
			}
		}else{
			data = new HashMap<>();
		}

		axRet.setResultData(data).set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * 휴지통 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RAW_GARBAGE_LIST)
	@PostMapping("/garbageList.api")
	public ApiResult garbageList(
			HttpServletRequest request,
			@RequestBody RawMasterSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		PageInfo pi = new PageInfo(0, param);
		int totalItem = rawService.getGarbageListCount(param);
		List<Map<String, Object>> list = null;
		if(totalItem <= 0) {
			list = new ArrayList<Map<String, Object>>();
		}else {
			list = rawService.getGarbageList(param);
			pi.setCurrentPage(totalItem, param.getPage());
		}
		pi.setCurrentPage(totalItem, param.getPage());

		axRet.setSc(param).setPageInfo(pi).setList(list).set(ApiResultError.NO_ERROR, getLocale());


		return axRet;
	}

	/**
	 * <p>원료 담당자 업데이트</p>
	 * @param request
	 * @param setDTO
	 * @param errors 파라미터 바인딩 오류 내역
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RAW_SET_MANAGER)
	@PostMapping("/updateRawManager.api")
	public ApiResult updateRawManager(
			HttpServletRequest request,
			@RequestBody @Validated(ValidSetRaw0005.class) RawManagerSetDTO setDTO,
			Errors errors) throws JOSEException {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}
		LoginUserDTO loginUserDTO = getLoginedUserObject();
		setDTO.setRgtr(loginUserDTO.getSeq());

		//검색엔진 반영은 서비스 내부에서 처리
		if(rawService.updateRawManager(setDTO) <= 0){
			return axRet.set(ApiResultError.ERROR_DEFAULT, getLocale());
		}

		axRet.set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * 성분 구분 코드 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RAW_CM_TYPE_LIST)
	@PostMapping("/cmTypeList.api")
	public ApiResult getCmTypeList(
			HttpServletRequest request,
			@RequestBody RawMasterSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		List<Map<String, Object>> list = rawService.getCmTypeList();

		axRet.setList(list).set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * 원료 썸네일 예시 이미지 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RAW_EX_THUMB_LIST)
	@PostMapping("/exThumbList.api")
	public ApiResult getExThumbList(
			HttpServletRequest request,
			@RequestBody RawMasterSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		List<Map<String, Object>> fileList = fileRepo.getList("FT_EX_THUMB", 0, null, 100);

		axRet.setList(fileList).set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * 원료 파트너 썸네일 이미지 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RAW_PARTNER_THUMB_LIST)
	@PostMapping("/prtThumbList.api")
	public ApiResult getPrtThumbList(
			HttpServletRequest request,
			@RequestBody RawMasterSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		List<Map<String, Object>> fileList = fileRepo.getList("FT_PRT_THUMB", param.getPtnSeq(), null, 100);

		axRet.setList(fileList).set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * 원료 파트너 썸네일 이미지 등록
	 * @param request
	 * @param paramDto
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RAW_PARTNER_THUMB_ADD)
	@PostMapping("/addThumbFile.api")
	public ApiResult addThumbFile(
			HttpServletRequest request,
			@Validated() RawMasterSetDTO paramDto,
			Errors errors) {

		log.debug("param : {}", paramDto);

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}
		LoginUserDTO loginInfo = getLoginedUserObject();
		paramDto.setRgtr(loginInfo.getSeq());
		List<Map<String, Object>> list = null;
		try{
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

			list = rawService.procAttachThumbFiles(paramDto, getUploadFiles, null, true);

		}catch (Exception e){
			e.printStackTrace();
			return axRet.set(ApiResultError.ERROR_DEFAULT, getLocale());
		}

		axRet.setList(list).set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * <p>원료 파트너 썸네일 이미지 삭제</p>
	 * @param request
	 * @param setDTO
	 * @param errors 파라미터 바인딩 오류 내역
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RAW_PARTNER_THUMB_DEL)
	@PostMapping("/updateThumbDelYn.api")
	public ApiResult updateThumbDelYn(
			HttpServletRequest request,
			@RequestBody @Validated(ValidSetRaw0005.class) RawMasterSetDTO setDTO,
			Errors errors) throws JOSEException {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}
		LoginUserDTO loginUserDTO = getLoginedUserObject();

		//검색엔진 반영은 서비스 내부에서 처리
		if(fileRepo.updateFile(setDTO.getThumbnailId(), setDTO.getDelYn(), loginUserDTO.getSeq()) <= 0){
			return axRet.set(ApiResultError.ERROR_DEFAULT, getLocale());
		}

		axRet.set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}
}
