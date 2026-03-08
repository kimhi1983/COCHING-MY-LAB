package com.erns.coching.login.domain;

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <p>Token DTO</p>
 *
 * @author heejinyu@erns.co.kr
 *
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@ToString
@Builder
public class AclTokenVO {

	protected String token;
	protected String siteType;
	protected String deviceKey;
	protected long userSeq;
	protected Date expDate;
	protected Date regDttm;

}
