package com.erns.coching.common.file.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.common.file.domain.FileSearchDTO;
import com.erns.coching.common.file.domain.FileVO;
import com.erns.coching.common.file.mapper.FileDAO;
import com.erns.coching.common.file.service.FileService;


/**
 * 
 * <p>첨부파일 Service</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Service
@Transactional
public class FileServiceImpl implements FileService{

	@Autowired
	private FileDAO dao;

	@Override
	public List<Map<String, Object>> getList(FileSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(FileSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public List<Map<String, Object>> selectByRefId(FileSearchDTO param) {
		return dao.selectByRefId(param);
	}

	@Override
	public Map<String, Object> load(FileSearchDTO param) {
		return dao.load(param);
	}

	@Override
	public FileVO loadVo(String fileId) {
		return dao.loadVo(fileId);
	}

	@Override
	public int insert(FileVO param) {
		return dao.insert(param);
	}

	@Override
	public int update(FileVO param) {
		return dao.update(param);
	}
	
	@Override
	public int delete(String fileId) {
		return dao.delete(fileId);
	}
	
	@Override
	public int updateRefSeq(FileVO param) {
		return dao.updateRefSeq(param);
	}
	
	@Override
	public int updateOrder(FileVO param) {
		return dao.updateOrder(param);
	}

	@Override
	public int updateDelYn(FileVO param) {
		return dao.updateDelYn(param);
	}

	@Override
	public int updateDelYnByRefId(FileVO param) {
		return dao.updateDelYnByRefId(param);
	}

	@Override
	public int deleteByRefId(FileVO param) {
		return dao.deleteByRefId(param);
	}

	@Override
	public List<FileVO> getWillRemovePreviewFiles() {
		return dao.getWillRemovePreviewFiles();
	}
}
