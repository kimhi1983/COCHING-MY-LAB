package com.erns.es.log.controller;

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

import com.erns.es.common.aop.ApiLogging;
import com.erns.es.common.model.ApiResult;
import com.erns.es.common.model.PageInfo;
import com.erns.es.common.type.ApiResultError;
import com.erns.es.log.domain.UserLogSearchDTO;
import com.erns.es.log.service.UserLogService;

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
	private UserLogService userLogService;
	
	/**
	 * 사용자로그 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging
	@PostMapping("/list.api")    
	public ApiResult getLogList(
            HttpServletRequest request,
            @RequestBody UserLogSearchDTO param,
            Errors errors) {
                
        ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);        
    	
    	try {
    		//API Call
    		PageInfo pi = new PageInfo(0, param);
    		List<Map<String, Object>> logList = userLogService.getList(param);
    		int totalItem = userLogService.getListCount(param);
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
