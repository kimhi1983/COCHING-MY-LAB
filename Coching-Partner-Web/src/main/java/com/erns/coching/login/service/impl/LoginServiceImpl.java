package com.erns.coching.login.service.impl;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.erns.coching.login.domain.LoginUserDTO;
import com.erns.coching.login.mapper.LoginDAO;
import com.erns.coching.login.service.LoginService;

/**
 *
 * <p>Login Service</p>
 *
 * @author Kyungmin Lee
 *
 */
@Transactional
@Service
public class LoginServiceImpl implements LoginService, UserDetailsService {

	@Autowired
	private LoginDAO dao;

	@Override
	public LoginUserDTO checkPasswd(String userId, String userPw) {

		LoginUserDTO param = LoginUserDTO.CheckPasswdBuilder()
				.id(userId)
				.userPw(userPw)
				.build();

		LoginUserDTO findUser = dao.checkPasswd(param);
		return findUser;
	}

	@Override
	public int updatePwdErrorCnt(long membSeq) {
		return dao.updatePwdErrorCnt(membSeq);
	}

	@Override
	public int setLogin(LoginUserDTO param) {

		//패스워드 오류 초기화
		dao.updateResetPwdErrorCnt(param);

		//로그인 정보 (접속시간, IP)
		HttpServletRequest req = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		String accessIp = req.getRemoteAddr();

		LoginUserDTO loginInfoParam = LoginUserDTO.LastAccessBuilder()
					.seq(param.getSeq())
					.lastAccessIp(accessIp)
					.build();

		return dao.updateLoingInfo(loginInfoParam);
	}

	@Override
	public LoginUserDTO loadUser(String userId) {
		return dao.loadUser(userId);
	}

	@Override
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
		return dao.loadUser(id);
	}

}
