package com.erns.coching.login.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.login.domain.TempSnsInfoVO;
import com.erns.coching.login.mapper.TempSnsInfoDAO;
import com.erns.coching.login.service.TempSnsInfoService;

/**
 * 
 * <p>SnsInfo Service</p> 
 *
 * @author Hunwoo Park
 *
 */
@Transactional
@Service
public class TempSnsInfoServiceImpl implements TempSnsInfoService{
	
	@Autowired
	private TempSnsInfoDAO dao;

	@Override
	public TempSnsInfoVO load(String key) {
		return dao.load(key);
	}

	@Override
	public int insert(TempSnsInfoVO param) {
		TempSnsInfoVO oldData = dao.load(param.getSiKey());
		if(oldData != null) {
			dao.delete(param.getSiKey());
		}
		
		//오래된 임시 SNS 인증정보 삭제
		deleteExpired();
		
		return dao.insert(param);
	}

	@Override
	public int delete(String key) {
		//오래된 임시 SNS 인증정보 삭제
		deleteExpired();
				
		return dao.delete(key);
	}

	@Override
	public int deleteExpired() {
		return dao.deleteExpired();
	}

}
