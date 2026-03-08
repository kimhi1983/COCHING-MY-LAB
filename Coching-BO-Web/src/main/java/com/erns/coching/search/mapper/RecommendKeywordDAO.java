package com.erns.coching.search.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.search.domain.RecommendKeywordSearchDTO;
import com.erns.coching.search.domain.RecommendKeywordVO;

/**
 * 
 * <p>검색 추천어 Mapper</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Mapper
public interface RecommendKeywordDAO {
	
	public List<Map<String, Object>> getList(RecommendKeywordSearchDTO param);
	public int getListCount(RecommendKeywordSearchDTO param);
	public RecommendKeywordVO load(long seq);

	public int insert(RecommendKeywordVO param);
	public int update(RecommendKeywordVO param);
	public int updateSortOrder(RecommendKeywordVO param);
	public int updateUseYn(RecommendKeywordVO param);
	public int updateDelYn(RecommendKeywordVO param);	
	public int delete(long seq);

}
