package com.erns.coching.rnd.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.erns.coching.rnd.domain.AiLabResSearchDTO;
import com.erns.coching.rnd.domain.AiLabResVO;

/**
 * 
 * <p>AI 연구실 결과 Service</p> 
 *
 * @author Hunwoo Park 
 *
 */
public interface AiLabResService {

	public List<Map<String, Object>> getList(AiLabResSearchDTO param);
	public int getListCount(AiLabResSearchDTO param);
	
	public Map<String, Object> load(AiLabResSearchDTO param);
	public int insert(AiLabResVO param);
	public int update(AiLabResVO param);
	public int update(Collection<AiLabResVO> list);
	
	public int delete(long resSeq);
	public int delete(Collection<Long> seqs);
	public int deleteByLabMstSeq(long labMstSeq);
}

