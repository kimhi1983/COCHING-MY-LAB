package com.erns.coching.search.domain;

import javax.validation.constraints.NotEmpty;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.search.domain.vg.ValidEsManage0102;
import com.erns.coching.search.domain.vg.ValidEsManage0104;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class EsManageDTO extends BaseSearchDTO implements IReqDto{
	
//	@NotEmpty(groups = {
//			ValidEsManage0101.class,
//	    }, message = "commands 누락")
	private String commands;
	
	@NotEmpty(groups = {
			ValidEsManage0102.class,
			ValidEsManage0104.class,
	    }, message = "indexName 누락")
	private String indexName;
	
	@NotEmpty(groups = {
			ValidEsManage0102.class,
	    }, message = "mappingJson 누락")
	private String mappingJson;

}
