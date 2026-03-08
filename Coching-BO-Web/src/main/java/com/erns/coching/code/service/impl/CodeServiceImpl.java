package com.erns.coching.code.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.code.domain.CodeSearchDTO;
import com.erns.coching.code.domain.CodeVO;
import com.erns.coching.code.mapper.CodeDAO;
import com.erns.coching.code.service.CodeService;

/**
 *
 * <p>공통코드 관리 Service</p>
 *
 * @author Hunwoo Park
 *
 */
@Transactional
@Service
public class CodeServiceImpl implements CodeService {

    @Autowired
	private CodeDAO dao;

    public List<Map<String, Object>> getCodeList(CodeSearchDTO param){
        return dao.selectCodeList(param);
    }

	@Override
	public List<Map<String, Object>> getList(CodeSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(CodeSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public CodeVO load(CodeSearchDTO param) {
		return dao.load(param);
	}

	@Override
	public int insert(CodeVO param) {
		return dao.insert(param);
	}

	@Override
	public int update(CodeVO param) {
		return dao.update(param);
	}

	@Override
	public int updateUseYn(CodeVO param) {
		return dao.updateUseYn(param);
	}
	
	@Override
	public int delete(String code) {
		return dao.delete(code);
	}
	
	@Override
	public int updateState(Collection<CodeVO> list) {
		int retCnt = 0;

		for(CodeVO param : list){
			retCnt += dao.updateState(param);
		}

		return retCnt;
	}
	
	@Override
	public int updateOrder(Collection<CodeVO> list) {
		int retCnt = 0;

		for(CodeVO param : list){
			retCnt += dao.updateOrder(param);
		}

		return retCnt;
	}

}
