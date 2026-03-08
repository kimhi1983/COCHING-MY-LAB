package com.erns.coching.rnd.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.rnd.domain.AiLabResSearchDTO;
import com.erns.coching.rnd.domain.AiLabResVO;
import com.erns.coching.rnd.mapper.AiLabResDAO;
import com.erns.coching.rnd.service.AiLabResService;

/**
 *
 * <p>AI 연구실 결과 Service</p>
 *
 * @author Hunwoo Park
 *
 */
@Service
@Transactional
public class AiLabResServiceImpl implements AiLabResService{

	@Autowired
	private AiLabResDAO dao;

	@Override
	public List<Map<String, Object>> getList(AiLabResSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(AiLabResSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public Map<String, Object> load(AiLabResSearchDTO param) {
		return dao.load(param);
	}

	@Override
	public int insert(AiLabResVO param) {
		return dao.insert(param);
	}

	@Override
	public int update(AiLabResVO param) {
		return dao.update(param);
	}

	@Override
	public int update(Collection<AiLabResVO> list) {
		int retCnt = 0;

		for(AiLabResVO param : list){
			retCnt += dao.update(param);
		}

		return retCnt;
	}

	@Override
	public int delete(long resSeq) {
		return dao.delete(resSeq);
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

