package com.erns.coching.member.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.member.domain.MemberWithdrawInfoVO;

/**
 * <p>회원 Mapper</p>
 *
 * @author Kyungmin Lee
 *
 */
@Mapper
public interface MemberWithdrawInfoDAO {

	public List<Map<String, Object>> getList();
	public MemberWithdrawInfoVO load(long seq);
	public MemberWithdrawInfoVO loadByMembSeq(long membSeq);
	public int insert(MemberWithdrawInfoVO param);
}
