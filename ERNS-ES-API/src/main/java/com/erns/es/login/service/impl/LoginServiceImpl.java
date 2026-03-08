package com.erns.es.login.service.impl;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.erns.es.login.domain.LoginUserDTO;
import com.erns.es.login.mapper.LoginDAO;
import com.erns.es.login.service.LoginService;

/**
 *
 * <p>로그인 Service</p>
 *
 * @author Hunwoo Park
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
				.userId(userId)
				.userPw(userPw)
				.build();

		LoginUserDTO findUser = dao.checkPasswd(param);
		return findUser;
	}

	@Override
	public int updatePwdErrorCnt(LoginUserDTO param) {
		return dao.updatePwdErrorCnt(param);
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
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		return dao.loadUser(userId);
	}

}
