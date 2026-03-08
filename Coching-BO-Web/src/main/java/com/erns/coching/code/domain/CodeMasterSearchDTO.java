package com.erns.coching.code.domain;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * <p>공통코드 마스터 검색 DTO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CodeMasterSearchDTO extends BaseSearchDTO implements IReqDto{
	
	private String grpCode;
	private String grpCodeName;
	private String codeDesc;
	private String etcCmmt;
	private String useYn;
	private String delYn;
	
	private String searchText;
	
}
