package com.erns.coching.member.domain;

import java.util.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.join.domain.vg.ValidGroupUserJoin0001;
import com.erns.coching.join.domain.vg.ValidGroupUserJoin0002;
import com.erns.coching.member.domain.vg.ValidMember0002;
import com.erns.coching.member.domain.vg.ValidMember0003;
import com.erns.coching.member.domain.vg.ValidMember0004;
import com.erns.coching.member.domain.vg.ValidMember0005;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>회원 설정 DTO</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@Setter
@ToString()
@NoArgsConstructor
public class MemberSetDTO extends AbstractReqDTO implements IReqDto{

	@NotNull(groups = {ValidMember0002.class,
				ValidMember0003.class,
				ValidMember0004.class,
				ValidMember0005.class},
	message = "membSeq 누락")
	private long membSeq;		//MEMB_SEQ

	private String membId;

	@NotNull(groups = {ValidGroupUserJoin0001.class},
	message = "사용자 타입 누락")
	private String membType;

	private long ptnSeq;
	private String salt;

	@NotNull(groups = {ValidGroupUserJoin0001.class,
				ValidMember0005.class},
	message = "비밀번호 누락")
	@Size(groups = {ValidGroupUserJoin0001.class,
			ValidMember0005.class},
	min = 6, max = 16,
	message = "비밀번호 6자리 이상 16자리 이하")
	private String pswd;

	@NotNull(groups = {},
	message = "사용자 이름 누락")
	private String membName;

	private String nickname;

	@NotNull(groups = {ValidGroupUserJoin0002.class},
			message = "사용자 연락처 누락")
	private String useYn;

	@NotNull(groups = {ValidGroupUserJoin0001.class},
	message = "사용자 연락처 누락")
	private String phone;

	@NotNull(groups = {ValidGroupUserJoin0001.class},
	message = "사용자 이메일 누락")
	@Email(message = "이메일 형식 오류")
	private String email;

	private String emailYn;
	private String messageYn;
	private String dormantYn;
	protected long rgtr;			// 등록자
	protected Date rgtDttm;			// 등록일
	protected long chnr;			// 수정자
	protected Date chngDttm;		// 수정일
}
