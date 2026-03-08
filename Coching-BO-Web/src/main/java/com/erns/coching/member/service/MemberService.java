package com.erns.coching.member.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.erns.coching.member.domain.MemberSearchDTO;
import com.erns.coching.member.domain.MemberSetDTO;
import com.erns.coching.member.domain.MemberVO;

/**
 *
 * <p>회원기본정보 Service</p>
 *
 * @author Hunwoo Park
 *
 */
public interface MemberService {

	public List<Map<String, Object>> getList(MemberSearchDTO param);
	public int getListCount(MemberSearchDTO param);

	public int insert(MemberVO param);
	public int update(MemberVO param);
	public int updatePassword(MemberVO param);
	public int updateUseYn(MemberVO memberVO);

	public int getMembInfoListCount();
	public List<Map<String, Object>> getMembInfoList();

	public MemberVO loadMemb(MemberSearchDTO param);
	public Map<String, Object> load(MemberSearchDTO param);

	public Map<String, Object> chkDuplicate(MemberVO param);

	public int updateMember(MemberSetDTO setDTO);
	public Map<String, Object> updateMemberWithPswd(MemberSetDTO setDTO);

	public int updateMemberUseYn(MemberSetDTO setDTO);
	public int updateState(Collection<MemberVO> list);

	public int initPasswd(MemberSetDTO paramDto);

	//프로필 배치
	public List<MemberVO> selectProfileBatchList(int maxCnt);
	public void createProfileImg(int maxCnt);
}
