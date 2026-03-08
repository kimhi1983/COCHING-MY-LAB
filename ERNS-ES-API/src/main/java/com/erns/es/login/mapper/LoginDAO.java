package com.erns.es.login.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.erns.es.login.domain.LoginUserDTO;

/**
 *
 * <p>로그인 정보 Mapper</p>
 *
 * @author Hunwoo Park
 *
 */
@Mapper
public interface LoginDAO {

	//사용자 비밀번호 확인(주로 로그인 시)
	public LoginUserDTO checkPasswd(LoginUserDTO param);

	//비밀번호 오류 수 증가
	public int updatePwdErrorCnt(LoginUserDTO param);

	//비밀번호 오류 횟수 초기화
	public int updateResetPwdErrorCnt(LoginUserDTO param);

	//로그인 정보 기록
	public int updateLoingInfo(LoginUserDTO param);

	//로그인 정보 가져오기
	public LoginUserDTO loadUser(String userId);

}
