package com.erns.coching.banner.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.banner.domain.BannerMasterSearchDTO;
import com.erns.coching.banner.domain.BannerMasterVO;

/**
 * 
 * <p>배너마스터 Mapper</p> 
 *
 * @author Kyungmin Lee
 *
 */
@Mapper
public interface BannerMasterDAO {

	public List<Map<String, Object>> getList(BannerMasterSearchDTO param);
	public int getListCount	(BannerMasterSearchDTO param);
	
	public Map<String, Object> load(BannerMasterSearchDTO param); 
	
	public int insert(BannerMasterVO param);
	public int update(BannerMasterVO param);
	public int updateUseYn(BannerMasterVO param);	
	
	public int updateState(BannerMasterVO param);
	
	public int delete(String bannerMstCd);
}
