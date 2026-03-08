package com.erns.es.log.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.es.log.domain.ApiProcLogSearchDTO;
import com.erns.es.log.domain.ApiProcLogVO;

/**
 * 
 * <p>API 로그 Mapper</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Mapper
public interface ApiProcLogDAO {
	
	int insert(ApiProcLogVO vo);
	
	List<Map<String, Object>> selectList(ApiProcLogSearchDTO param);
	int selectListCount(ApiProcLogSearchDTO param);

}
