package com.erns.coching.raw.domain;

import javax.validation.constraints.NotNull;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.raw.domain.vg.ValidMasterRaw0032;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * <p>원료 마스터 설정 DTO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RawMasterSetDTO extends AbstractReqDTO implements IReqDto{
	
	@NotNull(groups = {
		ValidMasterRaw0032.class
	}, message = "rawSeq 누락")
	protected long rawSeq;
	protected long ptnSeq;
	protected String rawName;
	protected String prodCompany;
	protected String prodCountry;
	protected String supplier;
	protected String weight;

	protected String useYn;
	protected String delYn;
	protected String permitYn;
}
