package com.erns.coching.login.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>Token 검색 DTO</p>
 *
 * @author heejinyu@erns.co.kr
 *
 */
@Getter
@Setter
@ToString
public class AclTokenSearchDTO {

	protected String token;
	protected long userSeq;
	protected String siteType;
	protected String deviceKey;

	protected boolean isExpired;

}
