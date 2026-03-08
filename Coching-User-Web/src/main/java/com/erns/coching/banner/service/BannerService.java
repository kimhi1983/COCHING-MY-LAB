package com.erns.coching.banner.service;

import java.util.List;
import java.util.Map;

import com.erns.coching.banner.domain.BannerSearchDTO;
/**
 * 
 * <p>배너정보 Service</p> 
 *
 * @author Kyungmin Lee
 *
 */
public interface BannerService {

	public List<Map<String, Object>> getList(BannerSearchDTO param);
	public int getListCount(BannerSearchDTO param);
	
	public Map<String, Object> load(BannerSearchDTO param);
	
	public List<Map<String, Object>> getFiles(BannerSearchDTO param);
	public List<Map<String, Object>> getFiles(String fileTypeCd, long bannerSeq);
	
	public int updateClicks(BannerSearchDTO param);	
}
