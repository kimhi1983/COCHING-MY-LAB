package com.erns.coching.login.service;

import com.erns.coching.login.domain.LoginUserDTO;

/**
 *
 * <p>Login Service</p>
 *
 * @author Hunwoo Park
 *
 */
public interface LoginService {

	public LoginUserDTO checkPasswd(String userId, String userPw);

	public LoginUserDTO loadUser(String userId);

	public int updatePwdErrorCnt(long membSeq);

	public int setLogin(LoginUserDTO param);

}
