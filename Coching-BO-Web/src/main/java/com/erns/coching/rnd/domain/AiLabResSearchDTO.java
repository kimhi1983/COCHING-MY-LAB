package com.erns.coching.rnd.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.rnd.domain.vg.ValidAiLabRes0011;
import com.erns.coching.rnd.domain.vg.ValidAiLabRes0012;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * <p>AI 연구실 결과 검색 DTO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class AiLabResSearchDTO extends BaseSearchDTO implements IReqDto{
	
	@NotNull(groups = {		
			ValidAiLabRes0012.class,
		}, message = "resSeq 누락")
	@Min(groups = {
			ValidAiLabRes0012.class
		},value = 1, message = "resSeq 누락")
	private long resSeq;        // AI_LAB_MST_SEQ

	@NotNull(groups = {		
			ValidAiLabRes0011.class,
			ValidAiLabRes0012.class,
		}, message = "labSeq 누락")
	@Min(groups = {
			ValidAiLabRes0011.class,
			ValidAiLabRes0012.class
		},value = 1, message = "labMstSeq 누락")
	private long labMstSeq;        // LAB_MST_SEQ
	
}

