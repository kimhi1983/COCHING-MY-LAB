package com.erns.coching.common.log.domain;

import java.util.Map;

public interface IApiProcLogVO {

  public void setProcStatus(String status);
	
	public void setLogType(String logType);
	
	public void setParamMap(Map<String, Object> apiParam);
	
	public void setResData(Map<String, Object> resDataMap);
}
