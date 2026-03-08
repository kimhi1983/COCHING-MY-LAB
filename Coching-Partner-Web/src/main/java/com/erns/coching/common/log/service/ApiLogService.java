package com.erns.coching.common.log.service;

import javax.servlet.http.HttpServletRequest;

import com.erns.coching.common.log.domain.IApiProcLogVO;
import com.erns.coching.common.model.ApiResult;

public interface ApiLogService {

	public int addLog(IApiProcLogVO param);
	
	public IApiProcLogVO generateReqLog(HttpServletRequest request);
	
	public IApiProcLogVO generateResLog(HttpServletRequest request, ApiResult apiRet);
	
	public IApiProcLogVO generateErrorLog(HttpServletRequest request, Exception e);
	
	public void setRequestInfo(HttpServletRequest request, IApiProcLogVO logVo);
	
	public void addLastLog(IApiProcLogVO param);
}
