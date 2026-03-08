package com.erns.coching.login.domain;

/**
 *
 * <p>사용자 구분용 Interface</p>
 *
 * @author Hunwoo Park
 *
 */
public interface IUser {

	public long getSeq();

	public long getPtnSeq();

	public String getUserId();

	public String getUserType();

	public String getUserName();


}
