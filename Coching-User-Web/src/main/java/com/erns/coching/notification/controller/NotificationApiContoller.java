package com.erns.coching.notification.controller;

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
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.model.PageInfo;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.login.domain.LoginUserDTO;
import com.erns.coching.notification.domain.NotificationSearchDTO;
import com.erns.coching.notification.domain.NotificationSetDTO;
import com.erns.coching.notification.domain.vg.ValidNotification0001;
import com.erns.coching.notification.domain.vg.ValidSetNotification0001;
import com.erns.coching.notification.service.NotificationService;

import lombok.extern.slf4j.Slf4j;

/**
*
* <p>알림 API Controller</p>
*
* @author Hunwoo Park
*
*/
@Slf4j
@RestController
@RequestMapping("/api/notification")
@PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_COCHING_USER')")
public class NotificationApiContoller extends AbstractApiController{

	@Autowired
	private NotificationService notificationService;

	/**
	 * 알림 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.NOTIFICATION_LIST)
	@PostMapping("/list.api")
	public ApiResult getPopupList(
            HttpServletRequest request,
            @RequestBody @Validated(ValidNotification0001.class) NotificationSearchDTO param,
            Errors errors) {

        ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
        if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		LoginUserDTO user = getLoginedUserObject();
		param.setMembSeq(user.getSeq());

		PageInfo pi = new PageInfo(0, param);
		int totalItem = notificationService.getListCount(param);
		List<Map<String, Object>> list = null;
		if(totalItem <= 0) {
			list = new ArrayList<Map<String, Object>>();
		}else {
			list = notificationService.getList(param);
			pi.setCurrentPage(totalItem, param.getPage());
		}
		pi.setCurrentPage(totalItem, param.getPage());
		param.setChkYn("N");
		int newCnt = notificationService.getListCount(param);
		Map<String, Object> map = new HashMap<>();
		map.put("newCnt", newCnt);

		axRet.setResultData(map).setSc(param).setPageInfo(pi).setList(list).set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * 확인여부 업데이트
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.NOTIFICATION_CHK_YN)
	@PostMapping("/updateChkYn.api")
	public ApiResult updateChkYn(
			HttpServletRequest request,
			@RequestBody @Validated(ValidSetNotification0001.class) NotificationSetDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT, getLocale());
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		if(notificationService.updateChkYn(param) <= 0){
			return axRet.set(ApiResultError.ERROR_DEFAULT, getLocale());
		}

		//별다른 액션없이 OK 리턴
 		return axRet.set(ApiResultError.NO_ERROR, getLocale());
	}
}
