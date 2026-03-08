package com.erns.coching.login.service.impl;

import javax.servlet.http.HttpServletRequest;

import com.erns.coching.login.domain.LoginDTO;
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
	public LoginUserDTO checkPasswd(String id, String userPw) {

		LoginUserDTO param = LoginUserDTO.CheckPasswdBuilder()
				.id(id)
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
		LoginDTO user = new LoginDTO();
		user.setId(userId);
		return dao.getUser(user);
	}

	@Override
	public LoginUserDTO loadUser(LoginDTO param) {
		return dao.getUser(param);
	}


	@Override
	public LoginUserDTO loadMembId(LoginDTO param) {
		return dao.loadMembId(param);
	}

	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		LoginDTO user = new LoginDTO();
		user.setId(userId);
		return dao.getUser(user);
	}
}
