package com.erns.coching.raw.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.common.service.AbstractCochingApiService;
import com.erns.coching.raw.domain.RawDetailSearchDTO;
import com.erns.coching.raw.domain.RawDetailVO;
import com.erns.coching.raw.domain.RawManagerSearchDTO;
import com.erns.coching.raw.domain.RawMasterSearchDTO;
import com.erns.coching.raw.domain.RawMasterVO;
import com.erns.coching.raw.mapper.RawDAO;
import com.erns.coching.raw.service.RawService;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * <p>원료 Service</p>
 *
 * @author Kyungmin Lee
 *
 */
@Slf4j
@Transactional
@Service
public class RawServiceImpl extends AbstractCochingApiService implements RawService {

	@Autowired
	private RawDAO dao;

	@Override
	public List<Map<String, Object>> getList(RawMasterSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(RawMasterSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public Map<String, Object> load(RawMasterSearchDTO param) {
		return dao.load(param);
	}
	
	@Override
	public int updateState(Collection<RawMasterVO> list) {
		int retCnt = 0;

		for(RawMasterVO param : list){
			retCnt += dao.updateState(param);
		}

		return retCnt;
	}

	@Override
	public List<Map<String, Object>> getElmList(RawMasterSearchDTO param) {
		return dao.getElmList(param);
	}

	@Override
	public List<Map<String, Object>> getDocList(RawMasterSearchDTO param) {
		return dao.getDocList(param);
	}

	@Override
	public List<Map<String, Object>> getTypeList(RawMasterSearchDTO param) {
		return dao.getTypeList(param);
	}

	@Override
	public List<Map<String, Object>> getManagerList(RawManagerSearchDTO param){
		return dao.getManagerList(param);
	}

	@Override
	public Map<String, Object> getManager(RawManagerSearchDTO param){
		return dao.getManager(param);
	}

	@Override
	public Map<String, Object> getDetail(RawDetailSearchDTO param){
		return dao.getDetail(param);
	}

	@Override
	public RawDetailVO loadDetail(RawDetailSearchDTO param){
		return dao.loadDetail(param);
	}

	@Override
	public List<Map<String, Object>> getGarbageList(RawMasterSearchDTO param) {
		return dao.getGarbageList(param);
	}

	@Override
	public int getGarbageListCount(RawMasterSearchDTO param) {
		return dao.getGarbageListCount(param);
	}

	@Override
	public List<Map<String, Object>> getCmTypeList() {
		return dao.getCmTypeList();
	}

}
