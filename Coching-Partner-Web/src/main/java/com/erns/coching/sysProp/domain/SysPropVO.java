package com.erns.coching.sysProp.domain;

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <p>시스템설정 SysPropVO</p>
 * 
 * @author 	jsjeong@erns.co.kr
 *
 */
@Getter	
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class SysPropVO {
										 
	private String sysKey;
	private String sysValue;
	
	private long rgtr;                  //RGTR      
	private Date rgtDttm;               //RGT_DTTM          
	private long chnr;                  //CHNR     
	private Date chngDttm;              //CHNG_DTTM
}
