package com.erns.coching.code.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.code.domain.CodeSearchDTO;
import com.erns.coching.code.domain.CodeVO;

/**
 *
 * <p>공통코드 DAO Mapper</p>
 *
 * @author Hunwoo Park
 *
 */
@Mapper
public interface CodeDAO {

    List<Map<String, Object>> selectCodeList(CodeSearchDTO param);

    public List<Map<String, Object>> getList(CodeSearchDTO param);
    public int getListCount(CodeSearchDTO param);

    public CodeVO load(CodeSearchDTO param);
    public int insert(CodeVO param);
    public int update(CodeVO param);
    public int updateUseYn(CodeVO param);
    
    public int updateState(CodeVO param);
    public int updateOrder(CodeVO param);

    public int delete(String code);
}
