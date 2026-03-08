package com.erns.coching.search.domain;

import java.util.List;

import javax.validation.constraints.NotEmpty;

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
 * <p>추천 검색어 복수 설정 DTO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RecommendKeywordMultipleSetDTO extends AbstractReqDTO implements IReqDto {

	@NotEmpty(groups = {
			ValidRecommendKeyword0020.class,
			ValidRecommendKeyword0030.class,
    	}, message = "list 누락")
	private List<RecommendKeywordSetDTO> list;
}