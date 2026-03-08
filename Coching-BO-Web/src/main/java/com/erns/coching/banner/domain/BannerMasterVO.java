package com.erns.coching.banner.domain;

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 
 * <p>배너마스터 VO
 * T_BANNER_MST</p> 
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class BannerMasterVO {
	private String bannerMstCd;		//BANNER_MST_CD
	private String bannerType;		//BANNER_TYPE
	private String bannerMstName;	//BANNER_MST_NAME
	private String bannerMstDesc;	//BANNER_MST_DESC
	private String useYn;			//USE_YN	
	private int width;				//WIDTH
	private int height;				//HEIGHT
	private String previewUrl;		//PREVIEW_URL
	
	private long rgtr;				//RGTR
	private Date rgtDttm;			//RGT_DTTM
	private long chnr;				//CHNR
	private Date chngDttm;			//CHNG_DTTM
}
