package com.erns.coching.popup.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.popup.domain.PopupSearchDTO;
import com.erns.coching.popup.domain.PopupVO;

/**
 *
 * <p>팝업 Mapper</p>
 *
 * @author Hunwoo Park
 *
 */
@Mapper
public interface PopupDAO {

	public List<Map<String, Object>> getList(PopupSearchDTO param);
	public int getListCount(PopupSearchDTO param);

	public Map<String, Object> load(PopupSearchDTO param);

	public int insert(PopupVO param);

	public int update(PopupVO param);
	public int updateUseYn(PopupVO param);
	public int updateDelYn(PopupVO param);
	public int updateOrder(PopupVO param);
	public int updateState(PopupVO param);
	
	public int delete(long popupSeq);

}
