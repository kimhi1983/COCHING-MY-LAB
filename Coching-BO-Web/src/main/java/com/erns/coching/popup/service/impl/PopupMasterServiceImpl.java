package com.erns.coching.popup.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.popup.domain.PopupMasterSearchDTO;
import com.erns.coching.popup.domain.PopupMasterVO;
import com.erns.coching.popup.domain.PopupSearchDTO;
import com.erns.coching.popup.mapper.PopupMasterDAO;
import com.erns.coching.popup.service.PopupMasterService;

/**
 * 
 * <p>팝업마스터 Service</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Service
@Transactional
public class PopupMasterServiceImpl implements PopupMasterService{


	@Autowired
	private PopupMasterDAO dao;

	@Override
	public List<Map<String, Object>> getList(PopupMasterSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(PopupMasterSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public Map<String, Object> load(PopupSearchDTO param) {
		return dao.load(param);
	}

	@Override
	public int insert(PopupMasterVO param) {
		return dao.insert(param);
	}

	@Override
	public int update(PopupMasterVO param) {
		return dao.update(param);
	}

	@Override
	public int updateUseYn(PopupMasterVO param) {
		return dao.updateUseYn(param);
	}
	
	@Override
	public int updateState(PopupMasterVO param) {
		return dao.updateState(param);
	}
	
	@Override
	public int updateState(Collection<PopupMasterVO> list) {
		int retCnt = 0;

		for(PopupMasterVO param : list){
			retCnt += dao.updateState(param);
		}

		return retCnt;
	}

	@Override
	public int delete(String popupMstCd) {
		return dao.delete(popupMstCd);
	}
}
