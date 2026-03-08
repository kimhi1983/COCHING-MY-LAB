package com.erns.coching.cochingtv.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.erns.coching.cochingtv.domain.CochingtvSearchDTO;
import com.erns.coching.cochingtv.domain.CochingtvVO;
/**
 * 
 * <p>코칭TV Service</p> 
 *
 * @author Kyungmin Lee
 *
 */
public interface CochingtvService {

	public List<Map<String, Object>> getList(CochingtvSearchDTO param);
	public int getListCount(CochingtvSearchDTO param);
	
	public Map<String, Object> load(CochingtvSearchDTO param);
	
	public Map<String, Object> insert(CochingtvVO cochingtvVO) throws IllegalStateException, IOException;
	
	public int updateState(Collection<CochingtvVO> list);
	public int updateDelYn(Collection<CochingtvVO> seqs);
	public int updateOrder(Collection<CochingtvVO> list);
	
	public Map<String, Object> getYoutubeInfo(CochingtvSearchDTO param);
}
