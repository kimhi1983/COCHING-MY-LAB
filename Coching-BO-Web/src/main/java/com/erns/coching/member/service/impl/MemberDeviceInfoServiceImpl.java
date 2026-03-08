package com.erns.coching.member.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.erns.coching.member.domain.MemberDeviceInfoSearchDTO;
import com.erns.coching.member.domain.MemberDeviceInfoVO;
import com.erns.coching.member.mapper.MemberDeviceInfoDAO;
import com.erns.coching.member.service.MemberDeviceInfoService;

/**
 *
 * <p>회원 Device 정보 Service</p>
 *
 * @author Hunwoo Park
 *
 */
@Service
@Transactional
public class MemberDeviceInfoServiceImpl implements MemberDeviceInfoService {

	@Autowired
	private MemberDeviceInfoDAO dao;

	@Override
	public List<MemberDeviceInfoVO> getList(MemberDeviceInfoSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(MemberDeviceInfoSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public MemberDeviceInfoVO load(String appId) {
		return dao.load(appId);
	}

	@Override
	public int delete(String appId) {
		return dao.delete(appId);
	}

	@Override
	public int setUserDevice(MemberDeviceInfoVO param) {
		if(!StringUtils.hasText(param.getAppId())) {
			return 0;
		}

		long userSeq = param.getMembSeq();
		if(userSeq <= 0) {
			return 0;
		}

		int ret = 0;
		MemberDeviceInfoVO old = dao.load(param.getAppId());
		if(old != null) {
			if(!(userSeq == old.getMembSeq())) {
				ret = dao.update(param);
			}else {
				//Do not update
			}
		}else {
			ret = dao.insert(param);
		}

		return ret;
	}

	@Override
	public List<MemberDeviceInfoVO> getUserList(long membSeq) {
		MemberDeviceInfoSearchDTO param = MemberDeviceInfoSearchDTO.builder()
				.membSeq(membSeq)
				.build();
		return this.getList(param);
	}
}
