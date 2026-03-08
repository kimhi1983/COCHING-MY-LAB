package com.erns.es.code.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.es.code.domain.CodeMasterVO;
import com.erns.es.code.domain.CodeSearchDTO;
import com.erns.es.code.mapper.CodeMasterDAO;
import com.erns.es.code.service.CodeMasterService;

/**
 *
 * <p>공통코드마스터 관리 Service</p>
 *
 * @author Hunwoo Park
 *
 */
@Transactional
@Service
public class CodeMasterServiceImpl implements CodeMasterService{

	@Autowired
	private CodeMasterDAO dao;

	@Override
	public List<Map<String, Object>> getList(CodeSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(CodeSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public int insert(CodeMasterVO param) {
		return dao.insert(param);
	}

	@Override
	public int update(CodeMasterVO param) {
		return dao.update(param);
	}

	@Override
	public int updateUseYn(CodeMasterVO param) {
		return dao.updateUseYn(param);
	}
	
	@Override
	public int updateDelYn(CodeMasterVO param) {
		return dao.updateDelYn(param);
	}

	@Override
	public int delete(String grpCode) {
		return dao.delete(grpCode);
	}

}
