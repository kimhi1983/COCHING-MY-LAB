package com.erns.coching.log.service;

import java.util.List;
import java.util.Map;

import com.erns.coching.log.domain.ApiLogSearchDTO;

/**
 * 
 * <p>API 로그 관리 Service</p> 
 *
 * @author Hunwoo Park 
 *
 */
public interface ApiLogManageService {

	/**
	 * 로그 목록 조회
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> getList(ApiLogSearchDTO param);

	/**
	 * 로그 목록 count
	 * @param param
	 * @return
	 */
	public int getListCount(ApiLogSearchDTO param);
}
