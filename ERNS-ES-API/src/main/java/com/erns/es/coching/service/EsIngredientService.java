package com.erns.es.coching.service;

import com.erns.es.coching.domain.EsIngredientDTO;
import com.erns.es.coching.domain.EsIngredientSearchDTO;

public interface EsIngredientService {
	
	public EsIngredientDTO loadRawMapping(EsIngredientSearchDTO param);
	
}
