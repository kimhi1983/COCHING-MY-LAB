package com.erns.es.code.domain;

import javax.validation.constraints.NotBlank;

import com.erns.es.code.domain.vg.ValidGroupAddress0001;
import com.erns.es.common.model.BaseSearchDTO;
import com.erns.es.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * <p>도로명 주소검색 DTO</p>
 *
 * @author Hunwoo Park
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class AddressSearchDTO extends BaseSearchDTO implements IReqDto {

	@NotBlank(groups = {ValidGroupAddress0001.class},
    		message = "keyword 검색어 누락")
	protected String keyword;
}
