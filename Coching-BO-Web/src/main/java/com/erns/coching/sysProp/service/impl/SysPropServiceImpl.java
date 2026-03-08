package com.erns.coching.sysProp.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.sysProp.domain.SysPropSearchDTO;
import com.erns.coching.sysProp.domain.SysPropVO;
import com.erns.coching.sysProp.mapper.SysPropDAO;
import com.erns.coching.sysProp.service.SysPropService;

/**
 * <p>시스템설정 SysPropServiceImpl</p>
 * 
 * @author 	jsjeong@erns.co.kr
 *
 */
@Service
@Transactional
public class SysPropServiceImpl implements SysPropService {

	@Autowired
	private SysPropDAO dao;
	
	@Override
	public List<Map<String, Object>> getList(SysPropSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(SysPropSearchDTO param) {
		return dao.getListCount(param);
	}
	
	@Override
	public Map<String, Object> load(SysPropSearchDTO param) {
		return dao.load(param);
	}

	@Override
	public SysPropVO loadVo(String sysKey) {
		return dao.loadVo(sysKey);
	}
	
	@Override
	public int setSysProp(SysPropVO param) {
		return dao.setSysProp(param);
	}

	@Override
	public int setSysProp(Collection<SysPropVO> list) {
		int ret = 0;

		for(SysPropVO vo : list){
			ret += dao.setSysProp(vo);
		}
		
		return ret;
	}

	@Override
	public int delete(SysPropVO param) {
		return dao.delete(param);
	}

	@Override
	public int delete(Collection<SysPropVO> list) {
		int ret = 0;

		for(SysPropVO vo : list){
			ret += dao.delete(vo);
		}

		return ret;
	}
}
