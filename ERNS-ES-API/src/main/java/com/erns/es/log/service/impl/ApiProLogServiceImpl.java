package com.erns.es.log.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.es.log.domain.ApiProcLogSearchDTO;
import com.erns.es.log.domain.ApiProcLogVO;
import com.erns.es.log.mapper.ApiProcLogDAO;
import com.erns.es.log.service.ApiProcLogService;

/**
 * 
 * <p>사용자 로그 Service</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Service
@Transactional
public class ApiProLogServiceImpl implements ApiProcLogService {
	
	@Autowired
	private ApiProcLogDAO dao;

	@Override
	public int addLog(ApiProcLogVO param) {
		return dao.insert(param);
	}

	@Override
	public List<Map<String, Object>> selectList(ApiProcLogSearchDTO param) {
		return dao.selectList(param);
	}

	@Override
	public int selectListCount(ApiProcLogSearchDTO param) {
		return dao.selectListCount(param);
	}

}
