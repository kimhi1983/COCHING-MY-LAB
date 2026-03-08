package com.erns.coching.popup.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.popup.domain.PopupSearchDTO;

/**
*
* <p>Popup Mapper</p>
*
* @author Kyunmin Lee
*
*/
@Mapper
public interface PopupDAO {

	public List<Map<String, Object>> getList(PopupSearchDTO param);

	public int updateClickCnt(long popupSeq);
}
