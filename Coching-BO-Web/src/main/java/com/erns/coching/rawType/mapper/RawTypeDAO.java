package com.erns.coching.rawType.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.rawType.domain.RawTypeSearchDTO;
import com.erns.coching.rawType.domain.RawTypeVO;


/**
 *
 * <p>원료 마스터 정보 Mapper</p>
 *
 * @author Kyungmin Lee
 *
 */
@Mapper
public interface RawTypeDAO {

	public List<Map<String, Object>> getList(RawTypeSearchDTO param);
	public int getListCount(RawTypeSearchDTO param);

	public Map<String, Object> load(RawTypeSearchDTO param);

	public int isHaveCode(RawTypeSearchDTO param);

	public int insert(RawTypeVO param);
	public int update(RawTypeVO param);
	
	public int updateState(RawTypeVO param);
	
	public int updateUseYn(RawTypeVO param);

}
