package com.erns.coching.banner.domain;

import java.util.Date;

import org.springframework.util.Assert;

import com.erns.coching.login.domain.LoginUserDTO;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 
 * <p>배너관리 VO
 * T_BANNER_INF</p> 
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class BannerVO {
	
	private long bannerSeq;
	private String bannerMstCd;
	
	private String bannerName;
	private String fromDate;
	private String toDate;
	private String bannerDesc;

	private String bannerUrl;
	private int bannerWidth;
	private int bannerHeight;
	private int clicks;
	private int sortOrd;
	private String useYn;
	private String delYn;
	private long rgtr;
	private Date rgtDttm;
	private long chnr;
	private Date chngDttm;	
	
	protected void setFromBannerSetDTO(BannerSetDTO fromDto) {
		this.bannerSeq = fromDto.getBannerSeq();
	    this.bannerMstCd = fromDto.getBannerMstCd();
	    
	    this.bannerName = fromDto.getBannerName();
	    this.fromDate = fromDto.getFromDate();
	    this.toDate = fromDto.getToDate();
	    this.bannerDesc = fromDto.getBannerDesc();
	    this.bannerUrl = fromDto.getBannerUrl();	    
	    this.bannerWidth = fromDto.getBannerWidth();
	    this.bannerHeight = fromDto.getBannerHeight();
	    this.useYn = fromDto.getUseYn();
			this.delYn = fromDto.getDelYn();	
	}
	
	/**
	 * 배너 등록
	 * @param fromDto
	 * @param addUser
	 */
	@Builder(builderClassName = "AddBannerBuilder", builderMethodName = "AddBannerBuilder") 
	public BannerVO(BannerSetDTO fromDto, LoginUserDTO addUser) {
	    Assert.notNull(fromDto, "fromDto must not be null");
	    Assert.notNull(addUser, "addUser must not be null");
	    
	    this.setFromBannerSetDTO(fromDto);
	    this.bannerSeq = 0L;
	    this.delYn = "N";
	    
	    this.rgtr = addUser.getSeq();
	    this.chnr = addUser.getSeq();
	}
	
	/**
	 * 배너 설정
	 * @param bannerSeq
	 * @param fromDto
	 * @param updateUser
	 */
	@Builder(builderClassName = "SetBannerBuilder", builderMethodName = "SetBannerBuilder") 
	public BannerVO(long bannerSeq, BannerSetDTO fromDto, LoginUserDTO updateUser) {
	    Assert.notNull(fromDto, "fromDto must not be null");
	    Assert.notNull(updateUser, "updateUser must not be null");
	    
	    this.setFromBannerSetDTO(fromDto);
	    this.bannerSeq = bannerSeq;
	    
	    this.chnr = updateUser.getSeq();
	}
	
	/**
	 * 팝업 삭제
	 * @param bannerMstCd
	 * @param bannerSeq
	 * @param delYn
	 * @param updateUser
	 */
	@Builder(builderClassName = "UpdateDelYnBuilder", builderMethodName = "UpdateDelYnBuilder") 
	public BannerVO(String bannerMstCd, long bannerSeq, String delYn, LoginUserDTO updateUser) {
		Assert.notNull(bannerMstCd, "bannerMstCd must not be null");
	    Assert.notNull(delYn, "delYn must not be null");
	    Assert.notNull(updateUser, "updateUser must not be null");
	    
	    this.bannerMstCd = bannerMstCd;
	    this.bannerSeq = bannerSeq;
	    this.delYn = delYn;
	    	    
	    this.chnr = updateUser.getSeq();
	}
}
