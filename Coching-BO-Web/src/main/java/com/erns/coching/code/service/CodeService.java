package com.erns.coching.code.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.erns.coching.code.domain.CodeSearchDTO;
import com.erns.coching.code.domain.CodeVO;

/**
 * 
 * <p>공통코드 관리 Service</p>  
 *
 * @author Hunwoo Park 
 *
 */
public interface CodeService {
    
    public List<Map<String, Object>> getCodeList(CodeSearchDTO param);
    
    public List<Map<String, Object>> getList(CodeSearchDTO param);
    public int getListCount(CodeSearchDTO param);
    
    public CodeVO load(CodeSearchDTO param);
    public int insert(CodeVO param);
    public int update(CodeVO param);
    public int updateUseYn(CodeVO param);
    
    public int updateState(Collection<CodeVO> list);
    public int updateOrder(Collection<CodeVO> list);
    
    public int delete(String code);
}
