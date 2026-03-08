package com.erns.es.coching.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonRawValue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"detailJson"})
public class EsCochingIngredientVO {
	
	private Integer id;	
	private String repName;	
	private String repNameEn;
	private Integer kcaiId;
	private Integer ewgId;
	private Integer hwIngdId;
	
	private String[] inci;
	private String[] namesKo;
	private String[] namesEn;
	private String[] purposes;
	private String[] casNo;
	private String[] ecNo;
	
	private String formula;
	private String expUrl1;
	private String expUrl2;
	
	private Integer ewgScoreMin;
	private Integer ewgScore;
	private String ewgDataLabel;
	private String ewgRawcnoId;
	private String ewgUrl;
	private Date ewgChngDttm;
	
	
	@JsonRawValue	
	private String detailJson;
	
	private Date rgtDttm;
	private Date chngDttm;
}
