package com.erns.coching.popup.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.erns.coching.popup.domain.PopupSearchDTO;
import com.erns.coching.popup.domain.PopupVO;

/**
 * 
 * <p>팝업정보 Service</p> 
 *
 * @author Hunwoo Park 
 *
 */
public interface PopupService {

	public List<Map<String, Object>> getList(PopupSearchDTO param);
	public int getListCount(PopupSearchDTO param);
	
	public Map<String, Object> load(PopupSearchDTO param);
	public int insert(PopupVO param);
	public int update(PopupVO param);

	public int updateUseYn(PopupVO param);	
	public int updateDelYn(PopupVO param);
	public int updateState(PopupVO param);
	
	public Map<String, Object> insert(PopupVO popupVO, List<MultipartFile> newFiles) throws IllegalStateException, IOException;
	public Map<String, Object> update(PopupVO popupVO, List<MultipartFile> addFiles, String strDelFileIds) throws IllegalStateException, IOException;
	
	public List<Map<String, Object>> getFiles(PopupSearchDTO param);
	public List<Map<String, Object>> getFiles(String fileTypeCd, long popupSeq);
	
	public int updateState(Collection<PopupVO> list);
	public int updateOrder(Collection<PopupVO> list);
	
	public int delete(long popupSeq);
	public int delete(Collection<Long> seqs);
}
