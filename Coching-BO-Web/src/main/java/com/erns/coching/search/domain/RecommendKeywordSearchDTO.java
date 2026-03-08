package com.erns.coching.search.domain;

import javax.validation.constraints.Min;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.search.domain.vg.ValidRecommendKeyword0012;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * <p>추천 검색어 목록 조회DTO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RecommendKeywordSearchDTO extends BaseSearchDTO implements IReqDto {
	
	@Min(groups = {
	  	ValidRecommendKeyword0012.class,
		}, 
		value = 1,
    message = "seq 누락")
	private long seq;			//SEQ
	
	private String nameL;		//검색어
	
	private String useYn = "Y";		//사용여부
	private String delYn = "N";		//삭제여부	
}
