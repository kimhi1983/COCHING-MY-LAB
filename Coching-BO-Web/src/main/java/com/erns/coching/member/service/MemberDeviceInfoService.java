package com.erns.coching.member.service;

import java.util.List;

import com.erns.coching.member.domain.MemberDeviceInfoSearchDTO;
import com.erns.coching.member.domain.MemberDeviceInfoVO;

/**
 *
 * <p>회원의 device 정보 Service</p>
 *
 * @author Hunwoo Park
 *
 */
public interface MemberDeviceInfoService {

	public List<MemberDeviceInfoVO> getList(MemberDeviceInfoSearchDTO param);
	public int getListCount(MemberDeviceInfoSearchDTO param);

	public MemberDeviceInfoVO load(String appId);
	public int delete(String appId);

	public int setUserDevice(MemberDeviceInfoVO param);
	public List<MemberDeviceInfoVO> getUserList(long membSeq);

}
