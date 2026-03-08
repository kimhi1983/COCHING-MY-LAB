package com.erns.coching.search.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.search.domain.HwBrandSearchDTO;
import com.erns.coching.search.domain.HwBrandVO;

/*
 * 
 * <p>화해 브랜드 Mapper</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Mapper
public interface HwBrandDAO {

  public List<Map<String, Object>> getList(HwBrandSearchDTO param);
  public List<HwBrandVO> getListVo(HwBrandSearchDTO param);
	public int getListCount(HwBrandSearchDTO param);
	public HwBrandVO load(long seq);

	public int insert(HwBrandVO param);		
	public int delete(long seq);
	public int deleteAll();
}
