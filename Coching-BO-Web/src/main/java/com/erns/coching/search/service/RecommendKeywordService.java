package com.erns.coching.search.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.erns.coching.search.domain.RecommendKeywordSearchDTO;
import com.erns.coching.search.domain.RecommendKeywordVO;

/**
 * 
 * <p>추천 검색어 Service</p> 
 *
 * @author Hunwoo Park 
 *
 */
public interface RecommendKeywordService {

	public List<Map<String, Object>> getList(RecommendKeywordSearchDTO param);
	public int getListCount(RecommendKeywordSearchDTO param);
	public RecommendKeywordVO load(long seq);

	public int insert(RecommendKeywordVO param);
	public int update(RecommendKeywordVO param);
	
	public int merge(RecommendKeywordVO param);
	public int merge(Collection<RecommendKeywordVO> list);

	public int updateSortOrder(Collection<RecommendKeywordVO> list);
	public int updateUseYn(Collection<RecommendKeywordVO> list);
	public int updateDelYn(Collection<RecommendKeywordVO> list);	
	
	public int delete(long seq);
	public int delete(Collection<Long> seqs);
}
