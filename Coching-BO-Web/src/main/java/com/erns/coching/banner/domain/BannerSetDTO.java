package com.erns.coching.banner.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.erns.coching.banner.domain.vg.ValidBanner0020;
import com.erns.coching.banner.domain.vg.ValidBanner0030;
import com.erns.coching.banner.domain.vg.ValidBanner0031;
import com.erns.coching.banner.domain.vg.ValidBanner0032;
import com.erns.coching.banner.domain.vg.ValidBanner0040;
import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * <p>배너 설정 DTO</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class BannerSetDTO extends AbstractReqDTO implements IReqDto{

	@NotEmpty(groups = {
	        ValidBanner0020.class,
	        ValidBanner0030.class,
	        ValidBanner0031.class,
					ValidBanner0032.class,
	        ValidBanner0040.class,
	    }, message = "bannerMstCd 누락")
	private String bannerMstCd;

	@NotNull(groups = {
			ValidBanner0030.class,
			ValidBanner0031.class,
			ValidBanner0032.class,
			ValidBanner0040.class,
        }, message = "bannerSeq 누락")
	@Min(groups = {
			ValidBanner0030.class,
			ValidBanner0031.class,
			ValidBanner0032.class,
			ValidBanner0040.class,
		}, value = 1,message = "bannerSeq 누락")
	private Long bannerSeq;

	@NotEmpty(groups = {
			ValidBanner0020.class,
			ValidBanner0030.class
	    }, message = "bannerName 누락")
	private String bannerName;

	@Pattern(groups = {
			ValidBanner0020.class,
			ValidBanner0030.class,
		},regexp = "^(19|20)\\d{2}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[0-1])$"
		, message = "fromDate 형식오류.")
	private String fromDate;

	@Pattern(groups = {
			ValidBanner0020.class,
			ValidBanner0030.class,
		},regexp = "^(19|20)\\d{2}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[0-1])$"
		, message = "toDate 형식오류.")
	private String toDate;

	private String bannerDesc;

	private String bannerUrl;

	private int bannerWidth;
	private int bannerHeight;
	private int clicks;

	@Min(groups = {
			ValidBanner0031.class
		}, value = 1, message = "sortOrd 누락")
	private int sortOrd;

	@NotEmpty(groups = {
			ValidBanner0020.class,
			ValidBanner0030.class,
			ValidBanner0032.class,
		}, message = "useYn 누락")
	@Pattern(groups = {
			ValidBanner0020.class,
			ValidBanner0030.class,
			ValidBanner0032.class,
		}, message = "useYn 형식오류(Y|N)", regexp = "Y|N")
	private String useYn;

	@NotEmpty(groups = {
			ValidBanner0032.class,
		  ValidBanner0040.class,
		}, message = "delYn 누락")
	@Pattern(groups = {
			ValidBanner0032.class,
			ValidBanner0040.class,
		}, message = "delYn 형식오류(Y|N)", regexp = "Y|N")
	private String delYn;
}
