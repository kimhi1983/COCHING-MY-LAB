package com.erns.coching.search.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.search.domain.EsIngredientSearchDTO;
import com.erns.coching.search.mapper.CochingEsIngredientDicDAO;
import com.erns.coching.search.service.CochingEsIngredientDicService;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * <p>
 * 코칭 ES용 성분 Service
 * </p>
 *
 * @author Hunwoo Park
 *
 */
@Slf4j
@Transactional
@Service
public class CochingEsIngredientDicServiceImpl implements CochingEsIngredientDicService {

  private CochingEsIngredientDicDAO dao;

  @Autowired
  public CochingEsIngredientDicServiceImpl(CochingEsIngredientDicDAO dao) {
    this.dao = dao;
  }

  @Override
  public List<Map<String, Object>> getList(EsIngredientSearchDTO param) {
    return dao.getList(param);
  }

  @Override
  public int getListCount(EsIngredientSearchDTO param) {
    return dao.getListCount(param);
  }

  @Override
  public Map<String, Object> load(long rawSeq) {
    return dao.load(rawSeq);
  }

}
