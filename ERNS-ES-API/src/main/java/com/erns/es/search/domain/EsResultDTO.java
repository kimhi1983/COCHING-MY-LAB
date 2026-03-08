package com.erns.es.search.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
public class EsResultDTO {
	
	@Builder.Default
	private int version = 1;
	
	private long total;	
	
	private double maxScore;
	
	public long getSize() {
		return null == list ? 0 : list.size();
	}
	
	private List<Object> list;
}
