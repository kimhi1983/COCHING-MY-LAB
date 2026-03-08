package com.erns.coching.popup.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;

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
	
	@NotEmpty(groups = {
        }, message = "popupMstCd 누락")
	private String popupMstCd;

	@NotNull(groups = {
        }, message = "popupSeq 누락")
	@Min(groups = {
		},value = 1, message = "popupSeq 누락")
	private long popupSeq;
	
	private String useYn;
	private String delYn;
	
}
