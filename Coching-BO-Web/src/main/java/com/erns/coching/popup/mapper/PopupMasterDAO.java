package com.erns.coching.popup.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.popup.domain.PopupMasterSearchDTO;
import com.erns.coching.popup.domain.PopupMasterVO;
import com.erns.coching.popup.domain.PopupSearchDTO;

/**
 * 
 * <p>팝업마스터 Mapper</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Mapper
public interface PopupMasterDAO {

	public List<Map<String, Object>> getList(PopupMasterSearchDTO param);
	public int getListCount	(PopupMasterSearchDTO param);
	
	public Map<String, Object> load(PopupSearchDTO param); 
	
	public int insert(PopupMasterVO param);
	public int update(PopupMasterVO param);
	public int updateUseYn(PopupMasterVO param);
	public int updateState(PopupMasterVO param);
	
	public int delete(String popupMstCd);
}
