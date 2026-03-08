package com.erns.coching.rnd.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.rnd.domain.LabElementInfSearchDTO;
import com.erns.coching.rnd.domain.LabElementInfVO;

/**
 *
 * <p>연구실 원소 정보 Mapper</p>
 *
 * @author Hunwoo Park
 *
 */
@Mapper
public interface LabElementInfDAO {

	public List<Map<String, Object>> getList(LabElementInfSearchDTO param);
	public int getListCount(LabElementInfSearchDTO param);

	public Map<String, Object> load(LabElementInfSearchDTO param);

	public int insert(LabElementInfVO param);
	public int update(LabElementInfVO param);
	
	public int delete(long labElementSeq);
  public int deleteByLabMstSeq(long labMstSeq);

}

