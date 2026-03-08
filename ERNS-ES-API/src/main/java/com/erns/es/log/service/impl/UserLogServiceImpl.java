package com.erns.es.log.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.es.log.domain.UserLogSearchDTO;
import com.erns.es.log.domain.UserLogVO;
import com.erns.es.log.mapper.UserLogDAO;
import com.erns.es.log.service.UserLogService;

/**
 * 
 * <p>사용자 로그 Service</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Service
@Transactional
public class UserLogServiceImpl implements UserLogService{
	
	@Autowired
	private UserLogDAO dao;

	@Override
	public int insert(UserLogVO param) {
		return dao.insert(param);
	}

	@Override
	public List<Map<String, Object>> getList(UserLogSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(UserLogSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public UserLogVO loadJson(UserLogVO param) {
		return dao.loadJson(param);
	}

}
