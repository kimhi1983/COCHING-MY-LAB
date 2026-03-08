package com.erns.coching.login.domain;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import com.erns.coching.common.type.UserRole;
import com.erns.coching.member.domain.MemberVO;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <p>사용자 로그인 DTO</p>
 *
 * @author heejinyu@erns.co.kr
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(exclude = "password")
public class LoginUserDTO implements IUser, UserDetails{

	private static final long serialVersionUID = -1489147552623175610L;

	protected long seq; 		//MEMB_SEQ
	protected long ptnSeq;
	protected String userType;	//MEMB_TYPE
	protected String userName;	//MEMB_NAME
	protected String id;		//ID

	protected String password;	//PSWD
	protected String phone;		//PHONE
	protected String email;
	protected String nickname;	//NICKNAME

	protected String useYn;		//USE_YN 사용여부
	protected String wtdrYn;	//WTDR_YN 탈회여부
	protected String dormantYn;	//DORMANT_YN 휴면여부

	protected Date pwdChangeDate;	//PSWD_CHNG_DTTM 비밀번호 변경일
	protected int pwdErrCnt;		//PSWD_ERR_CNT 비밀번호 틀린 횟수
	protected String pwdInitYn; 	//PSWE_INIT_YN 비밀번호 초기화여부
	protected Date pswdExpDttm;		//PSWD_EXP_DTTM 비밀번호 만료일자

	protected Date lastAccessDate; 	//RCT_ACS_DTTM
	protected String lastAccessIp;	//RCT_ACS_IP

	//기타 로그인 관련 정보
	protected String retPassCheckYn;	//비밀번호 확인 여부

	/**
	 * 로그인 From JWE
	 * @param jweClaims
	 */
	@Builder(builderClassName = "FromJweBuilder", builderMethodName = "FromJweBuilder")
	public LoginUserDTO(Map<String, Object> jweClaims) {
	    Assert.notNull(jweClaims, "jweClaims must not be null");

	    this.id = (String)jweClaims.getOrDefault("id", "");
	    this.seq = Long.parseLong(""+jweClaims.getOrDefault("userSeq", "0"));
		this.ptnSeq = Long.parseLong(""+jweClaims.getOrDefault("ptnSeq", "0"));
		this.userType = (String)jweClaims.getOrDefault("userType", "");
	    this.userName = (String)jweClaims.getOrDefault("userName", "");
	}

	/**
	 * 로그인 From memberVO
	 * @param param
	 */
	@Builder(builderClassName = "FromMemberVoBuilder", builderMethodName = "FromMemberVoBuilder")
	public LoginUserDTO(MemberVO param) {
	    Assert.notNull(param, "param must not be null");

	    this.id = param.getMembId();
	    this.seq = param.getMembSeq();
		this.ptnSeq = param.getPtnSeq();
		this.userType = param.getMembType();
	    this.userName = param.getMembName();
	}

	/**
	 * 비밀번호 확인용
	 * @param id
	 * @param userPw
	 */
	@Builder(builderClassName = "CheckPasswdBuilder", builderMethodName = "CheckPasswdBuilder")
	public LoginUserDTO(String id, String userPw) {
	    Assert.notNull(id, "userId must not be null");
	    Assert.notNull(userPw, "userPw must not be null");

	    this.id = id;
	    this.password = userPw;
	}

	@Builder(builderClassName = "LastAccessBuilder", builderMethodName = "LastAccessBuilder")
	public LoginUserDTO(long seq, String lastAccessIp) {
	    Assert.notNull(lastAccessIp, "lastAccessIp must not be null");

	    this.seq = seq;
	    this.lastAccessIp = lastAccessIp;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// 계정의 권한 목록을 리턴
        Set<GrantedAuthority> roles = new HashSet<>();
        roles.add(new SimpleGrantedAuthority(UserRole.COCHING_USER.getValue()));
        return roles;
	}

	@Override
	public String getUsername() {
		return this.id;	// 계정의 고유한 값 리턴
	}

	@Override
	public boolean isAccountNonExpired() {
		return "Y".equals(this.useYn);	// 계정의 만료 여부 리턴
	}

	@Override
	public boolean isAccountNonLocked() {
		return "Y".equals(this.useYn);	// 계정의 잠김 여부 리턴
	}

	@Override
	public boolean isCredentialsNonExpired() {
		//TODO : 비밀번호 유효기간
		return !"Y".equals(this.pwdInitYn);  // 비밀번호 만료 여부 리턴
	}

	@Override
	public boolean isEnabled() {
		return "Y".equals(this.useYn);	// 계정의 활성화 여부 리턴
	}

	@Override
	public String getUserName() {
		return this.userName;
	}

}
