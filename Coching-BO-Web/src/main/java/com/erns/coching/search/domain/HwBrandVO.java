package com.erns.coching.search.domain;

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 
 * <p>화해 브랜드
 * T_HW_BRAND
 * </p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class HwBrandVO {

  private long seq;
  private String brandFullName;
  private String brandKo;
  private String brandEn;
  private int count;
  private Date rgtDttm;  
}
