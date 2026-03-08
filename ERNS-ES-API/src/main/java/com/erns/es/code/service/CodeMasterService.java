package com.erns.es.code.service;

import java.util.List;
import java.util.Map;

import com.erns.es.code.domain.CodeMasterVO;
import com.erns.es.code.domain.CodeSearchDTO;

/**
 *
 * <p>공통코드마스터 관리 Service</p>
 *
 * @author Hunwoo Park
 *
 */
public interface CodeMasterService {

	public List<Map<String, Object>> getList(CodeSearchDTO param);
	public int getListCount(CodeSearchDTO param);
	public int insert(CodeMasterVO param);
	public int update(CodeMasterVO param);
	public int updateUseYn(CodeMasterVO param);
	public int updateDelYn(CodeMasterVO param);
	public int delete(String grpCode);
}
