package com.erns.coching.member.domain;

import javax.validation.constraints.NotNull;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.member.domain.vg.ValidMemberList0001;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>회원 검색 DTO</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class MemberSearchDTO extends AbstractReqDTO implements IReqDto {

	@NotNull(groups = {},
			message = "membSeq 누락")
	private long membSeq;

	@NotNull(groups = {ValidMemberList0001.class},
			message = "ptnSeq 누락")
	private long ptnSeq;

	private String membId;

	private String membType;

	private String email;

	private String nickname;

	private String emailL;

	private String membNameL;

	private String nicknameL;

	private String phoneL;

	private String joinFromDate;

	private String joinToDate;


	private String useYn;			//사용여부

	private String wtdrYn;			//탈퇴여부

	private String wtdrFromDate;	//탈퇴일 from

	private String wtdrToDate;		//탈퇴일 to

	private String rctFromDate;		//최근접속일 from

	private String rctToDate;		//최근접속일 to

	private String dormantYn;	//DORMANT_YN(휴면여부)
}
