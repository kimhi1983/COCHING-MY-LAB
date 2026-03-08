package com.erns.coching.code.domain;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import com.erns.coching.code.domain.vg.ValidCode0032;
import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * <p>공통코드 복수 설정 DTO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CodeMultipleSetDTO extends AbstractReqDTO implements IReqDto {

	@NotEmpty(groups = {
		ValidCode0032.class
    }, message = "list 누락")
	private List<CodeSetDTO> list;

}