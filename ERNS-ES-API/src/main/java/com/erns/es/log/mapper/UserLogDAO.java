package com.erns.es.log.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.es.log.domain.UserLogSearchDTO;
import com.erns.es.log.domain.UserLogVO;

/**
 * 
 * <p>사용자 로그 Mapper</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Mapper
public interface UserLogDAO {
	
	public int insert(UserLogVO param);
	public List<Map<String, Object>> getList(UserLogSearchDTO param); 
	public int getListCount(UserLogSearchDTO param); 
	
	public UserLogVO loadJson(UserLogVO param);

}
