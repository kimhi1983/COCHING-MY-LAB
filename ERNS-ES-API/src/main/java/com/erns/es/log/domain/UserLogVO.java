package com.erns.es.log.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * <p>사용자로그 VO
 * T_MEMB_LOG_H</p>
 *
 * @author Hunwoo Park
 *
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@ToString
@Builder
public class UserLogVO {

	private String logDate; //YYYY-MM-DD HH:mm:ss.SSS (YYYY-MM-DD HH24:MI:SS.MS)
	private long userSeq;
	private String logType;
	private String siteType;
	private String userId;
	private String userIp;
	private String logJson;

}
