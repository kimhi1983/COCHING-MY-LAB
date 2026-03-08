package com.erns.coching.popup.domain;

import javax.validation.constraints.NotEmpty;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.popup.domain.vg.ValidPopupMaster0032;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * <p>팝업 마스터 설정 DTO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class PopupMasterSetDTO extends AbstractReqDTO implements IReqDto{
	
	@NotEmpty(groups = {
		ValidPopupMaster0032.class
	}, message = "popupMstCd 누락")
	private String popupMstCd;
	
	private String popupType;
	private String popupMstName;
	private String popupMstDesc;
	private String useYn;
	private int width;
	private int height;
	private String previewUrl;
}
