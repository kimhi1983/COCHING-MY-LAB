package com.erns.coching.member.service;

import java.util.List;
import java.util.Map;

import com.erns.coching.common.model.ApiResult;
import com.erns.coching.join.domain.UserJoinDTO;
import com.erns.coching.member.domain.MemberSearchDTO;
import com.erns.coching.member.domain.MemberSetDTO;
import com.erns.coching.member.domain.MemberTermsAgreementHistoryVO;
import com.erns.coching.member.domain.MemberVO;
import com.erns.coching.member.domain.MemberWithdrawInfoVO;

/**
 * <p>회원 Service</p>
 *
 * @author Kyungmin Lee
 *
 */
public interface MemberService {

	public List<Map<String, Object>> getList(MemberSearchDTO param);

	public int getListCount(MemberSearchDTO param);

	public MemberVO load(MemberVO param);

	public int insert(MemberVO param);

	public int update(MemberVO param);

	public int updateUseYn(MemberSetDTO param);

//
	public int updatePassword(MemberVO param);

	public Map<String, Object> chkDuplicate(MemberVO param);

	public int updatePwChngDttm(MemberSetDTO param);
//
//	public int updateWithPassword(MemberVO param);

	public int delete(long membSeq);

	public Map<String, Object> loadMembInfo(long membSeq);

	public ApiResult userUpdate(UserJoinDTO joinDto);

	//public int updateWithdrawYn(MemberVO param);
	//public int updateDormantYn(MemberVO param);

//	//사용자 탈회
	public int withdraw(MemberWithdrawInfoVO param);
	public int insertAgreementHistory(MemberTermsAgreementHistoryVO param);

	public int initPasswd(MemberSetDTO paramDto);

	public int changePasswd(MemberSetDTO paramDto);
}
