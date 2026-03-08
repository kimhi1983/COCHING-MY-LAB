package com.erns.coching.search.domain;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;

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
	
	private long seq;			//SEQ
	
	private String nameL;		//검색어
	
	private String useYn = "Y";		//사용여부
	private String delYn = "N";		//삭제여부	
}
