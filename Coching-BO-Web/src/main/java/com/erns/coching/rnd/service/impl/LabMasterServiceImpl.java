package com.erns.coching.rnd.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.rnd.domain.LabMasterSearchDTO;
import com.erns.coching.rnd.domain.LabMasterVO;
import com.erns.coching.rnd.mapper.LabMasterDAO;
import com.erns.coching.rnd.service.LabMasterService;

/**
 *
 * <p>연구실 마스터 Service</p>
 *
 * @author Hunwoo Park
 *
 */
@Service
@Transactional
public class LabMasterServiceImpl implements LabMasterService{

	@Autowired
	private LabMasterDAO dao;

	@Override
	public List<Map<String, Object>> getList(LabMasterSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(LabMasterSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public Map<String, Object> load(LabMasterSearchDTO param) {
		return dao.load(param);
	}

	@Override
	public int insert(LabMasterVO param) {
		return dao.insert(param);
	}

	@Override
	public int update(LabMasterVO param) {
		return dao.update(param);
	}

	@Override
	public int update(Collection<LabMasterVO> list) {
		int retCnt = 0;

		for(LabMasterVO param : list){
			retCnt += dao.update(param);
		}

		return retCnt;
	}

	@Override
	public int delete(long labMstSeq) {
		return dao.delete(labMstSeq);
	}

	@Override
	public int delete(Collection<Long> seqs) {
		int retCnt = 0;

		for(Long seq : seqs){
			retCnt += delete(seq);
		}

		return retCnt;
	}

}

