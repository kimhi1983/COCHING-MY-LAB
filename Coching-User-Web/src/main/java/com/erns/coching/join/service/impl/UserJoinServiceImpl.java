package com.erns.coching.join.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.erns.coching.member.domain.MemberSetDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.service.AbstractCochingApiService;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.join.domain.UserJoinDTO;
import com.erns.coching.join.service.UserJoinService;
import com.erns.coching.member.domain.MemberSearchDTO;
import com.erns.coching.member.domain.MemberTermsAgreementHistoryVO;
import com.erns.coching.member.domain.MemberVO;
import com.erns.coching.member.service.MemberService;
import com.erns.coching.partner.domain.PartnerSearchDTO;
import com.erns.coching.partner.domain.PartnerVO;
import com.erns.coching.partner.service.PartnerService;
import com.erns.coching.url.service.ShortUrlService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

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


	@Override
	public ApiResult addUser(UserJoinDTO joinDto) {
		ApiResult axRet = new ApiResult();
		log.debug("[USER JOIN INFO] {}", joinDto.toString());

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

			//프로필 생성
			try {
				String fileName = "profile_"+ memberVO.getMembSeq() + ".png";
				MultipartFile avatarFile = memberService.createAvatarMultipartFile(joinDto.getMembName(), 100, 100, fileName);

				List<MultipartFile> fileList = new ArrayList<>();
				fileList.add(avatarFile);
				MemberSetDTO memberSetDTO = new MemberSetDTO();
				memberSetDTO.setMembSeq(memberVO.getMembSeq());
				memberSetDTO.setFileType("PROFILE");

				memberService.procAttachFiles(memberSetDTO, fileList,null, false);

			}catch(Exception e){
				e.printStackTrace();
			}

			String[] termsCodes = joinDto.getTermsCd().split(",");
			String[] agreeYn = joinDto.getAgreeYn().split(",");
			for(int i=0; i<termsCodes.length; i++){
				MemberTermsAgreementHistoryVO termsVO = MemberTermsAgreementHistoryVO.
						SetMemberTermsBuilder()
						.membSeq(memberVO.getMembSeq())
						.termsCd(termsCodes[i])
						.agreeYn(agreeYn[i])
						.build();

				memberService.insertAgreementHistory(termsVO);
			}

		}else{
			//가입 실패
			return axRet.set(ApiResultError.JOIN_ERR_ID_OVERLAP, getLocale());
		}

		axRet.set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	@Override
	public ApiResult addPartner(UserJoinDTO joinDto) {
		ApiResult axRet = new ApiResult();

		log.debug("[PARTNER JOIN INFO] {}", joinDto.toString());

		//아이디 중복 확인
		MemberVO param = MemberVO
				.MemberDuplicateBuilder()
				.membId(joinDto.getMembId())
				.nickname(joinDto.getNickname())
				.build();
		Map<String, Object> checkUserMap = memberService.chkDuplicate(param);
		if(null != checkUserMap){
			//가입 실패
			return axRet.set(ApiResultError.JOIN_ERR_ID_OVERLAP, getLocale());
		}

		//기업중복 확인(국내원료사만)
		PartnerSearchDTO search = new PartnerSearchDTO();
		search.setPtnLic(joinDto.getPtnLic());
		Map<String, Object> partnerMap = null;

		if("001".equals(joinDto.getNation())){
			partnerMap = partnerService.load(search);
		}

		if(null == partnerMap) {

			long seq = partnerService.loadNextSeq();

			MemberVO memberVO = MemberVO
					.MemberJoinBuilder()
					.dto(joinDto)
					.ptnSeq(seq)
					.build();

			if(memberService.insert(memberVO) <= 0){
				//가입 실패
				return axRet.set(ApiResultError.JOIN_ERR, getLocale());
			}
			PartnerVO partnerVO = PartnerVO
					.AddPtnBuilder()
					.ptnSeq(seq)
					.rgtr(memberVO.getMembSeq())
					.fromDto(joinDto)
					.build();

			if(partnerService.insert(partnerVO) <= 0){
				return axRet.set(ApiResultError.JOIN_ERR, getLocale());
			}

			//프로필 생성
			try {
				String fileName = "profile_"+ memberVO.getMembSeq() + ".png";
				//파트너사 관리자는 파트너사명으로 생성 - ("㈜", "주식회사", "Inc.", "Ltd" 등)를 제거
				Pattern COMPANY_PATTERN = Pattern.compile(
						"(^|\\s|\\()?(주식회사|주|유|사)(\\s|\\)|$)?|㈜|㈔|(\\((주|유|사)\\))|((주|유|사)\\)$)|\\b(ltd|inc|co\\.?|corp|llc|gmbh)\\b",
						Pattern.CASE_INSENSITIVE
				);
				String profileName = COMPANY_PATTERN.matcher(partnerVO.getPtnName()).replaceAll("");
				profileName = profileName.replaceAll("\\s{2,}", " ").trim();

				MultipartFile avatarFile = memberService.createAvatarMultipartFile(profileName, 100, 100, fileName);

				List<MultipartFile> fileList = new ArrayList<>();
				fileList.add(avatarFile);
				MemberSetDTO memberSetDTO = new MemberSetDTO();
				memberSetDTO.setMembSeq(memberVO.getMembSeq());
				memberSetDTO.setFileType("PROFILE");

				memberService.procAttachFiles(memberSetDTO, fileList,null, false);

			}catch(Exception e){
				e.printStackTrace();
			}

			String[] termsCodes = joinDto.getTermsCd().split(",");
			String[] agreeYn = joinDto.getAgreeYn().split(",");
			for(int i=0; i<termsCodes.length; i++){
				MemberTermsAgreementHistoryVO termsVO = MemberTermsAgreementHistoryVO.
						SetMemberTermsBuilder()
						.membSeq(memberVO.getMembSeq())
						.termsCd(termsCodes[i])
						.agreeYn(agreeYn[i])
						.build();

				memberService.insertAgreementHistory(termsVO);
			}
		}else{
			//사업자 번호 중복 가입 실패
			return axRet.set(ApiResultError.ERROR_REG_COMP_OVERLAP, getLocale());
		}

		axRet.set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	@Override
	public ApiResult updateMemb(UserJoinDTO joinDto){
		ApiResult axRet = new ApiResult();

		log.debug("[USER UPDATE INFO] {}", joinDto.toString());

		MemberVO memberVO = MemberVO
				.UpdateMemberBuilder()
				.dto(joinDto)
				.build();
		if(memberService.update(memberVO) <= 0){
			//수정 실패
			return axRet.set(ApiResultError.ERROR_DEFAULT, getLocale());
		}

		//비밀번호 변경
		if(!StringUtils.isBlank(joinDto.getCurrentPswd()) && !StringUtils.isBlank(joinDto.getPswd())){
			//현재 비밀번호 확인
			MemberSearchDTO searchDTO = new MemberSearchDTO();
			searchDTO.setMembSeq(joinDto.getMembSeq());
			searchDTO.setPswd(joinDto.getCurrentPswd());
			MemberVO user = memberService.loadPassword(searchDTO);

			if(null == user){
				return axRet.set(ApiResultError.JOIN_ERR_NOT_MATCH_PASSWD, getLocale());
			}
			//비밀번호 포함 업데이트
			if(memberService.updatePassword(memberVO) <= 0){
				//수정 실패
				return axRet.set(ApiResultError.ERROR_DEFAULT, getLocale());
			}
		}
		axRet.set(ApiResultError.NO_ERROR, getLocale());
		return axRet;
	}

	@Override
	public ApiResult updatePartner(UserJoinDTO joinDto){
		ApiResult axRet = new ApiResult();

		log.debug("[PARTNER UPDATE INFO] {}", joinDto.toString());

		//기업중복 확인
		PartnerSearchDTO search = new PartnerSearchDTO();
		search.setPtnLic(joinDto.getPtnLic());
		search.setPtnSeq(joinDto.getPtnSeq());
		search.setIsExclude("EXCLUDE");
		List<Map<String, Object>> partnerList = partnerService.getList(search);

		if(partnerList.size() == 0) {

			MemberVO memberVO = MemberVO
					.UpdateMemberBuilder()
					.dto(joinDto)
					.build();

			if(memberService.update(memberVO) <= 0){
				//가입 실패
				return axRet.set(ApiResultError.JOIN_ERR, getLocale());
			}

			//비밀번호 변경
			if(!StringUtils.isBlank(joinDto.getCurrentPswd()) && !StringUtils.isBlank(joinDto.getPswd())){
				//현재 비밀번호 확인
				MemberSearchDTO searchDTO = new MemberSearchDTO();
				searchDTO.setMembSeq(joinDto.getMembSeq());
				searchDTO.setPswd(joinDto.getCurrentPswd());
				MemberVO user = memberService.loadPassword(searchDTO);

				if(null == user){
					return axRet.set(ApiResultError.JOIN_ERR_NOT_MATCH_PASSWD, getLocale());
				}
				//비밀번호 포함 업데이트
				if(memberService.updatePassword(memberVO) <= 0){
					//수정 실패
					return axRet.set(ApiResultError.ERROR_DEFAULT, getLocale());
				}
			}

			PartnerVO partnerVO = PartnerVO
					.UpdatePtnBuilder()
					.fromDto(joinDto)
					.build();

			if(partnerService.update(partnerVO) <= 0){
				return axRet.set(ApiResultError.JOIN_ERR, getLocale());
			}

		}else{
			//사업자 번호 중복 가입 실패
			return axRet.set(ApiResultError.ERROR_REG_COMP_OVERLAP, getLocale());
		}
		axRet.set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}
}
