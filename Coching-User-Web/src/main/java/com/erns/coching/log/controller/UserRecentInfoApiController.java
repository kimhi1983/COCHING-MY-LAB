package com.erns.coching.log.controller;

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
import com.erns.coching.log.domain.UserRecentInfoSearchDTO;
import com.erns.coching.log.domain.vg.ValidUserRecentInfo0011;
import com.erns.coching.log.domain.vg.ValidUserRecentInfo0040;
import com.erns.coching.log.service.UserRecentInfoService;
import com.erns.coching.login.domain.LoginUserDTO;

import lombok.extern.slf4j.Slf4j;

/**
 * 사용자 최근 정보 API
 * @author hw.park@erns.co.kr
 */
@Slf4j
@RestController
@RequestMapping("/api/log/user/recent")
@PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_COCHING_USER')")
public class UserRecentInfoApiController extends AbstractApiController {
  
  @Autowired
  private UserRecentInfoService userRecentInfoService;

  /**
	 * 사용자 최근 본 원료 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.USER_RECENT_RAW_LIST)
	@PostMapping("list.api")
	public ApiResult getUserList(
            HttpServletRequest request,
            @RequestBody @Validated(ValidUserRecentInfo0011.class) UserRecentInfoSearchDTO param,
            Errors errors) {
		
		ApiResult axRet = new ApiResult();
    if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		LoginUserDTO loginUser = getLoginedUserObject();
		param.setMembSeq(loginUser.getSeq());
		    
		//API Call
		PageInfo pi = new PageInfo(0, param);
		int totalItem = userRecentInfoService.getListCount(param);
		List<Map<String, Object>> list = new ArrayList<>();
		if(totalItem > 0) {
			list = userRecentInfoService.getList(param); //최근사용 목록			
		}
    pi.setCurrentPage(totalItem, param.getPage());

		axRet.setSc(param)
			.setPageInfo(pi)
			.setList(list)
			.set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	
	/**
	 * 사용자 최근 본 원료 삭제
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.USER_RECENT_RAW_DEL)
	@PostMapping("/del.api")
	public ApiResult deleteUserRecentInfo(
						HttpServletRequest request,
						@RequestBody @Validated(ValidUserRecentInfo0040.class) UserRecentInfoSearchDTO param,
						Errors errors) {
		log.debug("param: {}", param);

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		LoginUserDTO loginUser = getLoginedUserObject();

		try {
			param.setMembSeq(loginUser.getSeq());
			
			int ret = userRecentInfoService.deleteForUser(param);
			if(ret > 0) {
				axRet.setResultData(ret)
					.set(ApiResultError.NO_ERROR, getLocale());
			}
						
		}catch(Exception e) {
			e.printStackTrace();
			axRet.set(ApiResultError.ERROR_DEFAULT, getLocale());
		}

		return axRet;
	}
  
}
