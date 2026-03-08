package com.erns.coching.banner.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.erns.coching.banner.domain.vg.ValidBanner0011;
import com.erns.coching.banner.domain.vg.ValidBanner0012;
import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * <p>배너 검색 DTO</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class BannerAdSearchDTO extends BaseSearchDTO implements IReqDto{

	@NotNull(groups = {
			ValidBanner0012.class,
    	}, message = "bannerSeq 누락")
	@Min(groups = {
			ValidBanner0012.class,
		},
		value = 1,
		message = "bannerSeq 누락")
	private long bannerSeq;
	
	@NotBlank(groups = {
			ValidBanner0011.class,
			ValidBanner0012.class,
    	}, message = "bannerMstCd 누락")
	private String bannerMstCd;
	
	private String titleL;	//제목검색

	private String useYn;
	private String delYn;
	private String dispYn;

}
