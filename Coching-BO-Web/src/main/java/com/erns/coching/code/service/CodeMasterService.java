package com.erns.coching.code.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.erns.coching.code.domain.CodeMasterSearchDTO;
import com.erns.coching.code.domain.CodeMasterVO;

/**
 *
 * <p>공통코드마스터 관리 Service</p>
 *
 * @author Hunwoo Park
 *
 */
public interface CodeMasterService {

	public List<Map<String, Object>> getList(CodeMasterSearchDTO param);
	public int getListCount(CodeMasterSearchDTO param);
	public int insert(CodeMasterVO param);
	public int update(CodeMasterVO param);
	public int updateUseYn(CodeMasterVO param);
	public int updateDelYn(CodeMasterVO param);
	
	public int updateState(Collection<CodeMasterVO> list);
	
	public int delete(String grpCode);
}
