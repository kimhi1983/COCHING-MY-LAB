package com.erns.es.coching.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.erns.es.coching.domain.EsIngredientDTO;
import com.erns.es.coching.domain.EsIngredientSearchDTO;

/**
 * 
 * <p>성분 매핑 정보 DAO Mapper</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Mapper
public interface EsIngredientDAO {

	public EsIngredientDTO loadRawMapping(EsIngredientSearchDTO param);
}
