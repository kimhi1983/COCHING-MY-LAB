package com.erns.coching.rnd.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.rnd.domain.LabMasterSearchDTO;
import com.erns.coching.rnd.domain.LabMasterVO;

/**
 *
 * <p>연구실 마스터 Mapper</p>
 *
 * @author Hunwoo Park
 *
 */
@Mapper
public interface LabMasterDAO {

	public List<Map<String, Object>> getList(LabMasterSearchDTO param);
	public int getListCount(LabMasterSearchDTO param);

	public Map<String, Object> load(LabMasterSearchDTO param);

	public int insert(LabMasterVO param);

	public int update(LabMasterVO param);
	
	public int delete(long labMstSeq);

}

