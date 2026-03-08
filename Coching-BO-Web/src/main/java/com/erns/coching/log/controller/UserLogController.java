package com.erns.coching.log.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erns.coching.common.aop.ApiLogging;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.model.PageInfo;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.log.domain.ApiLogSearchDTO;
import com.erns.coching.log.service.ApiLogManageService;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * <p>사용자로그 API Controller</p> 
 *
 * @author Hunwoo Park 
 *
 */
@RestController
@RequestMapping("/api/log")
@PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_ADMIN')")
@Slf4j
public class UserLogController {

	@Autowired
	private ApiLogManageService apiLogService;
	
	/**
	 * 사용자로그 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.USER_LOG_LIST)
	@PostMapping("/list.api")    
	public ApiResult getLogList(
            HttpServletRequest request,
            @RequestBody ApiLogSearchDTO param,
            Errors errors) {
                
        ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);        
    	
    	try {
    		//API Call
    		PageInfo pi = new PageInfo(0, param);
    		List<Map<String, Object>> logList = apiLogService.getList(param);
    		int totalItem = apiLogService.getListCount(param);
    		pi.setCurrentPage(totalItem, param.getPage());
    		
    		axRet.setPageInfo(pi).setSc(param).setList(logList).set(ApiResultError.NO_ERROR);
    		
    	}catch (Exception e){
			log.error("{} Error", e);
			axRet.set(ApiResultError.ERROR_DEFAULT);
		}finally {
			
		}

        return axRet;
	}
}
