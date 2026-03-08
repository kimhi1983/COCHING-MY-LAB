package com.erns.coching.join.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erns.coching.join.service.UserJoinService;
import com.erns.coching.login.controller.BaseLoginController;
import com.erns.coching.mail.service.MailService;
import com.erns.coching.partner.service.PartnerService;
import com.erns.coching.url.service.ShortUrlService;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * <p>계정생성 API Controller</p>
 *
 * @author Kyungmin Lee
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/join")
@PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_ADMIN')")
public class UserJoinApiController extends BaseLoginController {

	@Autowired
	private UserJoinService userJoinService;

	@Autowired
	private PartnerService partnerService;

	@Autowired
	private ShortUrlService shortUrlService;

	@Autowired
	private MailService mailService;

	@Value("${member.rejoin.limit:0}")
	private int rejoinLimitDate;

	/**
	 * COMPANY 등록 / MEMBER 등록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
//	@ApiLogging
//	@PostMapping("/userJoin.api")
//	public ApiResult userJoin(
//			HttpServletRequest request,
//			@RequestBody @Validated(ValidGroupUserJoin0001.class) UserJoinDTO joinDto,
//			Errors errors) throws JOSEException {
//
//		ApiResult axRet = new ApiResult();
//		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
//			return bindError(errors, axRet);
//		}
//
//		//기업중복 확인
//		CompanySearchDTO search = new CompanySearchDTO();
//
//		search.setCompLicNum(joinDto.getCompLicNum());
//
//		Map<String, Object> companyMap = partnerService.loadComp(search);
//		CompanyVO companyVO = null;
//		long membSeq = 0;
//
//		log.debug("[JOIN INFO] {}", joinDto.toString());
//
//		if(null == companyMap) {
//			// 기업 신규 생성 + 멤버 신규 생성
//
//			long seq = partnerService.loadCompNextSeq();
//
//			MemberVO memberVO = MemberVO
//					.MemberJoinBuilder()
//					.dto(joinDto)
//					.compSeq(seq)
//					.status("001")
//					.build();
//
//			if(partnerService.insertMemb(memberVO) <= 0){
//				//가입 실패
//				return axRet.set(ApiResultError.JOIN_ERR);
//			}
//			membSeq = memberVO.getMembSeq();
//			companyVO = CompanyVO
//					.AddCompanyBuilder()
//					.compSeq(seq)
//					.rgtr(memberVO.getMembSeq())
//					.fromDto(joinDto)
//					.build();
//
//			if(partnerService.insertComp(companyVO) <= 0){
//				return axRet.set(ApiResultError.JOIN_ERR);
//			}
//
//
//			//TODO 탈퇴자처리
//
//		}else{
//			// 기업 존재 -> 멤버 신규 생성
//			long compSeq = ((BigDecimal) companyMap.get("compSeq")).longValue();
//
//			MemberVO userSearch = MemberVO
//					.MemberSearchBuilder()
//					.dto(joinDto)
//					.compSeq(compSeq)
//					.build();
//
//			MemberVO oldMembInfo = partnerService.loadMemb(userSearch);
//
//			if(null != oldMembInfo) {
//				//이미 가입된 회원입니다.
//				Map<String, Object> data = new HashMap<>();
//				data.put("userName", oldMembInfo.getMembName());
//				data.put("joinDate", DateUtil.SDF_DATEDOT.format(oldMembInfo.getJoinDttm()));
//				return axRet.set(ApiResultError.JOIN_ERR_OVERLAP).setResultData(data);
//			}
//
//			MemberVO memberVO = MemberVO
//					.MemberJoinBuilder()
//					.dto(joinDto)
//					.compSeq(compSeq)
//					.status("001")
//					.build();
//
//			if(partnerService.insertMemb(memberVO) <= 0){
//				//가입 실패
//				return axRet.set(ApiResultError.JOIN_ERR);
//			}
//			membSeq = memberVO.getMembSeq();
//		}
//
//		{	//고객 이메일 인증 발송
//			String checkToken = UUID.randomUUID().toString();
//
//			//Short URL 생성
//			Map<String, Object> urlParam = new HashMap<String, Object>();
//			urlParam.put("membSeq", membSeq);
//
//			ShortUrlVO suvo = ShortUrlVO.ShortUrlTypeBuilder()
//					.urlType(ShortUrlType.USER_TERMS_UPDATE)
//					.token(checkToken) //check token
//					.parameters(urlParam)
//					.build();
//			shortUrlService.insert(suvo);
//			log.debug("suvo:{}", suvo);
//
//			//메일 발송
//			MailType mailType = MailType.JOIN;
//			LocaleType locale = "001".equals(joinDto.getNation()) ? LocaleType.KO : LocaleType.EN;
//			Map<String, Object> mailData = new HashMap<String, Object>();
//			mailData.put("compName", joinDto.getCompNm());
//			mailData.put("userName", joinDto.getMembName());
//			mailData.put("buttonLink", suvo.getRequestUrl());
//			mailData.put("buttonName", "이메일 인증하기");
//
//			MailBatchVO mailVo = mailService.sendTemplateMail(
//					joinDto.getEmail(), joinDto.getMembSeq(),
//					mailType, locale, mailData,
//					null, null, null);
//			log.debug("mailVo:{}", mailVo);
//		}
//		return axRet.set(ApiResultError.NO_ERROR);
//	}

	/**
	 * EMAIL 전체 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
//	@ApiLogging
//	@PostMapping("/emailList.api")
//	public ApiResult getEmailListCount(
//			HttpServletRequest request,
//			@RequestBody MemberSearchDTO param,
//            Errors errors) {
//
//        ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
//        if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
//			return bindError(errors, axRet);
//		}
//
//        int totalItem = partnerService.getEmailListCount();
//        List<Map<String, Object>> list = null;
//        if(totalItem <= 0) {
//    		list = new ArrayList<Map<String, Object>>();
//    	} else {
//    		list = partnerService.getEmailList();
//    	}
//
//        axRet.setList(list);
//
//		return axRet.set(ApiResultError.NO_ERROR);
//	}


}
