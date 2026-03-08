package com.erns.es.login.domain;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import com.erns.es.common.type.UserRole;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * <p>로그인 사용자 DTO</p>
 *
 * @author Hunwoo Park
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString(exclude = "password")
public class LoginUserDTO implements IUser, UserDetails{

	private static final long serialVersionUID = -1489147552623175610L;

	protected long seq; 		//A_MEMB_SEQ

	protected String userId;	//A_MEMB_ID
	protected String userName;	//A_MEMB_NAME
	protected String password;	//A_MEMB_PSWD
	protected String phone;		//A_MEMB_PHONE
	protected String email;		//A_MEMB_EMAIL
	protected String useYn;		//USE_YN

	protected Date pwdChangeDate;	//PSWD_CHNG_DTTM
	protected int pwdErrCnt;		//PSWD_ERR_COUNT
	protected String pwdInitYn; 	//PSWE_INIT_YN

	protected Date lastAccessDate; 	//A_RCT_ACS_DTTM
	protected String lastAccessIp;	//A_RCT_ACS_ID

	//TODO : 기타 로그인 관련 정보

	/**
	 * 로그인 From JWE
	 * @param seq
	 * @param lastAccessIp
	 */
	@Builder(builderClassName = "FromJweBuilder", builderMethodName = "FromJweBuilder")
	public LoginUserDTO(Map<String, Object> jweClaims) {
	    Assert.notNull(jweClaims, "jweClaims must not be null");

	    this.userId = (String)jweClaims.getOrDefault("userId", "");
	    this.seq = Long.parseLong(""+jweClaims.getOrDefault("userSeq", "0"));
	    this.userName = (String)jweClaims.getOrDefault("userName", "");
	}

	/**
	 * 비밀번호 확인용
	 * @param userId
	 * @param userPw
	 */
	@Builder(builderClassName = "CheckPasswdBuilder", builderMethodName = "CheckPasswdBuilder")
	public LoginUserDTO(String userId, String userPw) {
	    Assert.notNull(userId, "userId must not be null");
	    Assert.notNull(userPw, "userPw must not be null");

	    this.userId = userId;
	    this.password = userPw;
	}

	@Builder(builderClassName = "LastAccessBuilder", builderMethodName = "LastAccessBuilder")
	public LoginUserDTO(long seq, String lastAccessIp) {
	    Assert.notNull(lastAccessIp, "lastAccessIp must not be null");

	    this.seq = seq;
	    this.lastAccessIp = lastAccessIp;
	}

//	/**
//	 * 일반 사용자 로그인 From memberVO
//	 * @param seq
//	 * @param param
//	 */
//	@Builder(builderClassName = "FromUserMemberVoBuilder", builderMethodName = "FromUserMemberVoBuilder")
//	public LoginUserDTO(MemberVO param) {
//	    Assert.notNull(param, "param must not be null");
//
//	    this.email = param.getEmail();
//	    this.seq = param.getMembSeq();
//	    this.userName = param.getMembName();
//	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// 계정의 권한 목록을 리턴
        Set<GrantedAuthority> roles = new HashSet<>();
        roles.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
        return roles;
	}

	@Override
	public String getUsername() {
		return this.userId;	// 계정의 고유한 값 리턴
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
