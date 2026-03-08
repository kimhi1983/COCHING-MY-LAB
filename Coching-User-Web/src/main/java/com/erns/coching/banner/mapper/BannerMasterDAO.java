package com.erns.coching.banner.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.banner.domain.BannerMasterVO;
import com.erns.coching.banner.domain.BannerSearchDTO;

/**
 * 
 * <p>배너마스터 Mapper</p> 
 *
 * @author Kyungmin Lee
 *
 */
@Mapper
public interface BannerMasterDAO {

	public List<Map<String, Object>> getList(BannerSearchDTO param);
	public int getListCount	(BannerSearchDTO param);
	
	public Map<String, Object> load(BannerSearchDTO param); 
	
	public int insert(BannerMasterVO param);
	public int update(BannerMasterVO param);
	public int updateUseYn(BannerMasterVO param);	
	
	public int delete(String bannerMstCd);
}
