package com.erns.coching.member.domain;

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * <p>관리자 정보 VO
 * t_memb_admin_inf</p>
 *
 * @author Hunwoo Park
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString(exclude = "password")
public class AdminMemberVO{

	protected long seq; 		//A_MEMB_SEQ

	protected String userId;	//A_MEMB_ID
	protected String userName;	//A_MEMB_NAME
	protected String password;	//A_MEMB_PSWD

	protected String salt;	//A_MEMB_SALT
	protected String phone;		//A_MEMB_PHONE
	protected String email;		//A_MEMB_EMAIL
	protected String useYn;		//USE_YN

	protected Date lastAccessDate; 	//A_RCT_ACS_DTTM
	protected String lastAccessIp;	//A_RCT_ACS_ID

	protected Date pwdChangeDate;	//PSWD_CHNG_DTTM
	protected int pwdErrCnt;		//PSWD_ERR_CNT
	protected String pwdInitYn; 	//PSWE_INIT_YN

	protected long rgtrSeq;		//RGTR
	protected Date rgtDate;		//RGT_DTTM
	protected long chnrSeq;		//CHNR
	protected Date chngDate;	//CHNG_DTTM

}
