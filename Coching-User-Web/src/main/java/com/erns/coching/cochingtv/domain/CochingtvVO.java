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
	    this.category = fromDto.getCategory();
	}
	
}
