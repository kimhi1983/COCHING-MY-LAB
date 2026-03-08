package com.erns.coching.rawType.domain;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.rawType.domain.vg.ValidRawType0032;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * <p>성분 구분 복수 설정 DTO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RawTypeMultipleSetDTO extends AbstractReqDTO implements IReqDto {

	@NotEmpty(groups = {
		ValidRawType0032.class
    }, message = "list 누락")
	private List<RawTypeSetDTO> list;

}