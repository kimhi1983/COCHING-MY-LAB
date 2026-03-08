package com.erns.coching.login.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString()
public class TempSnsInfoVO {

	private String siKey;		//SI_KEY : SNS 인증정보 키
	private String snsType;		//SNS_TYPE : SNS TYPE (네이버, 카카오, 페이스북)
	private String snsId;		//SNS_ID : SNS 자체 ID
	private String snsJson;		//SNS_JSON : SNS 인증정보 JSON
	private Date regDate;		//RGT_DTTM

}
