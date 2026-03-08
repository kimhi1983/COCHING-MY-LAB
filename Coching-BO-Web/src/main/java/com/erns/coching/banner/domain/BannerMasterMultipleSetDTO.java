package com.erns.coching.banner.domain;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import com.erns.coching.banner.domain.vg.ValidBannerMaster0032;
import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * <p>배너 마스터 복수 설정 DTO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class BannerMasterMultipleSetDTO extends AbstractReqDTO implements IReqDto {

	@NotEmpty(groups = {
			ValidBannerMaster0032.class
    }, message = "list 누락")
	private List<BannerMasterSetDTO> list;

}