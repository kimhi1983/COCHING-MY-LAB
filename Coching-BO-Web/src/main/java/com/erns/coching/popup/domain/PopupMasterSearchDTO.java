package com.erns.coching.popup.domain;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * <p>팝업 마스터 검색 DTO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class PopupMasterSearchDTO extends BaseSearchDTO implements IReqDto{
	
	private String popupMstCd;
	private String popupMstCdL;
	private String popupType;
	private String popupTypeL;
	private String popupMstName;
	private String popupMstDesc;
	private String popupMstDescL;
	private String useYn;
	private int width;
	private int height;
	private String previewUrl;
	
	private String allSrch;
	
}
