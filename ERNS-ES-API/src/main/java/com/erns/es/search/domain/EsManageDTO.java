package com.erns.es.search.domain;

import javax.validation.constraints.NotEmpty;

import com.erns.es.common.model.BaseSearchDTO;
import com.erns.es.common.model.IReqDto;
import com.erns.es.search.domain.vg.ValidEsManage0102;
import com.erns.es.search.domain.vg.ValidEsManage0104;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
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
