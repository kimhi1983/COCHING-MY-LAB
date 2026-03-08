package com.erns.coching.member.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.member.domain.vg.ValidAdminMember0001;
import com.erns.coching.member.domain.vg.ValidWithdraw0002;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>회원 DTO</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class MemberWithdrawDTO extends AbstractReqDTO implements IReqDto{

//	@NotNull(groups = {ValidWithdraw0002.class},
//			message = "membSeqID 누락")
	private Long membSeq;

//	@NotBlank(groups = {ValidWithdraw0002.class},
//			message = "사용자ID 누락")
	private String email;

	@NotBlank(groups = {ValidWithdraw0002.class},
			message = "withdrawToken 누락")
	private String withdrawToken;

	@NotBlank(groups = {ValidWithdraw0002.class},
			message = "withdrawReason 누락")
	private String withdrawReason;

//	@NotBlank(groups = {ValidWithdraw0002.class},
//			message = "withdrawReasonTxt 누락")
	private String withdrawReasonTxt;

	@NotBlank(groups = {ValidWithdraw0002.class},
			message = "delAccountAgreeYn 누락")
	@Pattern(groups = {ValidAdminMember0001.class},
			message = "delAccountAgreeYn 형식오류",
			regexp = "Y|N")
	private String delAccountAgreeYn;

	@NotBlank(groups = {ValidWithdraw0002.class},
			message = "delLinkInfoYn 누락")
	@Pattern(groups = {ValidAdminMember0001.class},
			message = "delLinkInfoYn 형식오류",
			regexp = "Y|N")
	private String delLinkInfoYn;
}
