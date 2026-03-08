package com.erns.coching.member.domain;

import javax.validation.constraints.NotEmpty;


import com.erns.coching.member.domain.vg.ValidMemberList0001;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>회원 정보 설정 DTO</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class SetMemberInfoDTO {


	private String email;

	@NotEmpty(groups = {
			ValidMemberList0001.class,
	}, message = "ptnSeq 누락")
	private String ptnSeq;
}
