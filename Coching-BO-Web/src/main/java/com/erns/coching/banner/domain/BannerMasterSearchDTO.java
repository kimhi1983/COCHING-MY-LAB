package com.erns.coching.banner.domain;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * <p>배너 마스터 검색 DTO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class BannerMasterSearchDTO extends BaseSearchDTO implements IReqDto{
	
	private String bannerMstCd;
	private String bannerMstCdL;
	private String bannerType;
	private String bannerTypeL;
	private String bannerMstName;
	private String bannerMstDesc;
	private String bannerMstDescL;
	private String useYn;	
	private int width;
	private int height;
	private String previewUrl;
	
	private String allSrch;
	
}
