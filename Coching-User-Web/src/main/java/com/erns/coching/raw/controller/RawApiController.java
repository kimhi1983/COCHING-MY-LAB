package com.erns.coching.raw.controller;

import java.util.ArrayList;
import java.util.HashMap;
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

import com.erns.coching.common.aop.ApiLogging;
import com.erns.coching.common.controller.AbstractApiController;
import com.erns.coching.common.file.repository.FileRepository;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.model.PageInfo;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.RecentRefCode;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.log.service.UserRecentInfoService;
import com.erns.coching.login.domain.LoginUserDTO;
import com.erns.coching.raw.domain.RawDetailSearchDTO;
import com.erns.coching.raw.domain.RawDetailVO;
import com.erns.coching.raw.domain.RawMasterSearchDTO;
import com.erns.coching.raw.domain.vg.ValidMasterRaw0001;
import com.erns.coching.raw.domain.vg.ValidSearchRawDetail0001;
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
@PreAuthorize("permitAll")
public class RawApiController extends AbstractApiController{

	public static final String FILE_INPUT_NAME = "raw_files";
	@Autowired
	protected RawService rawService;

	@Autowired
	private FileRepository fileRepo;

	@Autowired
  private UserRecentInfoService userRecentInfoService;

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

		param.setUseYn("Y");
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

		LoginUserDTO loginUser = getLoginedUserObject();
		RawDetailVO detailVo = rawService.loadDetail(param);
		if(null == detailVo) {
			return axRet.set(ApiResultError.NO_ERROR, getLocale());
		}

		if(null != loginUser) {
			log.debug("loginUser:{}", loginUser);
			//로그인 되어있는 경우 로그 추가
			userRecentInfoService.addLog(RecentRefCode.RAW
				, loginUser.getSeq()
				, String.valueOf(detailVo.getRawDetailSeq())
				, detailVo.getRawSeq()
			);			
		}
		
		List<Map<String, Object>> fileList = fileRepo.getList("FT_RAW", detailVo.getRawDetailSeq(), null);
		Map<String, Object> retData = new HashMap<>();
		retData.put("detail", detailVo);
		retData.put("files", fileList);
		
		axRet.setResultData(retData).set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}
	
}
