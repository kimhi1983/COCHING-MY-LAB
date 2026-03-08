package com.erns.es.coching.domain;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import com.erns.es.coching.domain.vg.ValidEsIngredientSearch0012;
import com.erns.es.common.model.BaseSearchDTO;
import com.erns.es.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class EsCochingIngredientSearchDTO extends BaseSearchDTO implements IReqDto {
	
	@Pattern(groups = {
			ValidEsIngredientSearch0012.class
		},regexp = "^(K|E|H)$" 
		, message = "idType 형식오류(K|E|H)")
	private String idType;
	
	@NotEmpty(groups = {
			ValidEsIngredientSearch0012.class
		}, message = "id 누락")
	private String id;
}
