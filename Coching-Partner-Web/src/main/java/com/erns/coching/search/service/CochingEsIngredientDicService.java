package com.erns.coching.search.service;

import java.util.List;
import java.util.Map;

import com.erns.coching.search.domain.EsIngredientSearchDTO;

public interface CochingEsIngredientDicService {

  public List<Map<String, Object>> getList(EsIngredientSearchDTO param);
  public int getListCount(EsIngredientSearchDTO param);
  public Map<String, Object> load(long rawSeq);
}
