package com.erns.coching.log.service;

import java.util.List;
import java.util.Map;

import com.erns.coching.common.type.RecentRefCode;
import com.erns.coching.log.domain.UserRecentInfoSearchDTO;

/**
 * <p>사용자 최근 정보 Service</p>
 */
public interface UserRecentInfoService {

  public List<Map<String, Object>> getList(UserRecentInfoSearchDTO param);
	public int getListCount(UserRecentInfoSearchDTO param);

	public int addLog(RecentRefCode refCode, long membSeq, String refSeq1);
	public int addLog(RecentRefCode refCode, long membSeq, String refSeq1, long refSeq2);
	public int addLog(RecentRefCode refCode, long membSeq, String refSeq1, long refSeq2, long refSeq3);
	
	public int deleteForUser(UserRecentInfoSearchDTO param);
}
