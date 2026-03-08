package com.erns.es.login.domain;

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * <p>토큰 저장 VO
 * T_TOKEN_ACCESS
 * T_TOKEN_BLACKLIST
 * T_TOKEN_REFRESH</p>
 *
 * @author Hunwoo Park
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
