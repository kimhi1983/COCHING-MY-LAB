package com.erns.coching.join.domain;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.join.domain.vg.ValidGroupUserJoin0001;
import com.erns.coching.join.domain.vg.ValidGroupUserJoin0002;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>계정 생성 DTO</p>
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
public class UserJoinDTO extends AbstractReqDTO implements IReqDto {

	@NotNull(groups = {ValidGroupUserJoin0001.class},
			message = "비밀번호 누락")
	private String pswd;

	@NotNull(groups = {ValidGroupUserJoin0002.class},
			message = "사용자 seq 누락")
	private long membSeq;

	@NotNull(groups = {ValidGroupUserJoin0001.class,
			ValidGroupUserJoin0002.class},
			message = "사용자 아이디 누락")
	private String membId;
	@NotNull(groups = {ValidGroupUserJoin0001.class},
			message = "멤버 타입 누락")
	private String membType;

	private long ptnSeq;
	private String salt;

	@NotNull(groups = {ValidGroupUserJoin0001.class,
			ValidGroupUserJoin0002.class},
			message = "사용자 이름 누락")
	private String membName;

	private String nickname;

	@NotNull(groups = {ValidGroupUserJoin0001.class,
			ValidGroupUserJoin0002.class},
			message = "사용자 연락처 누락")
	private String phone;

	@NotNull(groups = {ValidGroupUserJoin0001.class,
			ValidGroupUserJoin0002.class},
			message = "사용자 이메일 누락")
	@Email(message = "이메일 형식 오류")
	private String email;
	@NotNull(groups = {ValidGroupUserJoin0001.class,
			ValidGroupUserJoin0002.class},
			message = "사용여부 누락")
	private String useYn;

	private String emailYn;
	private String messageYn;
	private String dormantYn;
	private String nation;
	private long rgtr;

}
