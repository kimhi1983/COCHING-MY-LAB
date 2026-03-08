package com.erns.coching.cochingtv.domain;

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
 * <p>코칭TV 관리 VO
 * T_CH_TV</p> 
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class CochingtvVO {
	
	private long tvSeq;
	private String ytUrl;
	private long sortOrd;
	private String title;
	private String hashtag;
	private String views;
	private String ytDttm;
	private String useYn;
	private String delYn;
	private long rgtr;
	private Date rgtDttm;
	private long chnr;
	private Date chngDttm;
	private String content;
	private String category;
	
	protected void setFromCochingtvSetDTO(CochingtvSetDTO fromDto) {
		this.tvSeq = fromDto.getTvSeq();
		this.ytUrl = fromDto.getYtUrl();
		this.sortOrd = fromDto.getSortOrd();
		this.title = fromDto.getTitle();
		this.hashtag = fromDto.getHashtag();
		this.views = fromDto.getViews();
		this.ytDttm = fromDto.getYtDttm();
	    this.useYn = fromDto.getUseYn();
	    this.delYn = fromDto.getDelYn();
	    this.content = fromDto.getContent();
	    this.category = fromDto.getCategory();
	}
	
	/**
	 * 코칭TV 등록
	 * @param fromDto
	 * @param addUser
	 */
	@Builder(builderClassName = "AddCochingtvBuilder", builderMethodName = "AddCochingtvBuilder") 
	public CochingtvVO(CochingtvSetDTO fromDto, LoginUserDTO addUser) {
	    Assert.notNull(fromDto, "fromDto must not be null");
	    Assert.notNull(addUser, "addUser must not be null");
	    
	    this.setFromCochingtvSetDTO(fromDto);
	    this.tvSeq = 0L;
	    this.delYn = "N";
	    
	    this.rgtr = addUser.getSeq();
	    this.chnr = addUser.getSeq();
	}
	
	/**
	 * 코칭TV 설정
	 * @param bannerSeq
	 * @param fromDto
	 * @param updateUser
	 */
	@Builder(builderClassName = "SetCochingtvBuilder", builderMethodName = "SetCochingtvBuilder") 
	public CochingtvVO(long tvSeq, CochingtvSetDTO fromDto, LoginUserDTO updateUser) {
	    Assert.notNull(fromDto, "fromDto must not be null");
	    Assert.notNull(updateUser, "updateUser must not be null");
	    
	    this.setFromCochingtvSetDTO(fromDto);
	    this.tvSeq = tvSeq;
	    
	    this.chnr = updateUser.getSeq();
	}
	
	/**
	 * 코칭TV 삭제
	 * @param bannerMstCd
	 * @param bannerSeq
	 * @param delYn
	 * @param updateUser
	 */
	@Builder(builderClassName = "UpdateDelYnBuilder", builderMethodName = "UpdateDelYnBuilder") 
	public CochingtvVO(long tvSeq, String delYn, LoginUserDTO updateUser) {
		Assert.notNull(tvSeq, "tvSeq must not be null");
	    Assert.notNull(delYn, "delYn must not be null");
	    Assert.notNull(updateUser, "updateUser must not be null");
	    
	    this.tvSeq = tvSeq;
	    this.delYn = delYn;
	    	    
	    this.chnr = updateUser.getSeq();
	}
}
