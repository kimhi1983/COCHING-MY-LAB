package com.erns.coching.search.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.erns.coching.search.domain.HwBrandSearchDTO;
import com.erns.coching.search.domain.HwBrandVO;

/**
 * 
 * <p>화해 브랜드 Service</p> 
 *
 * @author Hunwoo Park 
 *
 */
public interface HwBrandService {

	public List<Map<String, Object>> getList(HwBrandSearchDTO param);
	public List<HwBrandVO> getListVo(HwBrandSearchDTO param);
	public int getListCount(HwBrandSearchDTO param);
	public HwBrandVO load(long seq);

	public int insert(HwBrandVO param);
	public int insert(Collection<HwBrandVO> param);
	public int delete(long seq);
	public int deleteAll();
} 
