package com.erns.coching.member.domain;

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.Assert;

/**
 * <p>회원 약관 동의 내역 VO</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString()
public class MemberTermsAgreementHistoryVO {

	protected long membSeq;		//MEMB_SEQ	사용자SEQ
	protected String termsCd;	//TERMS_CD	약관CD
	protected String agreeYn;	//AGREE_YN	동의여부

	protected long rgtr;		//RGTR	등록자
	protected Date rgtDttm;	//RGT_DTTM	등록일

	/**
	 * 약관 동의
	 * @param membSeq
	 */
	@Builder(builderClassName = "SetMemberTermsBuilder", builderMethodName = "SetMemberTermsBuilder")
	public MemberTermsAgreementHistoryVO(long membSeq, String termsCd, String agreeYn) {
		Assert.notNull(membSeq, "membSeq must not be null");
		Assert.notNull(termsCd, "termsCd must not be null");
		Assert.notNull(agreeYn, "agreeYn must not be null");

		this.membSeq = membSeq;
		this.termsCd = termsCd;
		this.agreeYn = agreeYn;
		this.rgtr = membSeq;
	}
}
