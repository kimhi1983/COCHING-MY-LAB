package com.erns.coching.code.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.code.domain.CodeMasterSearchDTO;
import com.erns.coching.code.domain.CodeMasterVO;

/**
 *
 * <p>공통코드 마스터 DAO Mapper</p>
 *
 * @author Hunwoo Park
 *
 */
@Mapper
public interface CodeMasterDAO {

	public List<Map<String, Object>> getList(CodeMasterSearchDTO param);
	public int getListCount(CodeMasterSearchDTO param);
	public int insert(CodeMasterVO param);
	public int update(CodeMasterVO param);
	public int updateUseYn(CodeMasterVO param);
	public int updateDelYn(CodeMasterVO param);
	public int updateState(CodeMasterVO param);
	public int delete(String grpCode);

}
