package com.erns.coching.favorite.domain;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.favorite.domain.vg.ValidFavorite0011;
import com.erns.coching.favorite.domain.vg.ValidFavorite0040;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>찜 목록 검색 DTO</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class FavoriteSearchDTO extends BaseSearchDTO implements IReqDto {

    private long membSeq;		//사용자 SEQ    
    
    @NotBlank(groups = {
			ValidFavorite0011.class
    	}, message = "참조코드 누락")
    @Pattern(groups = {
    		ValidFavorite0011.class
    	}, message = "참조코드 형식오류", regexp = "raw|prod")    
    private String refCode;		//참조 CD
    private long refSeq;		//참조 SEQ

    @NotNull(groups = {
            ValidFavorite0040.class,
        },
        message = "refSeqs 누락")
    private long[] refSeqs;     //사용자 SEQ
    
	private Date rgtDttm;		//등록일
}
