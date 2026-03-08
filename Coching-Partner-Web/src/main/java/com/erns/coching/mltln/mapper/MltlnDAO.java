package com.erns.coching.mltln.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.mltln.domain.MltlnSearchDTO;
import com.erns.coching.mltln.domain.MltlnVO;

/**
 *
 * <p>다국어 마스터 DAO Mapper</p>
 *
 * @author Kyungmin Lee
 *
 */
@Mapper
public interface MltlnDAO {

	public List<Map<String, Object>> getList(MltlnSearchDTO param);
	public List<MltlnVO> getListVo(MltlnSearchDTO param);
	public int getListCount(MltlnSearchDTO param);
	
	public Map<String, Object> load(MltlnSearchDTO param);
	
	public int isHaveCode(MltlnSearchDTO param);

	public int insert(MltlnVO param);
}
