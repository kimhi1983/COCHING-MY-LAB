package com.erns.coching.member.domain;

import javax.validation.constraints.NotNull;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;

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
public class MemberSearchDTO extends BaseSearchDTO implements IReqDto {

	@NotNull(groups = {},
			message = "membSeq 누락")
	private long membSeq;

	private long ptnSeq;

	private String joinFromDate;

	private String joinToDate;

	private String pswd;

	private String useYn;			//사용여부

	private String wtdrYn;			//탈퇴여부

	private String wtdrFromDate;	//탈퇴일 from

	private String wtdrToDate;		//탈퇴일 to

	private String rctFromDate;		//최근접속일 from

	private String rctToDate;		//최근접속일 to

	private String dormantYn;	//DORMANT_YN(휴면여부)
}
