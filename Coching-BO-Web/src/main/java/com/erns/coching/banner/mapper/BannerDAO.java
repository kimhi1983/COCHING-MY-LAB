package com.erns.coching.banner.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.banner.domain.BannerSearchDTO;
import com.erns.coching.banner.domain.BannerVO;

/**
 *
 * <p>배너 Mapper</p>
 *
 * @author Kyungmin Lee
 *
 */
@Mapper
public interface BannerDAO {

	public List<Map<String, Object>> getList(BannerSearchDTO param);
	public int getListCount(BannerSearchDTO param);

	public Map<String, Object> load(BannerSearchDTO param);
	
	public int insert(BannerVO param);

	public int update(BannerVO param);
	public int updateUseYn(BannerVO param);
	public int updateDelYn(BannerVO param);
	public int updateOrder(BannerVO param);
	public int updateState(BannerVO param);
	
	public int delete(long bannerSeq);

}
