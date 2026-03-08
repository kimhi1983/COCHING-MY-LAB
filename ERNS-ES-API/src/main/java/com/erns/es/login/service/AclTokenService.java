package com.erns.es.login.service;

import com.erns.es.login.domain.AclTokenVO;

/**
 *
 * <p>토큰 관리 Service</p>
 *
 * @author Hunwoo Park
 *
 */
public interface AclTokenService {

	public AclTokenVO getBlacklist(String token);
	public int addBlacklistToken(AclTokenVO param);
	public int deleteExpiredTokensInBlacklist();

	public AclTokenVO getRefresh(String token);
	public int addRefreshToken(AclTokenVO param);
	public int deleteRefreshToken(String token);
	public int deleteExpiredTokensInRefresh();

	public int updateNewRefreshToken(AclTokenVO newToken, String oldToken);

	public AclTokenVO getAccesslist(String token);
	public int addAccessToken(AclTokenVO param);
	public int deleteExpiredTokensInAccess();

	//계정 블럭처리
	public int setAllTokenBlock(long userSeq);

	//토큰 무효화 처리
	public int setBlackListAccessToken(String accessToken);
	public int setBlackListRefreshToken(String refreshToken);
	public int setBlackList(String accessToken, String refreshToken);

}
