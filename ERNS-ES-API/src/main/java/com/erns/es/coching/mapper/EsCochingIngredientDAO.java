package com.erns.es.coching.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.erns.es.coching.domain.EsCochingIngredientVO;
import com.erns.es.coching.domain.EsIngredientSearchDTO;

/**
 * 
 * <p>코칭 성분 정보 DAO Mapper</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Mapper
public interface EsCochingIngredientDAO {
	
	public EsCochingIngredientVO loadByIds(EsIngredientSearchDTO param);
	
	public List<EsCochingIngredientVO> getList(EsIngredientSearchDTO param);
	public int getListCount(EsIngredientSearchDTO param);

}
