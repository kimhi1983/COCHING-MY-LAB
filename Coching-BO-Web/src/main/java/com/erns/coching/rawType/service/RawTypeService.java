package com.erns.coching.rawType.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.erns.coching.rawType.domain.RawTypeSearchDTO;
import com.erns.coching.rawType.domain.RawTypeVO;

/**
 *
 * <p>원료 Service</p>
 *
 */
public interface RawTypeService {

	public List<Map<String, Object>> getList(RawTypeSearchDTO param);
	public int getListCount(RawTypeSearchDTO param);

	public Map<String, Object> load(RawTypeSearchDTO param);
	public int isHaveCode(RawTypeSearchDTO param);

	public int insert(RawTypeVO param);
	public int update(RawTypeVO param);
	public int updateState(Collection<RawTypeVO> list);
	
	public int updateUseYn(RawTypeVO param);
}
