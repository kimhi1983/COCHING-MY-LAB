package com.erns.coching.popup.domain;

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * <p>팝업마스터 VO
 * T_POPUP_MST</p>
 *
 * @author Hunwoo Park
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class PopupMasterVO {

	private String popupMstCd;		//POPUP_MST_CD
	private String popupType;		//POPUP_TYPE
	private String popupMstName;	//POPUP_MST_NAME
	private String popupMstDesc;	//POPUP_MST_DESC
	private String useYn;			//USE_YN
	private int width;				//WIDTH
	private int height;				//HEIGHT
	private String previewUrl;		//PREVIEW_URL

	private long rgtr;				//RGTR
	private Date rgtDttm;			//RGT_DTTM
	private long chnr;				//CHNR
	private Date chngDttm;			//CHNG_DTTM
}
