package com.erns.coching.search.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erns.coching.common.Const;
import com.erns.coching.common.aop.ApiLogging;
import com.erns.coching.common.controller.AbstractApiController;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.model.PageInfo;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.search.domain.CochingEsRawSearchDTO;
import com.erns.coching.search.domain.CochingEsRawVO;
import com.erns.coching.search.domain.EsManageDTO;
import com.erns.coching.search.domain.vg.ValidEsCochingRawMgr0011;
import com.erns.coching.search.domain.vg.ValidEsCochingRawMgr0012;
import com.erns.coching.search.domain.vg.ValidEsCochingRawMgr0029;
import com.erns.coching.search.domain.vg.ValidEsManage0101;
import com.erns.coching.search.service.CochingEsRawService;
import com.erns.coching.sysProp.domain.SysPropVO;
import com.erns.coching.sysProp.service.SysPropService;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 코칭 원료 ES 관리 API Controller
 * </p>
 * 
 * @author Hunwoo Park
 */
@Slf4j
@RestController
@RequestMapping("/api/search/manager/coching/raw")
@PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_ADMIN')")
public class CochingRawManagerApiController extends AbstractApiController {

  @Autowired
  protected CochingEsRawService cochingEsRawService;

	@Autowired
	private SysPropService sysPropService;
  
  /**
	 * 코칭 원료 DB 목록
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.ES_COCHING_RAW_LIST)
	@RequestMapping(value = "/list.api")
	public ApiResult list(
			HttpServletRequest request,
			@RequestBody @Validated(ValidEsCochingRawMgr0011.class) CochingEsRawSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		// API Call
		PageInfo pi = new PageInfo(0, param);
		int totalItem	=	cochingEsRawService.getListCount(param);
		List<CochingEsRawVO> list = null;
		if(totalItem <=	0){
			list = new ArrayList<>();
		}else{
			list = cochingEsRawService.getListVo(param);
		}
		pi.setCurrentPage(totalItem, param.getPage());

		axRet.setSc(param)
				.setPageInfo(pi)
				.setList(list)
				.set(ApiResultError.NO_ERROR);

		return axRet;
	}

	/**
	 *  코칭 원료 DB 단건 로드
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.ES_COCHING_RAW_GET)
	@RequestMapping("/get.api")
	public ApiResult getItem(
			HttpServletRequest request,
			@RequestBody @Validated(ValidEsCochingRawMgr0012.class) CochingEsRawSearchDTO param,
			Errors errors) {

		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) {// 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			// API Call
			Map<String, Object> retItem = cochingEsRawService.load(param.getRawSeq());
			axRet.setResultData(retItem)
				.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}

  /**
	 * 코칭 원료 DB 테이블 초기화
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.ES_COCHING_RAW_RESET_TABLE)
	@RequestMapping("/resetTable.api")
	public ApiResult resetTable(
			HttpServletRequest request,
			@RequestBody @Validated(ValidEsCochingRawMgr0029.class) CochingEsRawSearchDTO param,
			Errors errors) {

		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) {// 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			// API Call
      int ret = cochingEsRawService.resetTable();

			axRet.setResultData(ret)
				.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}

	// ES 원료 관리용 =============================================
	/**
	 * [ES]인덱스명 조회
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.ES_COCHING_RAW_INDICES)
	@PostMapping("/indices.api")
	public ApiResult indices(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody @Validated(ValidEsManage0101.class) EsManageDTO param,
			Errors errors) {

		log.debug("param:{}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) {// 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		// API Call
		try {
			SysPropVO indexPrefixProp = sysPropService.loadVo(Const.ES_COCHING_RAW_INDEX_PREFIX_SYSKEY);
			param.setCommands("/"+indexPrefixProp.getSysValue()+"*?format=json");

			axRet = cochingEsRawService.indices(param);
			axRet.put(ApiResult.AR_KEY_SC, param)
				.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			log.error("indices Error", e);			
		}

		return axRet;
	}

	/**
	 * [ES]인덱스 생성
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.ES_COCHING_RAW_CREATE_INDEX)
	@PostMapping("/createIndex.api")
	public ApiResult createIndex(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody @Validated(ValidEsManage0101.class) EsManageDTO param,
			Errors errors) {

		log.debug("param:{}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) {// 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		// API Call
		try {
			//파라미터 무시
			SysPropVO indexPrefixProp = sysPropService.loadVo(Const.ES_COCHING_RAW_INDEX_PREFIX_SYSKEY);
			DateTimeFormatter indexNameTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
			String newIndexName = indexPrefixProp.getSysValue() + "_" +LocalDateTime.now().format(indexNameTimeFormatter);
			param.setIndexName(newIndexName);

			cochingEsRawService.createEsIndex(param.getIndexName());
			axRet.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			log.error("createEsIndex Error", e);
			axRet.setResultMessage(e.getMessage());
		}

		return axRet;
	}
}
