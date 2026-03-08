package com.erns.es.login.service;

import com.erns.es.login.domain.LoginUserDTO;

/**
 *
 * <p>로그인 Service</p>
 *
 * @author Hunwoo Park
 *
 */
public interface LoginService {

	public LoginUserDTO checkPasswd(String userId, String userPw);

	public LoginUserDTO loadUser(String userId);

	public int updatePwdErrorCnt(LoginUserDTO param);

	public int setLogin(LoginUserDTO param);

}
