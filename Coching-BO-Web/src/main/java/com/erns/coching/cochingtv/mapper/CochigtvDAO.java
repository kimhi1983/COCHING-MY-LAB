package com.erns.coching.cochingtv.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.cochingtv.domain.CochingtvSearchDTO;
import com.erns.coching.cochingtv.domain.CochingtvVO;

/**
 *
 * <p>코칭TV Mapper</p>
 *
 * @author Kyungmin Lee
 *
 */
@Mapper
public interface CochigtvDAO {

	public List<Map<String, Object>> getList(CochingtvSearchDTO param);
	public int getListCount(CochingtvSearchDTO param);

	public Map<String, Object> load(CochingtvSearchDTO param);
	public int insert(CochingtvVO param);
	
	public int updateState(CochingtvVO param);
	public int updateDelYn(CochingtvVO param);
	public int updateOrder(CochingtvVO param);
	public int updateOrderWhenInsert();
}
