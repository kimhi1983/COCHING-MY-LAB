package com.erns.coching.banner.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.erns.coching.banner.domain.BannerSearchDTO;
import com.erns.coching.banner.domain.BannerVO;
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
	public int insert(BannerVO param);
	public int update(BannerVO param);

	public int updateUseYn(BannerVO param);	
	public int updateDelYn(BannerVO param);
	public int updateDelYn(Collection<BannerVO> seqs);
	public int updateState(BannerVO param);
	
	public Map<String, Object> insert(BannerVO bannerVO, List<MultipartFile> newFiles) throws IllegalStateException, IOException;
	public Map<String, Object> update(BannerVO bannerVO, List<MultipartFile> addFiles, String strDelFileIds) throws IllegalStateException, IOException;
	
	public List<Map<String, Object>> getFiles(BannerSearchDTO param);
	public List<Map<String, Object>> getFiles(String fileTypeCd, long bannerSeq);
	
	public int updateState(Collection<BannerVO> list);
	public int updateOrder(Collection<BannerVO> list);
		
	public int delete(long bannerSeq);
	public int delete(Collection<Long> seqs);
}
