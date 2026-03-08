package com.erns.coching.mltln.domain;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * <p>다국어 설정 DTO</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class MltlnSetDTO extends AbstractReqDTO implements IReqDto {

	protected String code;
	protected String codeNmKo;
	protected String codeNmEn;
}
