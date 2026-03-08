package com.erns.es.coching.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.es.coching.domain.EsIngredientDTO;
import com.erns.es.coching.domain.EsIngredientSearchDTO;
import com.erns.es.coching.mapper.EsIngredientDAO;
import com.erns.es.coching.service.EsIngredientService;

/**
 * 
 * <p>성분 매핑 정보 Service</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Service
@Transactional
public class EsIngredientServiceImpl implements EsIngredientService {
	
	@Autowired
	private EsIngredientDAO dao;

	@Override
	public EsIngredientDTO loadRawMapping(EsIngredientSearchDTO param){
		return dao.loadRawMapping(param);
	}
}
