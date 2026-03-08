package com.erns.coching.banner.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.banner.domain.BannerAdSearchDTO;
import com.erns.coching.banner.domain.BannerAdVO;

/**
 *
 * <p>배너 광고 Mapper</p>
 *
 * @author Kyungmin Lee
 *
 */
@Mapper
public interface BannerAdDAO {

	public List<Map<String, Object>> getList(BannerAdSearchDTO param);
	public int getListCount(BannerAdSearchDTO param);
	
	public Map<String, Object> load(BannerAdSearchDTO bannerSeq);
	
	public int insert(BannerAdVO param);
	public int update(BannerAdVO param);
	public int updateDispYn(BannerAdVO param);
	public int updateUseYn(BannerAdVO param);
	public int updateDelYn(BannerAdVO param);
	public int updateOrder(BannerAdVO param);
	
	public int updateClicks(BannerAdSearchDTO param);
	public int delete(BannerAdSearchDTO param);

}
