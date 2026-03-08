package com.erns.coching.log.domain;

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <p>사용자 최근 정보 VO
 * T_RECENT_INF</p>
 * 
 * @author Hunwoo Park
 * 
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@ToString
@Builder
public class UserRecentInfoVO {
  
  private long membSeq;   //사용자 SEQ
  private String refCode; //참조 CD
  private String refSeq1; //참조 SEQ1
  private long refSeq2;   //참조 SEQ2  
  private long refSeq3;   //참조 SEQ3
  private Date rgtDttm;		//등록일
  
}
