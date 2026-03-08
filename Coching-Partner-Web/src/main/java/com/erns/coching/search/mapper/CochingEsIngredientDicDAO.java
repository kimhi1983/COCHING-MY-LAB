package com.erns.coching.search.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.search.domain.EsIngredientSearchDTO;

/**
 * 
 * <p>코칭 성분 Mapper</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Mapper
public interface CochingEsIngredientDicDAO {

  public List<Map<String, Object>> getList(EsIngredientSearchDTO param);
  public int getListCount(EsIngredientSearchDTO param);
  public Map<String, Object> load(long rawSeq);
}
