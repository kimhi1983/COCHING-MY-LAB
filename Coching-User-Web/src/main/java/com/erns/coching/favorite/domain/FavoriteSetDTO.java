package com.erns.coching.favorite.domain;

import java.util.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.favorite.domain.vg.ValidFavorite0020;
import com.erns.coching.favorite.domain.vg.ValidFavorite0040;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
*
* <p>찜 목록 설정 DTO</p>
*
* @author Kyungmin Lee
*
*/
@Getter
@Setter
@ToString
@NoArgsConstructor
public class FavoriteSetDTO extends AbstractReqDTO implements IReqDto {
	
	private long membSeq;		//사용자 SEQ
	
	@NotBlank(groups = {
			ValidFavorite0020.class,
			ValidFavorite0040.class,
		}, message = "참조코드 누락")
	@Pattern(groups = {
			ValidFavorite0020.class,
			ValidFavorite0040.class,
		}, message = "참조코드 형식오류", regexp = "raw|prod")    
	private String refCode;		//참조 CD
	
	@Min(groups = {
			ValidFavorite0020.class,
			ValidFavorite0040.class,
		},
		value = 1,
		message = "참조Seq 누락")
	private long refSeq;		//참조 SEQ
    
	private Date rgtDttm;		//등록일

}
