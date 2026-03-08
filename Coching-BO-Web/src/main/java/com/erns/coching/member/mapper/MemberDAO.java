package com.erns.coching.member.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.member.domain.MemberSearchDTO;
import com.erns.coching.member.domain.MemberVO;

/**
 *
 * <p>회원 기본정보 Mapper</p>
 *
 * @author Kyungmin Lee
 *
 */
@Mapper
public interface MemberDAO {


	public List<Map<String, Object>> getMembList(MemberSearchDTO param);
	public int getMembListCount(MemberSearchDTO param);
	public MemberVO loadMemb(MemberSearchDTO param);
	public Map<String, Object> load(MemberSearchDTO param);

	public int insertMemb(MemberVO param);
	public int update(MemberVO param);
	public int updatePassword(MemberVO param);
	public int updateUseYn(MemberVO param);
	public int updateState(MemberVO param);

	public List<Map<String, Object>> getMembInfoList();
	public int getMembInfoListCount();

	public Map<String, Object> chkDuplicate(MemberVO param);

	public List<MemberVO> selectProfileBatchList(int maxCnt);
}
