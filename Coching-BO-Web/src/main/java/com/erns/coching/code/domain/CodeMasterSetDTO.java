package com.erns.coching.code.domain;

import javax.validation.constraints.NotEmpty;

import com.erns.coching.code.domain.vg.ValidGroupCode0032;
import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * <p>공통코드 마스터 설정 DTO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CodeMasterSetDTO extends AbstractReqDTO implements IReqDto{
	
	@NotEmpty(groups = {
		ValidGroupCode0032.class
	}, message = "popupMstCd 누락")
	private String grpCode;
	private String grpCodeName;
	private String codeDesc;
	private String etcCmmt;
	private String useYn;
	private String delYn;
}
