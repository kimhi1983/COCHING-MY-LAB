package com.erns.coching.favorite.domain;

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
 * <p>찜 목록 VO
 * T_FAVORITE_INF</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class FavoriteVO {

  private long membSeq;		//사용자 SEQ
  private String refCode;		//참조 CD
  private long refSeq;		//참조 SEQ
	private Date rgtDttm;		//등록일

	protected void setFromFavoriteSetDTO(FavoriteSetDTO fromDto) {
		this.membSeq = fromDto.getMembSeq();
		this.refCode = fromDto.getRefCode();
		this.refSeq = fromDto.getRefSeq();
	}

	/**
	 * 찜 목록 등록 Builder
	 * @param fromDto
	 */
	@Builder(builderClassName = "AddFavoriteBuilder", builderMethodName = "AddFavoriteBuilder")
	public FavoriteVO(FavoriteSetDTO fromDto) {
	    Assert.notNull(fromDto, "fromDto must not be null");
	    
	    this.setFromFavoriteSetDTO(fromDto);
		this.membSeq = fromDto.getMembSeq();
	}
	

}
