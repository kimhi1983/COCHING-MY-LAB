package com.erns.coching.member.mapper;

import java.util.List;
import java.util.Map;

import com.erns.coching.member.domain.MemberSetDTO;
import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.member.domain.MemberSearchDTO;
import com.erns.coching.member.domain.MemberTermsAgreementHistoryVO;
import com.erns.coching.member.domain.MemberVO;

/**
 * <p>회원 Mapper</p>
 *
 * @author Kyungmin Lee
 *
 */
@Mapper
public interface MemberDAO {

	public List<Map<String, Object>> getList(MemberSearchDTO param);
	public int getListCount(MemberSearchDTO param);
	public MemberVO load(MemberVO param);

	public int insert(MemberVO param);

	public int update(MemberVO param);
	public int updateUseYn(MemberSetDTO param);
	public int updatePassword(MemberVO param);

	public int delete(long membSeq);

	public Map<String, Object> loadMembInfo(long membSeq);

	public int updateWithdrawYn(MemberVO param);

//	public MemberVO loadPassword(long membSeq);

	public int insertAgreementHistory(MemberTermsAgreementHistoryVO param);

	public Map<String, Object> chkDuplicate(MemberVO param);

	public int updatePwChngDttm(MemberSetDTO param);

}
