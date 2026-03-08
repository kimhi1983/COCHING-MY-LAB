package com.erns.coching.raw.domain;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.raw.domain.vg.ValidMasterRaw0032;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * <p>원료 마스터 복수 설정 DTO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RawMasterMultipleSetDTO extends AbstractReqDTO implements IReqDto {

	@NotEmpty(groups = {
		ValidMasterRaw0032.class
    }, message = "list 누락")
	private List<RawMasterSetDTO> list;

}