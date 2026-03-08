package com.erns.coching.popup.controller;

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
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.popup.domain.PopupSearchDTO;
import com.erns.coching.popup.domain.vg.ValidPopup0001;
import com.erns.coching.popup.domain.vg.ValidPopup0002;
import com.erns.coching.popup.service.PopupService;

import lombok.extern.slf4j.Slf4j;

/**
*
* <p>Popup API Controller</p>
*
* @author Hunwoo Park
*
*/
@Slf4j
@RestController
@RequestMapping("/api/popup")
@PreAuthorize("permitAll")
public class PopupApiContoller extends AbstractApiController{

	@Autowired
	private PopupService popupService;

	/**
	 * 팝업 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.POPUP_LIST)
	@PostMapping("/list.api")
	public ApiResult getPopupList(
            HttpServletRequest request,
            @RequestBody @Validated(ValidPopup0001.class) PopupSearchDTO param,
            Errors errors) {

        ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
        if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

        //API Call
        List<Map<String, Object>> list = popupService.getList(param);

		axRet.setSc(param).setList(list);
    	return axRet.set(ApiResultError.NO_ERROR, getLocale());
	}

	/**
	 * 팝업 클릭
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.POPUP_CLICK)
	@PostMapping("/click.api")
	public ApiResult updateClickCnt(
			HttpServletRequest request,
			@RequestBody @Validated(ValidPopup0002.class) PopupSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT, getLocale());
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		popupService.updateClickCnt(param.getPopupSeq());

		//별다른 액션없이 OK 리턴
 		return axRet.set(ApiResultError.NO_ERROR, getLocale());
	}
}
