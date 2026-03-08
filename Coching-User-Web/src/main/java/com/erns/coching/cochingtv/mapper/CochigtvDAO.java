package com.erns.coching.cochingtv.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.cochingtv.domain.CochingtvSearchDTO;

/**
 *
 * <p>코칭TV Mapper</p>
 *
 * @author Kyungmin Lee
 *
 */
@Mapper
public interface CochigtvDAO {

	public Map<String, Object> load(CochingtvSearchDTO param);

	public List<Map<String, Object>> getList(CochingtvSearchDTO param);
	public int getListCount(CochingtvSearchDTO param);
	
}
