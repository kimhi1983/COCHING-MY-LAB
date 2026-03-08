package com.erns.coching.member.service.impl;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.common.file.repository.FileRepository;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.service.AbstractCochingApiService;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.join.domain.UserJoinDTO;
import com.erns.coching.member.domain.MemberSearchDTO;
import com.erns.coching.member.domain.MemberSetDTO;
import com.erns.coching.member.domain.MemberTermsAgreementHistoryVO;
import com.erns.coching.member.domain.MemberVO;
import com.erns.coching.member.domain.MemberWithdrawInfoVO;
import com.erns.coching.member.mapper.MemberDAO;
import com.erns.coching.member.mapper.MemberWithdrawInfoDAO;
import com.erns.coching.member.service.MemberService;
import com.erns.coching.partner.domain.PartnerSearchDTO;
import com.erns.coching.partner.service.PartnerService;

import lombok.extern.slf4j.Slf4j;


/**
*
* <p>회원 Service</p>
*
* @author Kyungmin Lee
*
*/
@Slf4j
@Transactional
@Service
public class MemberServiceImpl extends AbstractCochingApiService implements MemberService {

	@Autowired
	private MemberDAO dao;

	@Autowired
	private MemberWithdrawInfoDAO withdrawInfoDao;

	@Autowired
	private PartnerService partnerService;

	@Autowired
	private FileRepository fileRepo;

	@Override
	public List<Map<String, Object>> getList(MemberSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(MemberSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public MemberVO load(MemberVO param) {
		return dao.load(param);
	}

	@Override
	public int insert(MemberVO param) {
		return dao.insert(param);
	}

	@Override
	public int update(MemberVO param) {
		return dao.update(param);
	}

	@Override
	public int updateUseYn(MemberSetDTO param) {
		return dao.updateUseYn(param);
	}

	@Override
	public int updatePassword(MemberVO param) {
		return dao.updatePassword(param);
	}
//
//	@Override
//	public int updateWithPassword(MemberVO param) {
//		dao.update(param);
//		return dao.updatePassword(param);
//	}

	@Override
	public int delete(long membSeq) {
		return dao.delete(membSeq);
	}

	@Override
	public Map<String, Object> loadMembInfo(long membSeq) {
		return dao.loadMembInfo(membSeq);
	}

	@Override
	public ApiResult userUpdate(UserJoinDTO joinDto){
		ApiResult axRet = new ApiResult();

		log.debug("[PARTNER ACCOUNT UPDATE INFO] {}", joinDto.toString());

		PartnerSearchDTO search = new PartnerSearchDTO();
		search.setPtnSeq(joinDto.getPtnSeq());
		Map<String, Object> partnerMap = partnerService.load(search);

		//기업확인
		if(null == partnerMap) {
			return axRet.set(ApiResultError.ERROR_REG_COMP_NO, getLocale());
		}

		MemberVO userSearch = MemberVO
				.MemberSearchBuilder()
				.dto(joinDto)
				.ptnSeq(joinDto.getPtnSeq())
				.build();
		MemberVO oldMembInfo = load(userSearch);

		if(null == oldMembInfo) {
			//미등록 회원입니다.
			return axRet.set(ApiResultError.ERROR_REG_USER_NO, getLocale());
		}

		MemberVO memberVO = MemberVO
				.MemberJoinBuilder()
				.dto(joinDto)
				.build();

		if(update(memberVO) <= 0){
			//수정 실패
			return axRet.set(ApiResultError.ERROR_DEFAULT, getLocale());
		}

		axRet.set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}


	@Override
	public int withdraw(MemberWithdrawInfoVO param) {
		// 1.탈회정보 입력
		int retVal = withdrawInfoDao.insert(param);

		// 3.사용자 프로필에 탈회정보 기록
		MemberVO withdrawParam = MemberVO.MemberWithdrawBuilder()
				.seq(param.getMembSeq())
				.wtdrYn("Y") //탈회
				.build();
		dao.updateWithdrawYn(withdrawParam);

		//TODO : 탈회 처리


		return retVal;
	}

//	@Override
//	public MemberVO loadPassword(long membSeq) {
//		return dao.loadPassword(membSeq);
//	}

	@Override
	public int insertAgreementHistory(MemberTermsAgreementHistoryVO param){
		return dao.insertAgreementHistory(param);
	}

	@Override
	public Map<String, Object> chkDuplicate(MemberVO param){
		return dao.chkDuplicate(param);
	}

	@Override
	public int updatePwChngDttm(MemberSetDTO param) {
		return dao.updatePwChngDttm(param);
	}

//	@Override
//	public int updateWithdrawYn(MemberVO param) {
//		return dao.updateWithdrawYn(param);
//	}

//	@Override
//	public int updateDormantYn(MemberVO param) {
//		return dao.updateDormantYn(param);
//	}

	@Override
	public int initPasswd(MemberSetDTO paramDto){

		String initPswd = "123456";

		MemberVO memberVO = MemberVO
				.MemberUpdatePswdBuilder()
				.membSeq(paramDto.getMembSeq())
				.pswd(initPswd)
				.pswdInitYn("Y")
				.seq(paramDto.getChnr())
				.build();

		int result =  updatePassword(memberVO);

		return result;
	}

	@Override
	public int changePasswd(MemberSetDTO paramDto){
		MemberVO memberVO = MemberVO
				.MemberUpdatePswdBuilder()
				.membSeq(paramDto.getMembSeq())
				.pswd(paramDto.getPswd())
				.pswdInitYn("N")
				.build();

		int result = updatePassword(memberVO);

		return result;
	}
}
