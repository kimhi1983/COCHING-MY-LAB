package com.erns.coching.banner.domain;

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
 * T_BANNER_AD</p> 
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class BannerAdVO {
	
	private String bannerMstCd;
	private Long bannerSeq;
	private String title;
	private String content;
	private String fromDate;
	private String toDate;
	private String adStatus;
	private int sortOrd;
	private String dispYn;
	private String useYn;
	private String delYn;
	
	private long refSeq;
	private String refCode;
	private String refUrl;
	private String remarks;
	private int clicks;
	
	private long rgtr;
	private long chnr;
	
	protected void setFromBannerSetDTO(BannerAdSetDTO fromDto) {
		this.bannerSeq = fromDto.getBannerSeq();
	    this.bannerMstCd = fromDto.getBannerMstCd();
	    
	    this.title = fromDto.getTitle();
	    this.content = fromDto.getContent();
	    this.fromDate = fromDto.getFromDate();
	    this.toDate = fromDto.getToDate();
	    this.adStatus = fromDto.getAdStatus();
	    this.sortOrd = fromDto.getSortOrd();
	    this.dispYn = fromDto.getDispYn();
	    this.useYn = fromDto.getUseYn();
	    this.delYn = fromDto.getDelYn();
	    this.refSeq = fromDto.getRefSeq();
	    this.refCode = fromDto.getRefCode();
	    this.refUrl = fromDto.getRefUrl();
	    this.remarks = fromDto.getRemarks();
	    this.clicks = fromDto.getClicks();
	}
	
	/**
	 * 배너 등록
	 * @param fromDto
	 * @param addUser
	 */
	@Builder(builderClassName = "AddBannerBuilder", builderMethodName = "AddBannerBuilder") 
	public BannerAdVO(BannerAdSetDTO fromDto, LoginUserDTO addUser) {
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
	public BannerAdVO(long bannerSeq, BannerAdSetDTO fromDto, LoginUserDTO updateUser) {
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
	public BannerAdVO(String bannerMstCd, long bannerSeq, String delYn, LoginUserDTO updateUser) {
		Assert.notNull(bannerMstCd, "bannerMstCd must not be null");
	    Assert.notNull(delYn, "delYn must not be null");
	    Assert.notNull(updateUser, "updateUser must not be null");
	    
	    this.bannerMstCd = bannerMstCd;
	    this.bannerSeq = bannerSeq;
	    this.delYn = delYn;
	    	    
	    this.chnr = updateUser.getSeq();
	}
}
