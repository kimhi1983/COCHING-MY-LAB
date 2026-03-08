package com.erns.es.log.domain;

import com.erns.es.common.model.BaseSearchDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * <p>API 로그 검색 DTO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class ApiProcLogSearchDTO extends BaseSearchDTO {
	
	private Long seq;
	
	private Long membSeq;
	
	private String procStatus;
}
