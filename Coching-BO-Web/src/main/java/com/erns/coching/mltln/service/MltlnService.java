package com.erns.coching.mltln.service;

import java.util.List;
import java.util.Map;

import com.erns.coching.mltln.domain.MltlnSearchDTO;
import com.erns.coching.mltln.domain.MltlnVO;



/**
 * <p>다국어 Service</p>
 * 
 * @author 	Kyungmin Lee
 *
 */
public interface MltlnService {
	public List<Map<String, Object>> getList(MltlnSearchDTO param);
	public List<MltlnVO> getListVo(MltlnSearchDTO param);
	public int getListCount(MltlnSearchDTO param);

	public Map<String, Object> load(MltlnSearchDTO param);
	
	public int isHaveCode(MltlnSearchDTO param);
	public void insert(MltlnVO param);
	public void update(MltlnVO param);
	
	public void delete(String code);
	public void deleteAll();
	
	public boolean exportMltlnJson();
	public boolean importMltlnJson(List<MltlnVO> list);
}
