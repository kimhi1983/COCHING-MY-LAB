package com.erns.coching.log.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.log.domain.ApiLogSearchDTO;
import com.erns.coching.log.domain.ApiLogVO;

/**
 * 
 * <p>API 로그 Mapper</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Mapper
public interface ApiLogDAO {

	int insert(ApiLogVO vo);

	List<Map<String, Object>> getList(ApiLogSearchDTO param);
	int getListCount(ApiLogSearchDTO param);

}
