package com.erns.coching.popup.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.popup.domain.PopupSearchDTO;
import com.erns.coching.popup.mapper.PopupDAO;
import com.erns.coching.popup.service.PopupService;

/**
*
* <p>Popup Service</p>
*
* @author Kyunmin Lee
*
*/
@Service
@Transactional
public class PopupServiceImpl implements PopupService{

	@Autowired
	private PopupDAO dao;

	@Override
	public List<Map<String, Object>> getList(PopupSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int updateClickCnt(long popupSeq) {
		return dao.updateClickCnt(popupSeq);
	}
}
