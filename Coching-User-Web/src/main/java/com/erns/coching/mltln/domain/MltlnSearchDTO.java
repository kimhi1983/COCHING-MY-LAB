package com.erns.coching.mltln.domain;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>다국어 검색 DTO</p>
 * @author Kyungmin Lee
 *
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MltlnSearchDTO extends BaseSearchDTO implements IReqDto {

	private String allSrch;

	private String code;
	private String codeL;
	private String codeRL;

	protected String codeNmKoL;
	protected String codeNmEnL;

}
