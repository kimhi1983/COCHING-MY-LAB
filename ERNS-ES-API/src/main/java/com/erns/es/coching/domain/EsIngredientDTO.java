package com.erns.es.coching.domain;

import com.fasterxml.jackson.annotation.JsonRawValue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@Builder
@ToString(exclude = {"kdata", "edata", "hdata"})
public class EsIngredientDTO {
	
	private int id;
	
	private String repName;
	
	@JsonRawValue
	private String kdata;
	
	@JsonRawValue	
	private String edata;
	
	@JsonRawValue	
	private String hdata;
}
