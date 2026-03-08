package com.erns.coching.banner.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.erns.coching.banner.domain.vg.ValidBanner0011;
import com.erns.coching.banner.domain.vg.ValidBanner0012;
import com.erns.coching.banner.domain.vg.ValidBannerMaster0012;
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
public class BannerSearchDTO extends BaseSearchDTO implements IReqDto{

	@NotNull(groups = {		
			ValidBanner0012.class,
		}, message = "bannerSeq 누락")
	@Min(groups = {
			ValidBanner0012.class,
		}, 
		value = 1,
		message = "bannerSeq 누락")
	private long bannerSeq;

	@NotEmpty(groups = {
			ValidBannerMaster0012.class,
			ValidBanner0011.class,
		}, message = "bannerMstCd 누락")
	private String bannerMstCd;

	private String bannerName;
	private String bannerNameL;
	private int sortOrd;

	private String useYn;
	private String delYn;

}
