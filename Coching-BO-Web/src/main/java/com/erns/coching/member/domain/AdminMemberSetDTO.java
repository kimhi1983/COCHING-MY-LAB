package com.erns.coching.member.domain;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.member.domain.vg.ValidAdminMember0001;
import com.erns.coching.member.domain.vg.ValidAdminMember0003;
import com.erns.coching.member.domain.vg.ValidAdminMember0004;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * <p>관리자 정보 설정 DTO</p>
 *
 * @author Hunwoo Park
 *
 */
@Getter
@Setter
@ToString(exclude = "password")
@NoArgsConstructor
public class AdminMemberSetDTO extends AbstractReqDTO implements IReqDto{

	@NotNull(groups = {
				ValidAdminMember0003.class,
				ValidAdminMember0004.class
			},
			message = "관리자seq 누락")
	private Long seq;

	@NotNull(groups = {ValidAdminMember0001.class},
			message = "userIdID 누락")
	private String userId;

	@NotNull(groups = {ValidAdminMember0001.class},
			message = "비밀번호 누락")
	@Size(groups = {ValidAdminMember0001.class},
			min = 6, max = 16,
			message = "비밀번호 6자리 이상 16자리 이하")
	private String password;

	@NotNull(groups = {
				ValidAdminMember0001.class,
				ValidAdminMember0003.class
			},
			message = "userName 누락")
	private String userName;

	@NotNull(groups = {
				ValidAdminMember0001.class,
				ValidAdminMember0003.class
			},
			message = "phone 누락")
	private String phone;

	@NotNull(groups = {
				ValidAdminMember0001.class,
				ValidAdminMember0003.class
			},
			message = "email 누락")
	@Email(message = "email 형식 오류")
	private String email;

	@NotNull(groups = {
				ValidAdminMember0001.class,
				ValidAdminMember0003.class
			},
			message = "useYn 누락")
	@Pattern(groups = {
				ValidAdminMember0001.class,
				ValidAdminMember0003.class
			},
			message = "useYn 형식오류",
			regexp = "Y|N")
	private String useYn;

}
