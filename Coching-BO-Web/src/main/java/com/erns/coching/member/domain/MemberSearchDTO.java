package com.erns.coching.member.domain;

import javax.validation.constraints.NotNull;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.member.domain.vg.ValidMember0003;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * <p>회원 정보 검색 DTO</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberSearchDTO extends BaseSearchDTO implements IReqDto{

	//사용자 기본정보
	private String allSrch;
	@NotNull(groups = {ValidMember0003.class},
			message = "membSeq 누락")
	private long membSeq;

	private long ptnSeq;

	private String membId;

	private String email;

	private String nickname;

	private String membIdL;

	private String membType;

	private String menuType;

	private String emailL;

	private String membNameL;

	private String phoneL;

	private String joinFromDate;

	private String joinToDate;

	private String useYn;	//사용여부

	private String wtdrYn;	//탈퇴여부

	private String wtdrFromDate;	//탈퇴일 from

	private String wtdrToDate;		//탈퇴일 to

	private String rctFromDate;		//최근접속일 from

	private String rctToDate;		//최근접속일 to

	protected String dormantYn;	//DORMANT_YN(휴면여부)

}
