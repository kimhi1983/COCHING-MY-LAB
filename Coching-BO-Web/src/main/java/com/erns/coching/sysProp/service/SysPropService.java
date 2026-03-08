package com.erns.coching.sysProp.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.erns.coching.sysProp.domain.SysPropSearchDTO;
import com.erns.coching.sysProp.domain.SysPropVO;

/**
 * <p>시스템설정 SysPropService</p>
 *
 * @author 	jsjeong@erns.co.kr
 *
 */
public interface SysPropService {

	public List<Map<String, Object>> getList(SysPropSearchDTO param);
	public int getListCount(SysPropSearchDTO param);
	public Map<String, Object> load(SysPropSearchDTO param);
	public SysPropVO loadVo(String sysKey);

	public int setSysProp(SysPropVO param);
	public int setSysProp(Collection<SysPropVO> list);
	public int delete(SysPropVO param);
	public int delete(Collection<SysPropVO> list);
}
