package com.erns.coching.banner.domain;

import javax.validation.constraints.NotEmpty;

import com.erns.coching.banner.domain.vg.ValidBannerMaster0032;
import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * <p>배너 마스터 설정 DTO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class BannerMasterSetDTO extends AbstractReqDTO implements IReqDto{
	
	@NotEmpty(groups = {
		ValidBannerMaster0032.class
	}, message = "popupMstCd 누락")
	private String bannerMstCd;
	private String bannerType;
	private String bannerMstName;
	private String bannerMstDesc;
	private String useYn;	
	private int width;
	private int height;
	private String previewUrl;
}
