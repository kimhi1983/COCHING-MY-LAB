package com.erns.coching.member.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.erns.coching.member.domain.MemberSearchDTO;
import com.erns.coching.member.domain.MemberSetDTO;
import com.erns.coching.member.domain.MemberTermsAgreementHistoryVO;
import com.erns.coching.member.domain.MemberVO;
import com.erns.coching.member.domain.MemberWithdrawInfoVO;
import org.springframework.web.multipart.MultipartFile;

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

//	public List<Map<String, Object>> loadContractMemb(MemberSearchDTO param);

	public int insert(MemberVO param);

	public int update(MemberVO param);

	public int updatePassword(MemberVO param);

	public int delete(long membSeq);

	public Map<String, Object> loadMyInfo(long membSeq);
//
//	public ApiResult userUpdate(UserJoinDTO joinDto);
//
//	public ApiResult insteadUserUpdate(UserJoinDTO joinDto);
//
//	public int updateEmail(MemberVO param);
//	//public int updateWithdrawYn(MemberVO param);
//	//public int updateDormantYn(MemberVO param);
//
//	//사용자 탈회
	public int withdraw(MemberWithdrawInfoVO param);

	public MemberVO loadPassword(MemberSearchDTO searchDTO);

	public int insertAgreementHistory(MemberTermsAgreementHistoryVO param);

	public int updatePwChngDttm(MemberSetDTO param);

	public Map<String, Object> chkDuplicate(MemberVO param);

	public List<Map<String, Object>> procAttachFiles(MemberSetDTO paramDto, List<MultipartFile> uploadFiles, String strDelFileIds, boolean isPreview) throws IllegalStateException, IOException;

	public MultipartFile createAvatarMultipartFile(String name, int width, int height, String fileName) throws Exception;
}
