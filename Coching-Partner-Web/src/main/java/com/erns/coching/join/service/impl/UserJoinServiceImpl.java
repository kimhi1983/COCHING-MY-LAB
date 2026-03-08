package com.erns.coching.join.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.service.AbstractCochingApiService;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.join.domain.UserJoinDTO;
import com.erns.coching.join.service.UserJoinService;
import com.erns.coching.mail.service.MailService;
import com.erns.coching.member.domain.MemberVO;
import com.erns.coching.member.service.MemberService;
import com.erns.coching.partner.service.PartnerService;
import com.erns.coching.url.service.ShortUrlService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class UserJoinServiceImpl extends AbstractCochingApiService implements UserJoinService{

	@Autowired
	private MemberService memberService;

	@Autowired
	private PartnerService partnerService;

	@Autowired
	private ShortUrlService shortUrlService;

	@Autowired
	private MailService mailService;

	@Override
	public ApiResult addUser(UserJoinDTO joinDto) {
		ApiResult axRet = new ApiResult();
		log.debug("[PATNER ACCOUNT CREATE INFO] {}", joinDto.toString());

		//아이디 중복 확인
		MemberVO param = MemberVO
				.MemberDuplicateBuilder()
				.membId(joinDto.getMembId())
				.nickname(joinDto.getNickname())
				.build();
		Map<String, Object> checkUserMap = memberService.chkDuplicate(param);
		if(null == checkUserMap) {

			MemberVO memberVO = MemberVO
					.MemberJoinBuilder()
					.dto(joinDto)
					.build();

			if(memberService.insert(memberVO) <= 0){
				//가입 실패
				return axRet.set(ApiResultError.JOIN_ERR, getLocale());
			}

			//TODO 영업사원 약관 처리
//			String[] termsCodes = joinDto.getTermsCd().split(",");
//			String[] agreeYn = joinDto.getAgreeYn().split(",");
//			for(int i=0; i<termsCodes.length; i++){
//				MemberTermsAgreementHistoryVO termsVO = MemberTermsAgreementHistoryVO.
//						SetMemberTermsBuilder()
//						.membSeq(memberVO.getMembSeq())
//						.termsCd(termsCodes[i])
//						.agreeYn(agreeYn[i])
//						.build();
//
//				memberService.insertAgreementHistory(termsVO);
//			}

		}else{
			//가입 실패
			return axRet.set(ApiResultError.JOIN_ERR_ID_OVERLAP, getLocale());
		}

		axRet.set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}
}
