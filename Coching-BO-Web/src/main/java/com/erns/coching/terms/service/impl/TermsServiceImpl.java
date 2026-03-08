package com.erns.coching.terms.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.terms.domain.TermsSearchDTO;
import com.erns.coching.terms.domain.TermsVO;
import com.erns.coching.terms.mapper.TermsDAO;
import com.erns.coching.terms.service.TermsService;

/**
 *
 * <p>약관정보 Service</p>
 *
 * @author Hunwoo Park
 *
 */
@Transactional
@Service
public class TermsServiceImpl implements TermsService{

	@Autowired
	private TermsDAO dao;

	@Override
	public List<Map<String, Object>> getList(TermsSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(TermsSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public Map<String, Object>  load(TermsSearchDTO param) {
		return dao.load(param);
	}

	@Override
	public int insert(TermsVO param) {
		if("Y".equals(param.getUseYn())) {
			updateUseYn(param);
		}
		return dao.insert(param);
	}

	@Override
	public int update(TermsVO param) {
		int ret = dao.update(param);
		if("Y".equals(param.getUseYn())) {
			updateUseYn(param);
		}

		return ret;
	}

	@Override
	public int updateUseYn(TermsVO param) {
		return dao.updateUseYn(param);
	}

	@Override
	public List<Map<String, Object>> getVersionList(TermsSearchDTO param) {
		return dao.getVersionList(param);
	}

//	@Override
//	public int newVersion(TermsVO newTerms, TermsVO oldTerms) {
//		if(dao.insert(newTerms) < 0) {
//			return -1;
//		}
//
//		return dao.updateUseYn(oldTerms);
//	}
//
}
