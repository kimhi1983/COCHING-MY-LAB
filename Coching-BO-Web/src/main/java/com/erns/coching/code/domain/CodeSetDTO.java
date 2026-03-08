package com.erns.coching.code.domain;

import javax.validation.constraints.NotEmpty;

import com.erns.coching.code.domain.vg.ValidCode0031;
import com.erns.coching.code.domain.vg.ValidCode0032;
import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * <p>공통코드 설정 DTO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CodeSetDTO extends AbstractReqDTO implements IReqDto{
	
	@NotEmpty(groups = {
		ValidCode0032.class,
		ValidCode0031.class
	}, message = "code 누락")
    private String code;
	
	@NotEmpty(groups = {
		ValidCode0032.class,
		ValidCode0031.class
	}, message = "grpCode 누락")
    private String grpCode;
	
    private String codeName;
    private String etc1;
    private String etc2;
    private String etc3;
    private String etc4;
    private String etc5;
    private String etcCmmt;
    private String codeDesc;
    private String remarks;
    private int sortOrd;
    private String useYn;
}
