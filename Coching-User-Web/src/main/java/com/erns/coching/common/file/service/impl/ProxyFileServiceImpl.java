package com.erns.coching.common.file.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.common.file.domain.ProxyFileSearchDTO;
import com.erns.coching.common.file.domain.ProxyFileVO;
import com.erns.coching.common.file.mapper.ProxyFileDAO;
import com.erns.coching.common.file.service.ProxyFileService;


/**
 * 
 * <p>프록시파일 Service</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Service
@Transactional
public class ProxyFileServiceImpl implements ProxyFileService{

	@Autowired
	private ProxyFileDAO dao;

	@Override
	public List<Map<String, Object>> getList(ProxyFileSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(ProxyFileSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public Map<String, Object> load(ProxyFileSearchDTO param) {
		return dao.load(param);
	}

	@Override
	public ProxyFileVO loadVo(String fileId) {
		return dao.loadVo(fileId);
	}

	@Override
	public ProxyFileVO loadVoByOrgUrl(String orgUrl) {
		return dao.loadVoByOrgUrl(orgUrl);
	}

	@Override
	public int insert(ProxyFileVO param) {
		return dao.insert(param);
	}

	@Override
	public int update(ProxyFileVO param) {
		return dao.update(param);
	}

	@Override
	public int updateDelYn(ProxyFileVO param) {
		return dao.updateDelYn(param);
	}

	@Override
	public int delete(String fileId) {
		return dao.delete(fileId);
	}

	@Override
	public int deleteByOrgUrl(String orgUrl) {
		return dao.deleteByOrgUrl(orgUrl);
	}

	

}
