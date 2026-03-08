package com.erns.es.log.service;

import java.util.List;
import java.util.Map;

import com.erns.es.log.domain.ApiProcLogSearchDTO;
import com.erns.es.log.domain.ApiProcLogVO;

/**
 * 
 * <p>API 로그 Service</p> 
 *
 * @author Hunwoo Park 
 *
 */
public interface ApiProcLogService {
	
	/**
	 * 로그 등록
	 * @param param
	 * @return
	 */
	public int addLog(ApiProcLogVO param);
	
	/**
	 * 로그 목록 조회
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> selectList(ApiProcLogSearchDTO param);	
	
	/**
	 * 로그 목록 count
	 * @param param
	 * @return
	 */
	public int selectListCount(ApiProcLogSearchDTO param);
}
