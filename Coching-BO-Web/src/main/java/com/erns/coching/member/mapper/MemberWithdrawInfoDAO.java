package com.erns.coching.member.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.member.domain.MemberSearchDTO;

/**
 *
 * <p>회원 탈회 정보 Mapper</p>
 *
 * @author Hunwoo Park
 *
 */
@Mapper
public interface MemberWithdrawInfoDAO {

    public Map<String, Object> getWithdrwalStatistics();
    public Map<String, Object> getWithdrwalDetailStatistics(MemberSearchDTO param);
    public List<Map<String, Object>> getWithdrawReason(MemberSearchDTO param);
    public List<Map<String, Object>> getWithdrawReasonText(MemberSearchDTO param);
    public int getWithdrawReasonTextCount(MemberSearchDTO param);
}
