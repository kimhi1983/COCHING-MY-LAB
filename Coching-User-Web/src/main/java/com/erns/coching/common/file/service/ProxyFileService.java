package com.erns.coching.common.file.service;

import java.util.List;
import java.util.Map;

import com.erns.coching.common.file.domain.ProxyFileSearchDTO;
import com.erns.coching.common.file.domain.ProxyFileVO;

/**
 * 
 * <p>프록시파일 Service</p> 
 *
 * @author Hunwoo Park 
 *
 */
public interface ProxyFileService {
	
	public List<Map<String, Object>> getList(ProxyFileSearchDTO param);	
	public int getListCount(ProxyFileSearchDTO param);
	
	public Map<String, Object> load(ProxyFileSearchDTO param);
	public ProxyFileVO loadVo(String fileId);
	public ProxyFileVO loadVoByOrgUrl(String orgUrl);
	
	public int insert(ProxyFileVO param);
	public int update(ProxyFileVO param);
	public int updateDelYn(ProxyFileVO param);
	public int delete(String fileId);
	public int deleteByOrgUrl(String orgUrl);
}
