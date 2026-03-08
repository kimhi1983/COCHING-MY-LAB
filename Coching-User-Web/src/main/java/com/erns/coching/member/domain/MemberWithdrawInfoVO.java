package com.erns.coching.member.domain;

import java.util.Date;

import org.springframework.util.Assert;

import com.erns.coching.login.domain.LoginUserDTO;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <p>탈회 정보 VO
 * T_MEMB_WTHDR_INF</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString()
public class MemberWithdrawInfoVO {

	protected long seq;					//WTHDR_SEQ - 탈회SEQ
	protected long membSeq;				//MEMB_SEQ - 사용자SEQ

	protected String withdrawReason;	//WTHDR_RSN - 탈회사유
	protected String delAccountAgreeYn; //DEL_ACNT  - 계정삭제동의
	protected String delLinkInfoYn;		//DEL_INTRL - 연동정보삭제동의
	protected String withdrawReasonTxt; //WTHDR_RSN_TXT - 탈회사유 기타

	protected long rgtr;				//RGTR	-	등록자
	protected Date rgtDttm;				//RGT_DTTM	- 등록일

	@Builder(builderClassName = "MemberWidthdrawBuilder", builderMethodName = "MemberWidthdrawBuilder")
	public MemberWithdrawInfoVO(MemberWithdrawDTO fromDto, LoginUserDTO rgtr) {
	    Assert.notNull(fromDto, "fromDto must not be null");

	    this.seq = 0L;
		this.membSeq = fromDto.getMembSeq();

		this.withdrawReason = fromDto.getWithdrawReason();
		this.withdrawReasonTxt = fromDto.getWithdrawReasonTxt();
	    this.delAccountAgreeYn = fromDto.getDelAccountAgreeYn();
	    this.delLinkInfoYn = fromDto.getDelLinkInfoYn();

		this.rgtr = rgtr.getSeq();
	}

}
