package com.erns.es.coching.domain;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import com.erns.es.coching.domain.vg.ValidEsIngredientSearch0012;
import com.erns.es.common.model.BaseSearchDTO;
import com.erns.es.common.model.IReqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EsIngredientSearchDTO extends BaseSearchDTO implements IReqDto {
	
	public static String ID_TYPE_RAW = "R";
	public static String ID_TYPE_KCAI = "K";
	public static String ID_TYPE_EWG = "E";
	public static String ID_TYPE_HW = "H";
	
	@Pattern(groups = {
			ValidEsIngredientSearch0012.class
		},regexp = "^(R|K|E|H)$" 
		, message = "idType 형식오류(R|K|E|H)")
	protected String idType;
	
	@NotEmpty(groups = {
			ValidEsIngredientSearch0012.class
		}, message = "id 누락")
	protected String id;
	
	protected boolean loadDetailJson;
	
	@NotEmpty(groups = {
//			ValidEsIngredientSearch0011.class
	    }, message = "text 누락")
	protected String text;
}
