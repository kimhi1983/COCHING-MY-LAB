package com.erns.es.log.service;

import java.util.List;
import java.util.Map;

import com.erns.es.log.domain.UserLogSearchDTO;
import com.erns.es.log.domain.UserLogVO;

/**
 *
 * <p>사용자 로그 Service</p>
 *
 * @author Hunwoo Park
 *
 */
public interface UserLogService {

	public int insert(UserLogVO param);
	public List<Map<String, Object>> getList(UserLogSearchDTO param);
	public int getListCount(UserLogSearchDTO param);

	public UserLogVO loadJson(UserLogVO param);

}
