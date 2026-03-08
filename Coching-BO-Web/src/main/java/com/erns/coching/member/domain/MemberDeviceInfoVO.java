package com.erns.coching.member.domain;

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * <p>회원 Device 정보 VO
 * T_MEMB_DEVICE_INF</p>
 *
 * @author Hunwoo Park
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString()
public class MemberDeviceInfoVO {

	private String appId; 		//APP_ID	APP ID
	private Long membSeq; 		//MEMB_SEQ	사용자 SEQ
	private String deviceType; 	//DEV_TYPE	디바이스종류
	private Date chngDttm;		//CHNG_DTTM	수정일
}
