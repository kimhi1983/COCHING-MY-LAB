package com.erns.coching.code.service;

import com.erns.coching.code.domain.CodeMasterVO;
import com.erns.coching.code.domain.CodeSearchDTO;

import java.util.List;
import java.util.Map;

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
