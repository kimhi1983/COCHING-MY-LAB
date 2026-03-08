package com.erns.coching.common.file.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.common.file.domain.ProxyFileSearchDTO;
import com.erns.coching.common.file.domain.ProxyFileVO;

/**
*
* <p>프록시 파일 Mapper</p>
*
* @author Hunwoo Park
*
*/
@Mapper
public interface ProxyFileDAO {

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
