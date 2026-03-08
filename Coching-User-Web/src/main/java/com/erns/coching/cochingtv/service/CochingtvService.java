package com.erns.coching.cochingtv.service;

import java.util.List;
import java.util.Map;

import com.erns.coching.cochingtv.domain.CochingtvSearchDTO;
/**
 * 
 * <p>코칭TV Service</p> 
 *
 * @author Kyungmin Lee
 *
 */
public interface CochingtvService {

	public Map<String, Object> load(CochingtvSearchDTO param);
	
	public List<Map<String, Object>> getList(CochingtvSearchDTO param);
	public int getListCount(CochingtvSearchDTO param);
	
	public Map<String, Object> getYoutubeInfo(CochingtvSearchDTO param);
	
}
