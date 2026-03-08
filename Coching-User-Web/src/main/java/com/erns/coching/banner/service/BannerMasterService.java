package com.erns.coching.banner.service;

import java.util.List;
import java.util.Map;

import com.erns.coching.banner.domain.BannerMasterVO;
import com.erns.coching.banner.domain.BannerSearchDTO;

/**
 * 
 * <p>배너마스터 Service</p> 
 *
 * @author Kyungmin Lee
 *
 */
public interface BannerMasterService {
	public List<Map<String, Object>> getList(BannerSearchDTO param);
	public int getListCount	(BannerSearchDTO param);
	
	public Map<String, Object> load(BannerSearchDTO param); 
	
	public int insert(BannerMasterVO param);
	public int update(BannerMasterVO param);
	public int updateUseYn(BannerMasterVO param);	
	
	public int delete(String bannerMstCd);
}
