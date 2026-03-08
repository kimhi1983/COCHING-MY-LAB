package com.erns.coching.sysProp.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.sysProp.domain.SysPropSearchDTO;
import com.erns.coching.sysProp.domain.SysPropVO;

/**
 * <p>누진세율 TaxDAO</p>
 *
 * @author 	jsjeong@erns.co.kr
 *
 */
@Mapper
public interface SysPropDAO {
	public List<Map<String , Object>> getList(SysPropSearchDTO param);
	public int getListCount(SysPropSearchDTO param);
	public Map<String, Object> load(SysPropSearchDTO param);
	public SysPropVO loadVo(String sysKey);

}
