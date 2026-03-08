package com.erns.coching.popup.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.popup.domain.vg.ValidPopup0011;
import com.erns.coching.popup.domain.vg.ValidPopup0012;
import com.erns.coching.popup.domain.vg.ValidPopupMaster0012;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * <p>팝업 검색 DTO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class PopupSearchDTO extends BaseSearchDTO implements IReqDto{
	
	@NotNull(groups = {		
			ValidPopup0012.class,
		}, message = "popupSeq 누락")
	@Min(groups = {
			ValidPopup0012.class
		},value = 1, message = "popupSeq 누락")
	private long popupSeq;

	@NotEmpty(groups = {
			ValidPopupMaster0012.class,
			ValidPopup0011.class,			
			ValidPopup0012.class
		}, message = "popupMstCd 누락")
	private String popupMstCd;

	private String popupName;
	private String popupNameL;
	private int sortOrd;
	
	private String useYn;
	private String delYn;
	
}
