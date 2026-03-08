package com.erns.coching.rawType.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.rawType.domain.RawTypeSearchDTO;
import com.erns.coching.rawType.domain.RawTypeVO;
import com.erns.coching.rawType.mapper.RawTypeDAO;
import com.erns.coching.rawType.service.RawTypeService;

/**
 *
 * <p>원료 Service</p>
 *
 */
@Transactional
@Service
public class RawTypeTypeServiceImpl implements RawTypeService {

	@Autowired
	private RawTypeDAO dao;

	@Override
	public List<Map<String, Object>> getList(RawTypeSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(RawTypeSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public Map<String, Object> load(RawTypeSearchDTO param) {
		return dao.load(param);
	}

	@Override
	public int isHaveCode(RawTypeSearchDTO param) {
		return dao.isHaveCode(param);
	}

	@Override
	public int insert(RawTypeVO param) {
		return dao.insert(param);
	}

	@Override
	public int update(RawTypeVO param) {
		return dao.update(param);
	}
	
	@Override
	public int updateState(Collection<RawTypeVO> list) {
		int retCnt = 0;

		for(RawTypeVO param : list){
			retCnt += dao.updateState(param);
		}

		return retCnt;
	}

	@Override
	public int updateUseYn(RawTypeVO param) {
		return dao.updateUseYn(param);
	}
}
