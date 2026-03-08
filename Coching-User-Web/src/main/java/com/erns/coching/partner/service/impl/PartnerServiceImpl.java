package com.erns.coching.partner.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.partner.domain.PartnerSearchDTO;
import com.erns.coching.partner.domain.PartnerVO;
import com.erns.coching.partner.mapper.PartnerDAO;
import com.erns.coching.partner.service.PartnerService;

/**
 *
 * <p>파트너사 회원 기본정보 Service</p>
 *
 * @author Kyungmin Lee
 *
 */
@Transactional
@Service
public class PartnerServiceImpl implements PartnerService {

	@Autowired
	private PartnerDAO dao;

	@Override
	public List<Map<String, Object>> getList(PartnerSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(PartnerSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public Map<String, Object> load(PartnerSearchDTO param) {
		return dao.load(param);
	}
	
	@Override
	public long loadNextSeq(){
		return dao.loadNextSeq();
	};

	@Override
	public int insert(PartnerVO param) {
		return dao.insert(param);
	}


	@Override
	public int update(PartnerVO param) {
		return dao.update(param);
	}

	@Override
	public int updateDelYn(PartnerVO param) {
		return dao.updateDelYn(param);
	}

	@Override
	public int delete(long ptnSeq) {
		return dao.delete(ptnSeq);
	}




}
