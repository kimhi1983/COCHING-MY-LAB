package com.erns.coching.code.domain;

import com.erns.coching.code.domain.vg.ValidGroupAddress0001;
import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

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
