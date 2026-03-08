package com.erns.coching.join.domain;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.join.domain.vg.ValidGroupUserJoin0001;
import com.erns.coching.join.domain.vg.ValidGroupUserJoin0002;
import com.erns.coching.join.domain.vg.ValidGroupUserJoin0003;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>사용자 가입 DTO</p>
 *
 * @author heejinyu@erns.co.kr
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJoinDTO extends AbstractReqDTO implements IReqDto {

	private String currentPswd;

	@NotNull(groups = {ValidGroupUserJoin0001.class,
			ValidGroupUserJoin0003.class},
			message = "비밀번호 누락")
	private String pswd;

	@NotNull(groups = {ValidGroupUserJoin0002.class},
			message = "사용자 seq 누락")
	private long membSeq;

	@NotNull(groups = {ValidGroupUserJoin0001.class,
			ValidGroupUserJoin0003.class},
			message = "사용자 아이디 누락")
	private String membId;
	@NotNull(groups = {ValidGroupUserJoin0001.class,
			ValidGroupUserJoin0003.class},
			message = "멤버 타입 누락")
	private String membType;

	@NotNull(groups = {ValidGroupUserJoin0003.class},
			message = "파트너사 seq 누락")
	private long ptnSeq;
	private String salt;

	@NotNull(groups = {ValidGroupUserJoin0001.class,
			ValidGroupUserJoin0002.class,
			ValidGroupUserJoin0003.class},
			message = "사용자 이름 누락")
	private String membName;

	@NotNull(groups = {ValidGroupUserJoin0001.class,
			ValidGroupUserJoin0002.class,
			ValidGroupUserJoin0003.class},
			message = "사용자 이름 누락")
	private String nickname;

	@NotNull(groups = {ValidGroupUserJoin0001.class,
			ValidGroupUserJoin0002.class,
			ValidGroupUserJoin0003.class},
			message = "사용자 연락처 누락")
	private String phone;

	@NotNull(groups = {ValidGroupUserJoin0001.class,
			ValidGroupUserJoin0002.class,
			ValidGroupUserJoin0003.class},
			message = "사용자 이메일 누락")
	@Email(message = "이메일 형식 오류")
	private String email;

	private String emailYn;
	private String messageYn;
    private String dormantYn;

    @NotNull(groups = {ValidGroupUserJoin0003.class},
    		message = "사업자번호 누락")
    private String ptnLic; // 사업자번호

	@NotNull(groups = {ValidGroupUserJoin0003.class},
			message = "원료사 타입 누락")
	private String nation; // 원료사 타입

    @NotNull(groups = {ValidGroupUserJoin0003.class},
    		message = "파트너사명 누락")
    private String ptnName; // 파트너사명

	@NotNull(groups = {ValidGroupUserJoin0001.class,
			ValidGroupUserJoin0003.class},
			message = "termsCd 누락")
	private String termsCd;		//TERMS_CD

	@NotNull(groups = {ValidGroupUserJoin0001.class,
			ValidGroupUserJoin0003.class},
			message = "agreeYn 누락")
	private String agreeYn;		//ARGEE_YN

	private String pageUrl;
	
	private String nationName;
	private String ytUrl;
    private String ptnIntro;


    // ----------------------------------------------------

}
