package com.erns.coching.search.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.search.domain.RecommendKeywordSearchDTO;
import com.erns.coching.search.domain.RecommendKeywordVO;
import com.erns.coching.search.mapper.RecommendKeywordDAO;
import com.erns.coching.search.service.RecommendKeywordService;

/**
 * 
 * <p>추천 검색어 Service</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Transactional
@Service
public class RecommendKeywordServiceImpl implements RecommendKeywordService {
	
	@Autowired
	private RecommendKeywordDAO dao;

	@Override
	public List<Map<String, Object>> getList(RecommendKeywordSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(RecommendKeywordSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public RecommendKeywordVO load(long seq) {
		return dao.load(seq);
	}

	@Override
	public int insert(RecommendKeywordVO param) {
		return dao.insert(param);
	}

	@Override
	public int update(RecommendKeywordVO param) {
		return dao.update(param);
	}

	@Override
	public int merge(RecommendKeywordVO param) {
		if(param.getSeq() > 0){
			return update(param);
		}
		return insert(param);
	}

	@Override
	public int merge(Collection<RecommendKeywordVO> list) {
		int retCnt = 0;

		for(RecommendKeywordVO vo : list){
			retCnt += merge(vo);
		}
		return retCnt;
	}

	@Override
	public int updateSortOrder(Collection<RecommendKeywordVO> list) {
		int retCnt = 0;
		
		for(RecommendKeywordVO param : list) {
			retCnt =+ dao.updateSortOrder(param);
		}
		
		return retCnt; 
	}

	@Override
	public int updateUseYn(Collection<RecommendKeywordVO> list) {
		int retCnt = 0;
		
		for(RecommendKeywordVO param : list) {
			retCnt =+ dao.updateUseYn(param);
		}
		
		return retCnt;
	}

	@Override
	public int updateDelYn(Collection<RecommendKeywordVO> list) {
		int retCnt = 0;
		
		for(RecommendKeywordVO param : list) {
			retCnt =+ dao.updateDelYn(param);
		}
		
		return retCnt;
	}

	@Override
	public int delete(long seq) {
		return dao.delete(seq);
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
