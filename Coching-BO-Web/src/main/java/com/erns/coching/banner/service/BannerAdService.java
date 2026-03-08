package com.erns.coching.banner.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.erns.coching.banner.domain.BannerAdSearchDTO;
import com.erns.coching.banner.domain.BannerAdVO;
/**
 * 
 * <p>배너 광고 정보 Service</p> 
 *
 * @author Kyungmin Lee
 *
 */
public interface BannerAdService {

	public List<Map<String, Object>> getList(BannerAdSearchDTO param);
	public int getListCount(BannerAdSearchDTO param);
	
	public Map<String, Object> load(BannerAdSearchDTO param);
	
	public int insert(BannerAdVO param);
	public int update(BannerAdVO param);
	
	public Map<String, Object> insert(BannerAdVO bannerVO, List<MultipartFile> newFiles) throws IllegalStateException, IOException;
	public Map<String, Object> update(BannerAdVO bannerVO, List<MultipartFile> addFiles, String strDelFileIds) throws IllegalStateException, IOException;
	
	public List<Map<String, Object>> getFiles(BannerAdSearchDTO param);
	public List<Map<String, Object>> getFiles(String fileTypeCd, long bannerSeq);
	
	public int updateDispYn(Collection<BannerAdVO> list);
	public int updateUseYn(Collection<BannerAdVO> list);
	public int updateDelYn(BannerAdVO bannerVO);
	public int updateDelYn(Collection<BannerAdVO> list);
	
	public int updateOrder(Collection<BannerAdVO> list);
	public int updateState(Collection<BannerAdVO> list);
	
	public int updateClicks(BannerAdSearchDTO param);
	
	public int delete(BannerAdSearchDTO param);
	public int delete(Collection<BannerAdSearchDTO> seqs);
}
