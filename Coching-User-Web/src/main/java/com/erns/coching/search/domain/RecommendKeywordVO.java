package com.erns.coching.search.domain;

import java.util.Date;

import org.springframework.util.Assert;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 
 * <p>추천 검색어
 * T_RECOMMEND_KEYWORD
 * </p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class RecommendKeywordVO {
	
	private long seq;			//SEQ
	private String name;		//검색어
	private String useYn;		//사용여부
	private String delYn;		//삭제여부
	private int sortOrd;		//정렬순서
	private long rgtr;			//등록자
	private Date rgtDttm;		//등록일
	private long chng;			//수정자
	private Date chngDttm;		//수정일
	
	protected void setFromFavoriteSetDTO(RecommendKeywordSetDTO fromDto) {
		this.name = fromDto.getName();
		this.useYn = fromDto.getUseYn();
		this.delYn = fromDto.getDelYn();
		this.sortOrd = fromDto.getSortOrd();
	}
	
	/**
	 * 추천 검색어 등록 Builder
	 * @param fromDto
	 */
	@Builder(builderClassName = "AddRecommendKeywordBuilder", builderMethodName = "AddRecommendKeywordBuilder")
	public RecommendKeywordVO(RecommendKeywordSetDTO fromDto) {
	    Assert.notNull(fromDto, "fromDto must not be null");
	    
	    this.setFromFavoriteSetDTO(fromDto);
		
	    this.seq = 0L;
	    this.useYn = "Y";
	    this.delYn = "Y";
	    this.sortOrd = 9999;
	    this.rgtr = fromDto.getRgtr();
	    this.chng = fromDto.getChng();
	}
	
	/**
	 * 추천 검색어 수정 Builder
	 * @param fromDto
	 */
	@Builder(builderClassName = "EditRecommendKeywordBuilder", builderMethodName = "EditRecommendKeywordBuilder")
	public RecommendKeywordVO(RecommendKeywordSetDTO fromDto, long seq) {
	    Assert.notNull(fromDto, "fromDto must not be null");
	    
	    this.setFromFavoriteSetDTO(fromDto);
		
	    this.seq = seq;
	    this.chng = fromDto.getChng();
	}
}
