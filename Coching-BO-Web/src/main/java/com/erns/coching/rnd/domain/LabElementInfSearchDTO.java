package com.erns.coching.rnd.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.rnd.domain.vg.ValidLabElementInf0011;
import com.erns.coching.rnd.domain.vg.ValidLabElementInf0012;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * <p>연구실 원소 정보 검색 DTO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class LabElementInfSearchDTO extends BaseSearchDTO implements IReqDto{
	
	@NotNull(groups = {		
			ValidLabElementInf0012.class,
		}, message = "labElementSeq 누락")
	@Min(groups = {
			ValidLabElementInf0012.class
		},value = 1, message = "labElementSeq 누락")
	private long labElementSeq;

	@NotNull(groups = {		
			ValidLabElementInf0011.class,
			ValidLabElementInf0012.class,
		}, message = "labSeq 누락")
	@Min(groups = {
			ValidLabElementInf0011.class,
			ValidLabElementInf0012.class
		},value = 1, message = "labSeq 누락")
	private long labSeq;

	private long elementId;
	private int sortOrd;
	
	private String useYn;
	private String delYn;
	
}

