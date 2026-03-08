package com.erns.coching.member.domain;

import java.util.Date;

import com.erns.coching.join.domain.UserJoinDTO;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.Assert;

/**
 *
 * <p>회원 기본정보 VO
 * T_MEMB</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString()
public class MemberVO {

	protected long membSeq;			// 사용자 SEQ
	protected String membId;		// 아이디
	protected String membType;		// 사용자 타입
	protected long ptnSeq;			// 파트너사 SEQ
	protected String salt;			// 암호화키값
	protected String pswd;			// 비밀번호
	protected String membName;		// 이름
	protected String nickname;		// 닉네임
	protected String phone;			// 연락처
	protected String email;			// 이메일
	protected String emailYn;		// 메일수신여부
	protected String messageYn;		// 쪽지수신여부
	protected Date joinDttm;		// 가입일
	protected String useYn;			// 사용여부
	protected String wtdrYn;		// 탈퇴여부
	protected Date wtdrDttm;		// 탈퇴일
	protected String dormantYn;		// 휴면여부
	protected Date pswdChngDttm;	// 비밀번호 변경일
	protected long pswdErrCnt;		// 비밀번호 틀린 횟수
	protected String pswdInitYn;	// 비밀번호 초기화여부
	protected Date pswdExpDttm;		// 비밀번호 만료일
	protected Date rctAcsDttm;		// 최근 접속일
	protected String rctAcsIp;		// 최근 접속 IP
	protected long rgtr;			// 등록자
	protected Date rgtDttm;			// 등록일
	protected long chnr;			// 수정자
	protected Date chngDttm;		// 수정일

	protected String ptnName;		//원료사 이름

	@Builder(builderClassName = "MemberSearchBuilder", builderMethodName = "MemberSearchBuilder")
	public MemberVO(UserJoinDTO dto, long ptnSeq) {

		this.ptnSeq = ptnSeq;
		this.membSeq = getMembSeq();
		this.email = dto.getEmail();
		this.useYn = "Y";
	}

	@Builder(builderClassName = "MemberJoinBuilder", builderMethodName = "MemberJoinBuilder")
	public MemberVO(UserJoinDTO dto) {
		Assert.notNull(dto, "dto must not be null");

		if( 0 < ptnSeq) {
			this.ptnSeq = ptnSeq;
		}
		this.membId = dto.getMembId();
		this.membName = dto.getMembName();
		this.pswd = dto.getPswd();
		this.phone = dto.getPhone();
		this.email = dto.getEmail();
		this.membType = dto.getMembType();
		this.useYn = dto.getUseYn();
		this.rgtr = dto.getRgtr();
	}

	@Builder(builderClassName = "UpdateMemberBuilder", builderMethodName = "UpdateMemberBuilder")
	public MemberVO(MemberSetDTO dto) {

		this.membSeq = dto.getMembSeq();
		this.membName = dto.getMembName();
		this.nickname = dto.getNickname();
		this.phone = dto.getPhone();
		this.email = dto.getEmail();
		this.useYn = dto.getUseYn();
		this.chnr = dto.getChnr();
	}

	@Builder(builderClassName = "UpdateMemberUseYnBuilder", builderMethodName = "UpdateMemberUseYnBuilder")
	public MemberVO(long membSeq, String useYn, long chnr) {

		this.membSeq = membSeq;
		this.useYn = useYn;
		this.chnr = chnr;
	}

	@Builder(builderClassName = "MemberDuplicateBuilder", builderMethodName = "MemberDuplicateBuilder")
	public MemberVO(String membId, String email, String nickname) {

		this.membId = membId;
		this.email = email;
		this.nickname = nickname;
	}

	@Builder(builderClassName = "MemberUpdatePswdBuilder", builderMethodName = "MemberUpdatePswdBuilder")
	public MemberVO(long membSeq, String pswd, String pswdInitYn, long seq) {

		this.membSeq = membSeq;
		this.pswd = pswd;
		this.pswdInitYn = pswdInitYn;
		this.chnr = seq;
	}

	@Builder(builderClassName = "MemberMailBuilder", builderMethodName = "MemberMailBuilder")
	public MemberVO(long membSeq) {

		this.membSeq = membSeq;
	}

}
