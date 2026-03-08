package com.erns.coching.terms.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.terms.domain.TermsSearchDTO;
import com.erns.coching.terms.mapper.TermsDAO;
import com.erns.coching.terms.service.TermsService;

/**
*
* <p>Terms Service</p>
*
* @author Kyunmin Lee
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
	public List<Map<String, Object>> getVersionList(TermsSearchDTO param) {
		return dao.getVersionList(param);
	}	
}
