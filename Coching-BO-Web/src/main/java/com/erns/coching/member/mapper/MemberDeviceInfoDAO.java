package com.erns.coching.member.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.member.domain.MemberDeviceInfoSearchDTO;
import com.erns.coching.member.domain.MemberDeviceInfoVO;

/**
 *
 * <p>회원 Device 정보 Mapper</p>
 *
 * @author Hunwoo Park
 *
 */
@Mapper
public interface MemberDeviceInfoDAO {

	public List<MemberDeviceInfoVO> getList(MemberDeviceInfoSearchDTO param);
	public int getListCount(MemberDeviceInfoSearchDTO param);

	public MemberDeviceInfoVO load(String appId);

	public int insert(MemberDeviceInfoVO param);
	public int update(MemberDeviceInfoVO param);

	public int delete(String appId);
	public int deleteWhere(MemberDeviceInfoSearchDTO param);

}
