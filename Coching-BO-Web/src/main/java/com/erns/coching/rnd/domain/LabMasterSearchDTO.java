package com.erns.coching.rnd.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.rnd.domain.vg.ValidLabMaster0012;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * <p>연구실 마스터 검색 DTO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class LabMasterSearchDTO extends BaseSearchDTO implements IReqDto{
	
	@NotNull(groups = {		
			ValidLabMaster0012.class,
		}, message = "labMstSeq 누락")
	@Min(groups = {
			ValidLabMaster0012.class
		},value = 1, message = "labMstSeq 누락")
	private long labMstSeq;

	private String title;
	private String titleL;
	
	private String prodCateGroup;
	private String prodCateCode;
	
}
