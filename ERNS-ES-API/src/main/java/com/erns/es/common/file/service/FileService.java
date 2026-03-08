package com.erns.es.common.file.service;

import java.util.List;
import java.util.Map;

import com.erns.es.common.file.domain.FileSearchDTO;
import com.erns.es.common.file.domain.FileVO;

/**
 * 
 * <p>첨부파일 Service</p> 
 *
 * @author Hunwoo Park 
 *
 */
public interface FileService {
	
	public List<Map<String, Object>> getList(FileSearchDTO param);	
	public int getListCount(FileSearchDTO param);
	public List<Map<String, Object>> selectByRefId(FileSearchDTO param);	
	
	public Map<String, Object> load(FileSearchDTO param);
	
	public int insert(FileVO param);
	public int update(FileVO param);
	public int delete(String fileId);
	
	public int updateRefSeq(FileVO param);
	public int updateOrder(FileVO param);
	public int updateDelYn(FileVO param);
	public int updateDelYnByRefId(FileVO param);
	public int deleteByRefId(FileVO param);

}
