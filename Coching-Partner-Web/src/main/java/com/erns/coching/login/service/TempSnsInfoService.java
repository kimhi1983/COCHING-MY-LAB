package com.erns.coching.login.service;

import com.erns.coching.login.domain.TempSnsInfoVO;

/**
 *
 * <p>SnsInfo Service</p>
 *
 * @author Hunwoo Park
 *
 */
public interface TempSnsInfoService {

	public TempSnsInfoVO load(String key);
	public int insert(TempSnsInfoVO param);
	public int delete(String key);

	public int deleteExpired();
}
