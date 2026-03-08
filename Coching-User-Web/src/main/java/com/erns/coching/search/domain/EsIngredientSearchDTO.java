package com.erns.coching.search.domain;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.search.domain.vg.ValidEsIngredientSearch0012;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class EsIngredientSearchDTO extends BaseSearchDTO implements IReqDto {
	
	@Pattern(groups = {
			ValidEsIngredientSearch0012.class
		},regexp = "^(R|K|E|H)$" 
		, message = "idType 형식오류(R|K|E|H)")
	private String idType;
	
	@NotEmpty(groups = {
			ValidEsIngredientSearch0012.class
		}, message = "id 누락")
	private String id;

	private String index; // 인덱스
}
