package com.erns.coching.search.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.search.domain.IngredientNationLimitSearchDTO;
import com.erns.coching.search.domain.SuggestionSearchDTO;

/**
 * 
 * <p>검색 Mapper</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Mapper
public interface SearchDAO {

  public List<Map<String, Object>> getSuggetionList(SuggestionSearchDTO param);

  public Map<String, Object> getNationLmitListForIngredient(IngredientNationLimitSearchDTO param);
  public Map<String, Object> getNationExpLmitListForIngredient(IngredientNationLimitSearchDTO param);
}
