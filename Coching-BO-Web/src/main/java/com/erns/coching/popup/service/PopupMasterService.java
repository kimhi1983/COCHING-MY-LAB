package com.erns.coching.popup.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.erns.coching.popup.domain.PopupMasterSearchDTO;
import com.erns.coching.popup.domain.PopupMasterVO;
import com.erns.coching.popup.domain.PopupSearchDTO;

/**
 *
 * <p>팝업마스터 Service</p>
 *
 * @author Hunwoo Park
 *
 */
public interface PopupMasterService {

	public List<Map<String, Object>> getList(PopupMasterSearchDTO param);
	public int getListCount	(PopupMasterSearchDTO param);

	public Map<String, Object> load(PopupSearchDTO param);
	
	public int insert(PopupMasterVO param);
	public int update(PopupMasterVO param);
	public int updateUseYn(PopupMasterVO param);
	
	public int updateState(PopupMasterVO param);
	public int updateState(Collection<PopupMasterVO> list);
	
	public int delete(String popupMstCd);
}
