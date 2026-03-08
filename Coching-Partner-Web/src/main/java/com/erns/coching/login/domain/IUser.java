package com.erns.coching.login.domain;

/**
 * <p>IUser</p>
 *
 * @author heejinyu@erns.co.kr
 *
 */
public interface IUser {

	public long getSeq();

	public long getPtnSeq();

	public String getId();

	public String getUserType();

	public String getUserName();

}
