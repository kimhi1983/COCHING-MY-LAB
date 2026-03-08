package com.erns.coching.partner.domain;

import java.util.Date;

import org.springframework.util.Assert;

import com.erns.coching.join.domain.UserJoinDTO;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * <p>파트너사 VO
 * T_PARTNER_INF</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class PartnerVO {

	protected long ptnSeq;			//파트너사SEQ
	protected String ptnName;		//파트너사명
	protected String nation;		//국가
	protected String ptnLic;		//사업자번호
	protected String pageUrl;		//홈페이지URL
	protected String logoFileId;	//로고이미지
	protected String delYn;			//삭제여부
	protected String nationName;	// 국가명
	protected String ytUrl;		// Youtube URL
	protected String ptnIntro;		// 원료사 소개
	protected long rgtr;			//등록자
	protected Date rgtDttm;			//등록일
	protected long chnr;			//수정자
	protected Date chngDttm;		//수정일

	protected void setFromPtnSetDTO(PartnerSetDTO fromDto) {
		this.ptnSeq = fromDto.getPtnSeq();
		this.ptnName = fromDto.getPtnName();
		this.nation = fromDto.getNation();
		this.ptnLic = fromDto.getPtnLic();
		this.pageUrl = fromDto.getPageUrl();
		this.logoFileId = fromDto.getLogoFileId();
		this.delYn = fromDto.getDelYn();
	}

	/**
	 * 파트너사 등록 Builder
	 * @param fromDto
	 */
	@Builder(builderClassName = "AddPtnBuilder", builderMethodName = "AddPtnBuilder")
	public PartnerVO(UserJoinDTO fromDto, long ptnSeq, long rgtr) {
	    Assert.notNull(fromDto, "fromDto must not be null");

		this.ptnSeq = ptnSeq;
		this.nation = fromDto.getNation();
		this.ptnName = fromDto.getPtnName();
	    this.ptnLic = fromDto.getPtnLic();
		this.pageUrl = fromDto.getPageUrl();
		this.rgtr = rgtr;
		this.nationName = fromDto.getNationName();
		this.ytUrl = fromDto.getYtUrl();
		this.ptnIntro = fromDto.getPtnIntro();
	}

	/**
	 * 파트너사 수정 Builder
	 * @param fromDto
	 */
	@Builder(builderClassName = "UpdatePtnBuilder", builderMethodName = "UpdatePtnBuilder")
	public PartnerVO(UserJoinDTO fromDto) {
		Assert.notNull(fromDto, "fromDto must not be null");

		this.ptnSeq = fromDto.getPtnSeq();
		this.ptnName = fromDto.getPtnName();
		this.ptnLic = fromDto.getPtnLic();
		this.pageUrl = fromDto.getPageUrl();
		this.chnr = fromDto.getMembSeq();
		this.nationName = fromDto.getNationName();
		this.ytUrl = fromDto.getYtUrl();
		this.ptnIntro = fromDto.getPtnIntro();
	}

}
