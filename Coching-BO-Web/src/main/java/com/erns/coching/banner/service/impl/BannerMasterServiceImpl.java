package com.erns.coching.banner.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.banner.domain.BannerMasterSearchDTO;
import com.erns.coching.banner.domain.BannerMasterVO;
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
	public List<Map<String, Object>> getList(BannerMasterSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(BannerMasterSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public Map<String, Object> load(BannerMasterSearchDTO param) {
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
	public int updateState(Collection<BannerMasterVO> list) {
		int retCnt = 0;

		for(BannerMasterVO param : list){
			retCnt += dao.updateState(param);
		}

		return retCnt;
	}
	
	@Override
	public int delete(String bannerMstCd) {
		return dao.delete(bannerMstCd);
	}

}
