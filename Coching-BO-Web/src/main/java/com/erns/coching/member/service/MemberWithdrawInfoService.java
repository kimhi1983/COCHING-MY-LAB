package com.erns.coching.member.service;

import java.util.List;
import java.util.Map;

import com.erns.coching.member.domain.MemberSearchDTO;

/**
 * 
 * <p>회원 탈회 Service</p> 
 *
 * @author Hunwoo Park 
 *
 */
public interface MemberWithdrawInfoService {

    public Map<String, Object> getWithdrwalStatistics();
    public Map<String, Object> getWithdrwalDetailStatistics(MemberSearchDTO param);
    public List<Map<String, Object>> getWithdrawReason(MemberSearchDTO param);
    public List<Map<String, Object>> getWithdrawReasonText(MemberSearchDTO param);
    public int getWithdrawReasonTextCount(MemberSearchDTO param);
}
