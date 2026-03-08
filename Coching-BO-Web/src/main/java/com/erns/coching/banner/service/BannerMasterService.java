package com.erns.coching.banner.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.erns.coching.banner.domain.BannerMasterSearchDTO;
import com.erns.coching.banner.domain.BannerMasterVO;

/**
 * 
 * <p>배너마스터 Service</p> 
 *
 * @author Kyungmin Lee
 *
 */
public interface BannerMasterService {
	public List<Map<String, Object>> getList(BannerMasterSearchDTO param);
	public int getListCount	(BannerMasterSearchDTO param);
	
	public Map<String, Object> load(BannerMasterSearchDTO param); 
	
	public int insert(BannerMasterVO param);
	public int update(BannerMasterVO param);
	public int updateUseYn(BannerMasterVO param);	
	
	public int updateState(Collection<BannerMasterVO> list);
	
	public int delete(String bannerMstCd);
}
