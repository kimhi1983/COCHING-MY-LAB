package com.erns.coching.rnd.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.rnd.domain.LabElementInfSearchDTO;
import com.erns.coching.rnd.domain.LabElementInfVO;
import com.erns.coching.rnd.mapper.LabElementInfDAO;
import com.erns.coching.rnd.service.LabElementInfService;

/**
 *
 * <p>연구실 원소 정보 Service</p>
 *
 * @author Hunwoo Park
 *
 */
@Service
@Transactional
public class LabElementInfServiceImpl implements LabElementInfService{

	@Autowired
	private LabElementInfDAO dao;

	@Override
	public List<Map<String, Object>> getList(LabElementInfSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(LabElementInfSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public Map<String, Object> load(LabElementInfSearchDTO param) {
		return dao.load(param);
	}

	@Override
	public int insert(LabElementInfVO param) {
		return dao.insert(param);
	}

  @Override
  public int insert(Collection<LabElementInfVO> list) {
    int retCnt = 0;
    for(LabElementInfVO param : list){
      retCnt += dao.insert(param);
    }
    return retCnt;
  }

	@Override
	public int update(LabElementInfVO param) {
		return dao.update(param);
	}

	@Override
	public int update(Collection<LabElementInfVO> list) {
		int retCnt = 0;

		for(LabElementInfVO param : list){
			retCnt += dao.update(param);
		}

		return retCnt;
	}

	@Override
	public int delete(long labElementSeq) {
		return dao.delete(labElementSeq);
	}

	@Override
	public int delete(Collection<Long> seqs) {
		int retCnt = 0;

		for(Long seq : seqs){
			retCnt += delete(seq);
		}

		return retCnt;
	}

  @Override
  public int deleteByLabMstSeq(long labMstSeq) {
    return dao.deleteByLabMstSeq(labMstSeq);
  }
}

