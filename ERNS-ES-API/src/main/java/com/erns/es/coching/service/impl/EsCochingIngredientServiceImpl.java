package com.erns.es.coching.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.es.coching.domain.EsCochingIngredientVO;
import com.erns.es.coching.domain.EsIngredientSearchDTO;
import com.erns.es.coching.mapper.EsCochingIngredientDAO;
import com.erns.es.coching.service.EsCochingIngredientService;

/**
 * 
 * <p>코칭 성분 정보 Service</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Service
@Transactional
public class EsCochingIngredientServiceImpl implements EsCochingIngredientService {
	
	@Autowired
	private EsCochingIngredientDAO dao;
	
	@Override
	public EsCochingIngredientVO load(int id) {
		EsIngredientSearchDTO param = EsIngredientSearchDTO.builder()
				.idType(EsIngredientSearchDTO.ID_TYPE_RAW)
				.id(""+id)
				.build();
		
		return load(param);
	}

	@Override
	public EsCochingIngredientVO load(EsIngredientSearchDTO param) {
		return dao.loadByIds(param);
	}

	@Override
	public List<EsCochingIngredientVO> getList(EsIngredientSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(EsIngredientSearchDTO param) {
		return dao.getListCount(param);
	}

}
