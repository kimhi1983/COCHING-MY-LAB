package com.erns.coching.cochingtv.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erns.coching.cochingtv.domain.CochingtvMultipleSetDTO;
import com.erns.coching.cochingtv.domain.CochingtvSearchDTO;
import com.erns.coching.cochingtv.domain.CochingtvSetDTO;
import com.erns.coching.cochingtv.domain.CochingtvVO;
import com.erns.coching.cochingtv.domain.vg.ValidCochingtv0011;
import com.erns.coching.cochingtv.domain.vg.ValidCochingtv0020;
import com.erns.coching.cochingtv.domain.vg.ValidCochingtv0031;
import com.erns.coching.cochingtv.domain.vg.ValidCochingtv0032;
import com.erns.coching.cochingtv.domain.vg.ValidCochingtv0040;
import com.erns.coching.cochingtv.service.CochingtvService;
import com.erns.coching.common.Const;
import com.erns.coching.common.aop.ApiLogging;
import com.erns.coching.common.controller.AbstractApiController;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.model.PageInfo;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.login.domain.LoginUserDTO;
import com.erns.coching.search.service.CochingEsTvService;
import com.erns.coching.sysProp.domain.SysPropVO;
import com.erns.coching.sysProp.service.SysPropService;

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

	@Autowired
	private CochingEsTvService cochingEsTvService;

	@Autowired
	private SysPropService sysPropService;
	
	/**
	 * 코칭TV 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.COCHINGTV_LIST)
	@PostMapping("/list.api")
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
			.set(ApiResultError.NO_ERROR);

        return axRet;
	}
	
	/**
	 * 코칭TV 등록
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.COCHINGTV_ADD)
	@PostMapping("/new.api")
	public ApiResult insertCochingtv(
			HttpServletRequest request,
			@RequestBody @Validated(ValidCochingtv0020.class) CochingtvSetDTO param,
			Errors errors) {

		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			LoginUserDTO loginUser = getLoginedUserObject();

			CochingtvVO cochingtv = CochingtvVO.AddCochingtvBuilder()
					.addUser(loginUser)
					.fromDto(param)
					.build();

			Map<String, Object> retModel = cochingtvService.insert(cochingtv);

			//검색엔진 반영
			syncEs(new long[]{cochingtv.getTvSeq()});

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
	 * 코칭TV 상태 설정
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.COCHINGTV_UDT_STATE)
	@PostMapping("/setState.api")
	public ApiResult setCochingtvState(
			HttpServletRequest request,
			@RequestBody @Validated(ValidCochingtv0032.class) CochingtvMultipleSetDTO param,
			Errors errors) {

		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			// API Call
			LoginUserDTO loginUser = getLoginedUserObject();

			List<CochingtvVO> setList = new ArrayList<>();
			for (CochingtvSetDTO setDto : param.getList()) {
				if (setDto.getTvSeq() <= 0) {
					continue;
				}

				CochingtvVO cochingtv = CochingtvVO.builder()
						.tvSeq(setDto.getTvSeq())
						.ytUrl(setDto.getYtUrl())
						.title(setDto.getTitle())
						.hashtag(setDto.getHashtag())
						.views(setDto.getViews())
						.ytDttm(setDto.getYtDttm())
						.useYn(setDto.getUseYn())
						.content(setDto.getContent())
						.category(setDto.getCategory())
						.chnr(loginUser.getSeq())
						.build();

				setList.add(cochingtv);				
			}

			int ret = cochingtvService.updateState(setList);

			//검색엔진 반영
			long[] tvSeqList = setList.stream().mapToLong(CochingtvVO::getTvSeq).toArray();
			syncEs(tvSeqList);

			axRet.setResultData(ret)
					.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}
	
	/**
	 * 코칭TV 삭제
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.COCHINGTV_DEL)	
	@PostMapping("/dels.api")
	public ApiResult deleteCochingtvs(
			HttpServletRequest request,
			@RequestBody @Validated(ValidCochingtv0040.class) CochingtvMultipleSetDTO param,
			Errors errors) {

		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			// API Call
			LoginUserDTO loginUser = getLoginedUserObject();
			
			List<CochingtvVO> delList = new ArrayList<>();
			for (CochingtvSetDTO setDto : param.getList()) {
				
				CochingtvVO cochingtv = CochingtvVO.builder()
						.tvSeq(setDto.getTvSeq())
						.delYn(setDto.getDelYn())
						.chnr(loginUser.getSeq())
						.build();
				
				delList.add(cochingtv);
			}
			
			int ret = cochingtvService.updateDelYn(delList);

			//검색엔진 반영
			long[] tvSeqList = delList.stream().mapToLong(CochingtvVO::getTvSeq).toArray();
			syncEs(tvSeqList);

			axRet.setResultData(ret)
					.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}
	
	/**
	 * 코칭TV 순서 설정
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.COCHINGTV_UDT_ORDER)
	@PostMapping("/setOrders.api")
	public ApiResult setCochingtvOrders(
			HttpServletRequest request,
			@RequestBody @Validated(ValidCochingtv0031.class) CochingtvMultipleSetDTO param,
			Errors errors) {

		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			// API Call
			LoginUserDTO loginUser = getLoginedUserObject();

			List<CochingtvVO> setList = new ArrayList<>();
			for (CochingtvSetDTO setDto : param.getList()) {
				if (setDto.getTvSeq() <= 0) {
					continue;
				}

				CochingtvVO cochingtv = CochingtvVO.builder()
						.tvSeq(setDto.getTvSeq())
						.sortOrd(setDto.getSortOrd())
						.chnr(loginUser.getSeq())
						.build();

				setList.add(cochingtv);
			}

			int ret = cochingtvService.updateOrder(setList);

			//검색엔진 반영
			long[] tvSeqList = setList.stream().mapToLong(CochingtvVO::getTvSeq).toArray();
			syncEs(tvSeqList);

			axRet.setResultData(ret)
					.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}

	/**
	 * 검색엔진 개별 docs 반영
	 * @param tvSeqs
	 * @return
	 */
	public int syncEs(long[] tvSeqs){
		//검색엔진 반영
		String rawIndexName = getIndexName(Const.ES_COCHING_TV_ACTIVE_INDEX_SYSKEY);
		if(!StringUtils.hasText(rawIndexName)){
			return 0;
		}

		int retSyncCnt = 0;
		try{
			retSyncCnt = cochingEsTvService.bulkSyncDocToEs(rawIndexName, tvSeqs);
		}catch(Exception e){
			log.error("syncEs Error", e);
		}

		return retSyncCnt;
	}

	/**
	 * [ES] 환경설정에서 ES index 명을 가져온다.
	 * @param syskey
	 * @return
	 */
	private String getIndexName(String syskey) {
		SysPropVO loadProp = sysPropService.loadVo(syskey);
		if(null != loadProp){
			return loadProp.getSysValue();
		}

		return null;
	}
	
	
	/**
	 * Youtube API로 정보 가져오기
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.COCHINGTV_YOUTUBE_GET)
	@PostMapping("/getYoutubeApiInfo.api")
	public ApiResult getYoutubeApiInfo(
            HttpServletRequest request,
            @RequestBody @Validated(ValidCochingtv0011.class) CochingtvSearchDTO param,
            Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		Map<String, Object> ytInfo = cochingtvService.getYoutubeInfo(param);
		axRet.setResultData(ytInfo)
			.set(ApiResultError.NO_ERROR);
		return axRet;
	}
	
}
