package com.erns.coching.member.domain;

import javax.validation.constraints.NotNull;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.member.domain.vg.ValidAdminMember0003;
import com.erns.coching.member.domain.vg.ValidAdminMember0032;
import com.erns.coching.member.domain.vg.ValidMember0032;
import com.erns.coching.member.domain.vg.ValidSetMember0001;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
*
* <p>회원 설정 DTO</p>
*
* @author Kyungmin Lee
*
*/
@Getter
@Setter
@ToString
@NoArgsConstructor
public class MemberSetDTO extends AbstractReqDTO implements IReqDto {
	@NotNull(groups = {ValidSetMember0001.class,
			ValidAdminMember0003.class,
			ValidMember0032.class,
			ValidAdminMember0032.class},
			message = "membSeq 누락")
	private long membSeq;
	private long ptnSeq;
	@NotNull(groups = {ValidSetMember0001.class},
			message = "membId 누락")
	private String membId;
	private String pswd;
	@NotNull(groups = {ValidSetMember0001.class,
			ValidAdminMember0003.class},
			message = "membName 누락")
	private String membName;

	private String nickname;
	@NotNull(groups = {ValidSetMember0001.class},
			message = "membType 누락")
	private String membType;
	@NotNull(groups = {ValidSetMember0001.class,
			ValidAdminMember0003.class},
			message = "phone 누락")
	private String phone;
	@NotNull(groups = {ValidSetMember0001.class,
			ValidAdminMember0003.class},
			message = "email 누락")
	private String email;
	@NotNull(groups = {ValidSetMember0001.class,
			ValidAdminMember0003.class},
			message = "useYn 누락")
	private String useYn;
	private String wtdrYn;;
	private String dormantYn;
	private long rgtr;
	private long chnr;
}
