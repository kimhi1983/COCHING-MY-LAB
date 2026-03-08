package com.erns.coching.raw.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erns.coching.common.aop.ApiLogging;
import com.erns.coching.common.controller.AbstractApiController;
import com.erns.coching.common.file.repository.FileRepository;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.model.PageInfo;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.login.domain.LoginUserDTO;
import com.erns.coching.raw.domain.RawDetailSearchDTO;
import com.erns.coching.raw.domain.RawManagerSearchDTO;
import com.erns.coching.raw.domain.RawMasterMultipleSetDTO;
import com.erns.coching.raw.domain.RawMasterSearchDTO;
import com.erns.coching.raw.domain.RawMasterSetDTO;
import com.erns.coching.raw.domain.RawMasterVO;
import com.erns.coching.raw.domain.vg.ValidMasterRaw0001;
import com.erns.coching.raw.domain.vg.ValidMasterRaw0032;
import com.erns.coching.raw.domain.vg.ValidSearchRawDetail0001;
import com.erns.coching.raw.domain.vg.ValidSearchRawManager0001;
import com.erns.coching.raw.service.RawService;

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

		axRet.setSc(param).setPageInfo(pi).setList(list).set(ApiResultError.NO_ERROR);


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
		RawManagerSearchDTO search = new RawManagerSearchDTO();
		search.setRawSeq(param.getRawSeq());
		List<Map<String, Object>> managerList = rawService.getManagerList(search);
		List<Map<String, Object>> elmList = rawService.getElmList(param);
		List<Map<String, Object>> typeList = rawService.getTypeList(param);
		List<Map<String, Object>> docList = rawService.getDocList(param);

		RawDetailSearchDTO searchDetail = new RawDetailSearchDTO();
		searchDetail.setRawDetailSeq(param.getRawDetailSeq());
		Map<String, Object> detail = rawService.getDetail(searchDetail);
		List<Map<String, Object>> fileList = fileRepo.getList("FT_RAW", param.getRawDetailSeq(), null);
		if(null != fileList){
			detail.put("fileList", fileList);
		}

		data.put("managerList",managerList);
		data.put("elmList",elmList);
		data.put("typeList",typeList);
		data.put("docList",docList);
		data.put("detail",detail);

		axRet.setResultData(data).set(ApiResultError.NO_ERROR);

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
		axRet.setList(list).set(ApiResultError.NO_ERROR);

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
		axRet.setList(list).set(ApiResultError.NO_ERROR);

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
		axRet.setList(list).set(ApiResultError.NO_ERROR);

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
		axRet.setList(list).set(ApiResultError.NO_ERROR);

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
		long detailSeq = ((Number) data.get("rawDetailSeq")).longValue();
		List<Map<String, Object>> fileList = fileRepo.getList("FT_RAW", detailSeq, null);
		if(null != fileList){
			data.put("fileList", fileList);
		}

		axRet.setResultData(data).set(ApiResultError.NO_ERROR);

		return axRet;
	}

	/**
	 * 원료 목록
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

		param.setUseYn("N");
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

		axRet.setSc(param).setPageInfo(pi).setList(list).set(ApiResultError.NO_ERROR);


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

		axRet.setList(list).set(ApiResultError.NO_ERROR);

		return axRet;
	}
	
	/**
	 * 원료 상태 설정
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RAW_SET_STATE)
	@PostMapping("/setState.api")
	public ApiResult setRawState(
			HttpServletRequest request,
			@RequestBody @Validated(ValidMasterRaw0032.class) RawMasterMultipleSetDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			LoginUserDTO loginUser = getLoginedUserObject();

			List<RawMasterVO> setList = new ArrayList<>();
			for (RawMasterSetDTO setDto : param.getList()) {
				if (setDto.getRawSeq() <= 0){
					continue;
				}
				
				RawMasterVO rawMaster = RawMasterVO.builder()
						.rawSeq(setDto.getRawSeq())
						.useYn(setDto.getUseYn())
						.chnr(loginUser.getSeq())
						.build();

				setList.add(rawMaster);
			}

			int ret = rawService.updateState(setList);
			axRet.setResultData(ret)
				.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}
}
