package com.erns.es.coching.service;

import java.util.List;

import com.erns.es.coching.domain.EsCochingIngredientVO;
import com.erns.es.coching.domain.EsIngredientSearchDTO;

public interface EsCochingIngredientService {
	
	public EsCochingIngredientVO load(int id);
	
	public EsCochingIngredientVO load(EsIngredientSearchDTO param);
	
	public List<EsCochingIngredientVO> getList(EsIngredientSearchDTO param);
	
	public int getListCount(EsIngredientSearchDTO param);

}
