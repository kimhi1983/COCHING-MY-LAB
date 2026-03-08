package com.erns.coching.log.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.common.type.RecentRefCode;
import com.erns.coching.log.domain.UserRecentInfoSearchDTO;
import com.erns.coching.log.domain.UserRecentInfoVO;
import com.erns.coching.log.mapper.UserRecentInfoDAO;
import com.erns.coching.log.service.UserRecentInfoService;

/**
 *
 * <p>사용자 최근 정보 Service</p>
 *
 * @author Kyungmin Lee
 *
 */
@Transactional
@Service
public class UserRecentInfoServiceImpl implements UserRecentInfoService {

  @Autowired
  private UserRecentInfoDAO dao;

  public List<Map<String, Object>> getList(UserRecentInfoSearchDTO param) {
    return dao.getList(param);
  }

  public int getListCount(UserRecentInfoSearchDTO param) {
    return dao.getListCount(param);
  }

  @Override
  public int addLog(RecentRefCode refCode, long membSeq, String refSeq1) {
    return addLog(refCode, membSeq, refSeq1, 0, 0);
  }

  @Override
  public int addLog(RecentRefCode refCode, long membSeq, String refSeq1, long refSeq2) {
    return addLog(refCode, membSeq, refSeq1, refSeq2 , 0);
  }

  @Override
  public int addLog(RecentRefCode refCode, long membSeq, String refSeq1, long refSeq2, long refSeq3) {
    UserRecentInfoVO newLog = UserRecentInfoVO.builder()
        .refCode(refCode.getCode())
        .membSeq(membSeq)
        .refSeq1(refSeq1)
        .refSeq2(refSeq2)
        .refSeq3(refSeq3)
        .build();

    return dao.insert(newLog);
  }

  public int deleteForUser(UserRecentInfoSearchDTO param) {
    List<UserRecentInfoVO> delList = dao.getListVo(param);

    int delCnt = 0;
    long actMembSeq = param.getMembSeq();

    for (UserRecentInfoVO vo : delList) {
      if (actMembSeq == vo.getMembSeq()) {
        delCnt += dao.delete(vo);        
      }
    }

    return delCnt;
  }

  
  
}
