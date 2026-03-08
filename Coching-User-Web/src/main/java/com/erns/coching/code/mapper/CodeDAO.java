package com.erns.coching.code.mapper;

import com.erns.coching.code.domain.CodeSearchDTO;
import com.erns.coching.code.domain.CodeVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

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

    public int delete(String code);
}
