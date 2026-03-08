package com.erns.coching.code.service.impl;

import com.erns.coching.code.domain.CodeSearchDTO;
import com.erns.coching.code.domain.CodeVO;
import com.erns.coching.code.mapper.CodeDAO;
import com.erns.coching.code.service.CodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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

}
