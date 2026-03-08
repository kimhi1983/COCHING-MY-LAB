package com.erns.coching.search.service;

import java.util.List;
import java.util.Map;

import com.erns.coching.search.domain.IngredientNationLimitSearchDTO;
import com.erns.coching.search.domain.SuggestionSearchDTO;

/**
 * 검색서비스
 */
public interface SearchService {

  public List<Map<String, Object>> getSuggetionList(SuggestionSearchDTO param);

  public Map<String, Object> getNationLmitListForIngredient(IngredientNationLimitSearchDTO param);
  public Map<String, Object> getNationExpLmitListForIngredient(IngredientNationLimitSearchDTO param);
}
