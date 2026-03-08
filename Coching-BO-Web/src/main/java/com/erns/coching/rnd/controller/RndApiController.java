package com.erns.coching.rnd.controller;

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

import com.erns.coching.common.aop.ApiLogging;
import com.erns.coching.common.controller.AbstractApiController;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.model.PageInfo;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.login.domain.LoginUserDTO;
import com.erns.coching.rnd.domain.AiLabResSearchDTO;
import com.erns.coching.rnd.domain.AiPrescriptionDTO;
import com.erns.coching.rnd.domain.IngredientDTO;
import com.erns.coching.rnd.domain.LabElementInfSearchDTO;
import com.erns.coching.rnd.domain.LabElementInfVO;
import com.erns.coching.rnd.domain.LabMasterSearchDTO;
import com.erns.coching.rnd.domain.LabMasterSetDTO;
import com.erns.coching.rnd.domain.LabMasterVO;
import com.erns.coching.rnd.domain.vg.ValidAiLabRes0011;
import com.erns.coching.rnd.domain.vg.ValidAiPrescription0011;
import com.erns.coching.rnd.domain.vg.ValidLabMaster0011;
import com.erns.coching.rnd.domain.vg.ValidLabMaster0012;
import com.erns.coching.rnd.domain.vg.ValidLabMaster0020;
import com.erns.coching.rnd.domain.vg.ValidLabMaster0030;
import com.erns.coching.rnd.service.AiLabResService;
import com.erns.coching.rnd.service.LabElementInfService;
import com.erns.coching.rnd.service.LabMasterService;
import com.erns.coching.rnd.service.RndService;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * <p>
 * R&D API Controller
 * </p>
 *
 * @author Hunwoo Park
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/rnd")
@PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_ADMIN')")
public class RndApiController extends AbstractApiController {

  @Autowired
  private RndService rndService;

	@Autowired
	private LabMasterService labMasterService;

	@Autowired
	private LabElementInfService labElementInfService;

	@Autowired
	private AiLabResService aiLabResService;
	
	@ApiLogging(logType = UserLogType.RND_LAB_MASTER_LIST)
	@PostMapping("/labMaster/list.api")
	public ApiResult getLabMasterList(
		HttpServletRequest request,
		@RequestBody @Validated(ValidLabMaster0011.class) LabMasterSearchDTO param,
		Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		// API Call
		PageInfo pi = new PageInfo(0, param);
		int totalItem = labMasterService.getListCount(param);
		List<Map<String, Object>> list = null;
		if (totalItem <= 0) {
			list = new ArrayList<>();
		} else {
			list = labMasterService.getList(param);			
		}
		pi.setCurrentPage(totalItem, param.getPage());

		axRet.setPageInfo(pi)
			.setSc(param)
			.setList(list)
			.set(ApiResultError.NO_ERROR);

		return axRet;
	}

	@ApiLogging(logType = UserLogType.RND_LAB_MASTER_GET)
	@PostMapping("/labMaster/get.api")
	public ApiResult getLabMasterDetail(
			HttpServletRequest request,
			@RequestBody @Validated(ValidLabMaster0012.class) LabMasterSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		// API Call
		Map<String, Object> labMaster = labMasterService.load(param);

		LabElementInfSearchDTO labElementInfSearchDTO = new LabElementInfSearchDTO();
		labElementInfSearchDTO.setLabSeq(param.getLabMstSeq());
		labElementInfSearchDTO.setRowSize(-1);
		List<Map<String, Object>> labElementInfList = labElementInfService.getList(labElementInfSearchDTO);

		labMaster.put("ingredientList", labElementInfList);

		axRet.setResultData(labMaster)
			.set(ApiResultError.NO_ERROR);

		return axRet;
	}

	@ApiLogging(logType = UserLogType.RND_LAB_MASTER_ADD)
	@PostMapping("/labMaster/add.api")
	public ApiResult addLabMaster(
		HttpServletRequest request,
		@RequestBody @Validated(ValidLabMaster0020.class) LabMasterSetDTO param,
		Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			// API Call		
			LoginUserDTO loginUser = getLoginedUserObject();
			LabMasterVO labMasterVO = LabMasterVO.AddLabMasterBuilder()
				.fromDto(param)
				.addUser(loginUser)
				.build();

			if(labMasterService.insert(labMasterVO) > 0){
				List<LabElementInfVO> ingredientList = new ArrayList<>();
				for(IngredientDTO ingDto : param.getIngredientList()){
					LabElementInfVO ingredient = LabElementInfVO.builder()
						.labEleSeq(0L)
						.labMstSeq(labMasterVO.getLabMstSeq())
						.rawElmId(ingDto.getId())
						.rawElmKr(ingDto.getRepName())
						.rawElmEn(ingDto.getRepNameEn())
						.build();
					ingredientList.add(ingredient);
				}
				labElementInfService.insert(ingredientList);

				axRet.setResultData(labMasterVO)
					.set(ApiResultError.NO_ERROR);
			}

		} catch (Exception e){
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}


	@ApiLogging(logType = UserLogType.RND_LAB_MASTER_UDT)
	@PostMapping("/labMaster/set.api")
	public ApiResult setLabMaster(
		HttpServletRequest request,
		@RequestBody @Validated(ValidLabMaster0020.class) LabMasterSetDTO param,
		Errors errors) {
	
		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			// API Call		
			LoginUserDTO loginUser = getLoginedUserObject();
			LabMasterVO labMasterVO = LabMasterVO.SetLabMasterBuilder()
				.fromDto(param)
				.updateUser(loginUser)
				.labMstSeq(param.getLabMstSeq())
				.build();

			if(labMasterService.update(labMasterVO) > 0){
				labElementInfService.deleteByLabMstSeq(param.getLabMstSeq());

				List<LabElementInfVO> ingredientList = new ArrayList<>();
				for(IngredientDTO ingDto : param.getIngredientList()){
					LabElementInfVO ingredient = LabElementInfVO.builder()
						.labEleSeq(0L)
						.labMstSeq(labMasterVO.getLabMstSeq())
						.rawElmId(ingDto.getId())
						.rawElmKr(ingDto.getRepName())
						.rawElmEn(ingDto.getRepNameEn())
						.build();
					ingredientList.add(ingredient);
				}
				labElementInfService.insert(ingredientList);

				axRet.setResultData(labMasterVO)
					.set(ApiResultError.NO_ERROR);
			}

		} catch (Exception e){
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}
	

	@ApiLogging(logType = UserLogType.RND_LAB_MASTER_DEL)
	@PostMapping("/labMaster/del.api")
	public ApiResult deleteLabMaster(
			HttpServletRequest request,
			@RequestBody @Validated(ValidLabMaster0030.class) LabMasterSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			// API Call		
			labElementInfService.deleteByLabMstSeq(param.getLabMstSeq());
			aiLabResService.deleteByLabMstSeq(param.getLabMstSeq());

			if(labMasterService.delete(param.getLabMstSeq()) > 0){
				axRet.set(ApiResultError.NO_ERROR);
			}
		} catch (Exception e){
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}

	@ApiLogging(logType = UserLogType.RND_AI_LAB_RES_LIST)
	@PostMapping("/aiLabRes/list.api")
	public ApiResult getAiLabResList(
		HttpServletRequest request,
		@RequestBody @Validated(ValidAiLabRes0011.class) AiLabResSearchDTO param,
		Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		// API Call
		PageInfo pi = new PageInfo(0, param);
		int totalItem = aiLabResService.getListCount(param);
		List<Map<String, Object>> list = null;
		if (totalItem <= 0) {
			list = new ArrayList<>();
		} else {
			list = aiLabResService.getList(param);			
		}
		pi.setCurrentPage(totalItem, param.getPage());

		axRet.setPageInfo(pi)
			.setSc(param)
			.setList(list)
			.set(ApiResultError.NO_ERROR);

		return axRet;
	}


  /**
	 * AI 처방
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.RND_AI_PRSC_RESULT_V1)
	@PostMapping("/aiPrscResultV1.api")
	public ApiResult aiPrscResultV1(
			HttpServletRequest request,
			@RequestBody @Validated(ValidAiPrescription0011.class) AiPrescriptionDTO param,
			Errors errors) {

		log.debug("param:{}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		// API Call
		try {
			LoginUserDTO loginUser = getLoginedUserObject();
			param.setMembSeq(loginUser.getSeq());
			
			axRet = rndService.aiPrscResultV1(param);
		} catch (JsonProcessingException e) {
			log.error("aiPrscResultV1 Error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}  
}
