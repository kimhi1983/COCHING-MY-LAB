package com.erns.coching.rnd.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.erns.coching.rnd.domain.LabElementInfSearchDTO;
import com.erns.coching.rnd.domain.LabElementInfVO;

/**
 * 
 * <p>연구실 원소 정보 Service</p> 
 *
 * @author Hunwoo Park 
 *
 */
public interface LabElementInfService {

	public List<Map<String, Object>> getList(LabElementInfSearchDTO param);
	public int getListCount(LabElementInfSearchDTO param);
	
	public Map<String, Object> load(LabElementInfSearchDTO param);
	public int insert(LabElementInfVO param);
  public int insert(Collection<LabElementInfVO> list);

	public int update(LabElementInfVO param);
	public int update(Collection<LabElementInfVO> list);
	
	public int delete(long labElementSeq);
	public int delete(Collection<Long> seqs);
  public int deleteByLabMstSeq(long labMstSeq);
}

