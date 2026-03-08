package com.erns.coching.search.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.search.domain.IngredientNationLimitSearchDTO;
import com.erns.coching.search.domain.SuggestionSearchDTO;
import com.erns.coching.search.mapper.SearchDAO;
import com.erns.coching.search.service.SearchService;

/**
 * 
 * <p>검색 Service</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Transactional
@Service
public class SearchServiceImpl implements SearchService {

  @Autowired
  private SearchDAO dao;

  @Override
  public List<Map<String, Object>> getSuggetionList(SuggestionSearchDTO param) {
    return dao.getSuggetionList(param);
  }

  @Override
  public Map<String, Object> getNationLmitListForIngredient(IngredientNationLimitSearchDTO param) {
    return dao.getNationLmitListForIngredient(param);
  }
  
  @Override
  public Map<String, Object> getNationExpLmitListForIngredient(IngredientNationLimitSearchDTO param) {
    return dao.getNationExpLmitListForIngredient(param);
  }
}
