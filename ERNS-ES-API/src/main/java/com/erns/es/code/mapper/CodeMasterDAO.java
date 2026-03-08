package com.erns.es.code.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.es.code.domain.CodeMasterVO;
import com.erns.es.code.domain.CodeSearchDTO;

/**
 *
 * <p>공통코드 마스터 DAO Mapper</p>
 *
 * @author Hunwoo Park
 *
 */
@Mapper
public interface CodeMasterDAO {

	public List<Map<String, Object>> getList(CodeSearchDTO param);
	public int getListCount(CodeSearchDTO param);
	public int insert(CodeMasterVO param);
	public int update(CodeMasterVO param);
	public int updateUseYn(CodeMasterVO param);
	public int updateDelYn(CodeMasterVO param);
	public int delete(String grpCode);

}
