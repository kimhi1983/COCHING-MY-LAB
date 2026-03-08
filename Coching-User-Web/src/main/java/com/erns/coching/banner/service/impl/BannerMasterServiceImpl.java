package com.erns.coching.banner.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.banner.domain.BannerMasterVO;
import com.erns.coching.banner.domain.BannerSearchDTO;
import com.erns.coching.banner.mapper.BannerMasterDAO;
import com.erns.coching.banner.service.BannerMasterService;

/**
 * 
 * <p>배너마스터 Service</p> 
 *
 * @author Kyungmin Lee
 *
 */
@Service
@Transactional
public class BannerMasterServiceImpl implements BannerMasterService{

	@Autowired
	private BannerMasterDAO dao;
	
	@Override
	public List<Map<String, Object>> getList(BannerSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(BannerSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public Map<String, Object> load(BannerSearchDTO param) {
		return dao.load(param);
	}

	@Override
	public int insert(BannerMasterVO param) {
		return dao.insert(param);
	}

	@Override
	public int update(BannerMasterVO param) {
		return dao.update(param);
	}

	@Override
	public int updateUseYn(BannerMasterVO param) {
		return dao.updateUseYn(param);
	}
	
	@Override
	public int delete(String bannerMstCd) {
		return dao.delete(bannerMstCd);
	}

}
