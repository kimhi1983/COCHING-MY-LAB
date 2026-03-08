package com.erns.coching.search.domain;

import javax.validation.constraints.NotBlank;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.search.domain.vg.ValidRecommendKeyword0020;
import com.erns.coching.search.domain.vg.ValidRecommendKeyword0030;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * <p>추천 검색어 설정 DTO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RecommendKeywordSetDTO extends AbstractReqDTO implements IReqDto {
	
	private long seq;			//SEQ
	
	@NotBlank(groups = {
			ValidRecommendKeyword0020.class,
			ValidRecommendKeyword0030.class,
    	}, message = "키워드 누락")
	private String name;		//검색어
	
	private String useYn;		//사용여부
	private String delYn;		//삭제여부
	private int sortOrd;		//정렬순서
	
	private long rgtr;			//등록자
	private long chng;			//수정자

}
