package com.erns.coching.popup.service;

import java.util.List;
import java.util.Map;

import com.erns.coching.popup.domain.PopupSearchDTO;

/**
*
* <p>Popup Service</p>
*
* @author Kyunmin Lee
*
*/
public interface PopupService {

	public List<Map<String, Object>> getList(PopupSearchDTO param);

	public int updateClickCnt(long popupSeq);
}
