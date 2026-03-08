package com.erns.coching.rnd.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.erns.coching.rnd.domain.LabMasterSearchDTO;
import com.erns.coching.rnd.domain.LabMasterVO;

/**
 * 
 * <p>연구실 마스터 Service</p> 
 *
 * @author Hunwoo Park 
 *
 */
public interface LabMasterService {

	public List<Map<String, Object>> getList(LabMasterSearchDTO param);
	public int getListCount(LabMasterSearchDTO param);
	
	public Map<String, Object> load(LabMasterSearchDTO param);
	public int insert(LabMasterVO param);
	public int update(LabMasterVO param);
	public int update(Collection<LabMasterVO> list);
	
	public int delete(long labMstSeq);
	public int delete(Collection<Long> seqs);
}

