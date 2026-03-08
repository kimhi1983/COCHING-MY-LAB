package com.erns.es.login.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.erns.es.login.domain.AclTokenVO;
import com.erns.es.login.mapper.AccessTokenDAO;
import com.erns.es.login.mapper.BlacklistTokenDAO;
import com.erns.es.login.mapper.RefreshTokenDAO;
import com.erns.es.login.service.AclTokenService;

/**
 * 
 * <p>토큰 관리 Service</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Service
@Transactional
public class AclTokenServiceImpl implements AclTokenService{
	
	@Autowired
	private BlacklistTokenDAO blacklistTokenDao;
	
	@Autowired
	private RefreshTokenDAO refreshTokenDao;
	
	@Autowired
	private AccessTokenDAO accessTokenDao;
	
	@Override
	public AclTokenVO getBlacklist(String token) {
		return blacklistTokenDao.load(token);
	}

	@Override
	public int addBlacklistToken(AclTokenVO param) {
		AclTokenVO oldToken = blacklistTokenDao.load(param.getToken());
		if(null == oldToken) {
			return blacklistTokenDao.insert(param);			
		}
		
		return 0;
	}

	@Override
	public int deleteExpiredTokensInBlacklist() {
		return blacklistTokenDao.deleteExpiredTokens();
	}
	
	@Override
	public int setAllTokenBlock(long userSeq) {
		if(userSeq <= 0) {
			return 0;
		}
		
		int ret = blacklistTokenDao.insertFromAllRefreshByUserSeq(userSeq);
		ret += blacklistTokenDao.insertFromAllAccessByUserSeq(userSeq);
		return ret;
	}

	@Override
	public AclTokenVO getRefresh(String token) {
		return refreshTokenDao.load(token);
	}

	@Override
	public int addRefreshToken(AclTokenVO param) {
		deleteExpiredTokensInRefresh();
		
		AclTokenVO oldToken = refreshTokenDao.load(param.getToken());
		if(null != oldToken) {
			refreshTokenDao.delete(param.getToken());
		}
		
		return refreshTokenDao.insert(param);
	}

	@Override
	public int deleteRefreshToken(String token) {
		return refreshTokenDao.delete(token);
	}

	@Override
	public int deleteExpiredTokensInRefresh() {
		return refreshTokenDao.deleteExpiredTokens();
	}

	@Override
	public int updateNewRefreshToken(AclTokenVO newToken, String oldToken) {
		//Step1 : 기존 토큰을 blacklist 처리
		blacklistTokenDao.insertFromRefresh(oldToken);
		
		//Step2 : 기존 토큰을 refresh 에서 삭제
		refreshTokenDao.delete(oldToken);
		
		//Step3 : 신규 토큰을 refresh 에 추가
		AclTokenVO oldCheckToken = refreshTokenDao.load(newToken.getToken());
		if(null == oldCheckToken) {
			refreshTokenDao.insert(newToken);
		}
		
		return 1;
	}

	@Override
	public AclTokenVO getAccesslist(String token) {
		return accessTokenDao.load(token);
	}

	@Override
	public int addAccessToken(AclTokenVO param) {
		deleteExpiredTokensInAccess();
		
		AclTokenVO oldToken = accessTokenDao.load(param.getToken());
		if(null == oldToken) {
			return accessTokenDao.insert(param);			
		}
		
		return 0;
	}

	@Override
	public int deleteExpiredTokensInAccess() {
		return accessTokenDao.deleteExpiredTokens();
	}

	@Override
	public int setBlackListAccessToken(String accessToken) {
		deleteExpiredTokensInBlacklist();
		
		if(!StringUtils.hasText(accessToken)) {
			return 0;
		}
		
		return blacklistTokenDao.insertFromAccess(accessToken);
	}

	@Override
	public int setBlackListRefreshToken(String refreshToken) {
		deleteExpiredTokensInBlacklist();
		
		if(!StringUtils.hasText(refreshToken)) {
			return 0;
		}
		
		return blacklistTokenDao.insertFromRefresh(refreshToken);
	}

	@Override
	public int setBlackList(String accessToken, String refreshToken) {
		int aRet = setBlackListAccessToken(accessToken);
		int rRet = setBlackListRefreshToken(refreshToken);
		
		return aRet+rRet;
	}
	
}
