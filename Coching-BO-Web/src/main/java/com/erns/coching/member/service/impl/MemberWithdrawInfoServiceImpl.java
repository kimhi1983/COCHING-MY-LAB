package com.erns.coching.member.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.member.domain.MemberSearchDTO;
import com.erns.coching.member.mapper.MemberWithdrawInfoDAO;
import com.erns.coching.member.service.MemberWithdrawInfoService;

/**
 * 
 * <p>회원 탈회정보 Service</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Service
@Transactional
public class MemberWithdrawInfoServiceImpl implements MemberWithdrawInfoService{

    @Autowired
    private MemberWithdrawInfoDAO dao;

    @Override
    public Map<String, Object> getWithdrwalStatistics() {
        return dao.getWithdrwalStatistics();
    }

    @Override
    public Map<String, Object> getWithdrwalDetailStatistics(MemberSearchDTO param) {
        return dao.getWithdrwalDetailStatistics(param);
    }

    @Override
    public List<Map<String, Object>> getWithdrawReason(MemberSearchDTO param) {
        return dao.getWithdrawReason(param);
    }

    @Override
    public List<Map<String, Object>> getWithdrawReasonText(MemberSearchDTO param) {
        return dao.getWithdrawReasonText(param);
    }

    @Override
    public int getWithdrawReasonTextCount(MemberSearchDTO param) {
        return dao.getWithdrawReasonTextCount(param);
    }
}
