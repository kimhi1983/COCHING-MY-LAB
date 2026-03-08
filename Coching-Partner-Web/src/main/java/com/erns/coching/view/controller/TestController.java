package com.erns.coching.view.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.erns.coching.common.type.LocaleType;
import com.erns.coching.common.type.MailType;
import com.erns.coching.common.type.ShortUrlType;
import com.erns.coching.join.domain.UserJoinDTO;
import com.erns.coching.mail.domain.MailVO;
import com.erns.coching.mail.service.MailService;
import com.erns.coching.url.domain.ShortUrlVO;
import com.erns.coching.url.service.ShortUrlService;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * <p>테스트 컨트롤러</p>
 *
 * @author Hunwoo Park
 *
 */
@Slf4j
@Controller
@RequestMapping("/common/test") //TODO : 배포시 제거 필요
public class TestController {

	@Autowired
	private ShortUrlService shortUrlService;

	@Autowired
	private MailService mailService;

	//이메일 인증 발송 테스트
	@RequestMapping("/mailCheck.do")
	public void testSendmailCheck(
			HttpServletRequest request,
			HttpServletResponse response) {

		UserJoinDTO joinDto = UserJoinDTO.builder()
				.membSeq(86)
//				.nation("001") //한국
				.membName("박헌우")
				.ptnName("이알앤에스")
				.email("hw.park@erns.co.kr")
				.build();

		{
			log.debug("joinDto:{}", joinDto);

			//고객 이메일 인증 발송
			String checkToken = UUID.randomUUID().toString();

			//Short URL 생성
			Map<String, Object> urlParam = new HashMap<String, Object>();
			urlParam.put("membSeq", joinDto.getMembSeq());

			ShortUrlVO suvo = ShortUrlVO.ShortUrlTypeBuilder()
					.urlType(ShortUrlType.USER_MAIL_CHECK)
					.token(checkToken) //check token
					.parameters(urlParam)
					.build();
			shortUrlService.insert(suvo);
			log.debug("suvo:{}", suvo);

			//메일 발송
//			MailType mailType = MailType.JOIN;
//			LocaleType locale = "001".equals(joinDto.getNation()) ? LocaleType.KO : LocaleType.EN;
//			Map<String, Object> mailData = new HashMap<String, Object>();
//			mailData.put("compName", joinDto.getCompNm());
//			mailData.put("userName", joinDto.getMembName());
//			mailData.put("buttonLink", suvo.getRequestUrl());
//			mailData.put("buttonName", "이메일 인증하기");
//
//
//			MailVO mailVo = mailService.sendTemplateMail(
//					joinDto.getEmail(), joinDto.getMembSeq(),
//					mailType, locale, mailData,
//					null, null, null);
//
//			log.debug("mailVo:{}", mailVo);

		}
	}

	//구매계약 생성 안내 발송 테스트
	@RequestMapping("/createContractMail.do")
	public void testCreateContract(
			HttpServletRequest request,
			HttpServletResponse response) {

		String nation = "001";  //한국어
		long membSeq = 1;		//판매기업담당자 시퀀스
		String toEmail = "test@test.com";	//판매기업 담당자 이메일

		long cntrcSeq = 150;	//계약시퀀스
		String fmtCntrcRgtDttm = "23년 12월 27일";	//계약생성일
		String cntrcName = "결제생성 대기중 1-2";	//계약명
		String cntrcCreateCompType = "001"; //001 : 구매계약/ 002:판매계약
		String partnerCompTypeName = "001".equals(cntrcCreateCompType) ? "판매회사" : "구매회사";	//상대방 회사 타입

		{
			//Short URL 생성
			Map<String, Object> urlParam = new HashMap<String, Object>();
			urlParam.put("cntrcSeq", cntrcSeq);

			ShortUrlVO suvo = ShortUrlVO.ShortUrlTypeBuilder()
					.urlType(ShortUrlType.VIEW_PROCESS_CONTRACT_AGREEMENT)
					.parameters(urlParam)
					.build();
			shortUrlService.insert(suvo);
			log.debug("suvo:{}", suvo);

			//메일 발송
			MailType mailType = MailType.CREATE_CONTRACT;
			LocaleType locale = "001".equals(nation) ? LocaleType.KO : LocaleType.EN;
			Map<String, Object> mailData = new HashMap<String, Object>();
			mailData.put("fmtCntrcRgtDttm", fmtCntrcRgtDttm);
			mailData.put("cntrcName", cntrcName);
			mailData.put("partnerCompTypeName", partnerCompTypeName);
			mailData.put("buttonLink", suvo.getRequestUrl());
			mailData.put("buttonName", "계약 보기");

			MailVO mailVo = mailService.sendTemplateMail(
					toEmail, membSeq,
					mailType, locale, mailData,
					null, null, null);

			log.debug("mailVo:{}", mailVo);

		}
	}

	//구매계약 결제 대기 안내 발송 테스트
	@RequestMapping("/waitingPaymentMail.do")
	public void testWaitingPaymentMail(
			HttpServletRequest request,
			HttpServletResponse response) {

		String nation = "001";  //한국어
		long membSeq = 1;		//구매기업담당자 시퀀스
		String toEmail = "test@test.com";	//구매기업 담당자 이메일

		long cntrcSeq = 150;	//계약시퀀스
		String cntrcName = "결제생성 대기중 1-2";	//계약명


		{
			//Short URL 생성
			Map<String, Object> urlParam = new HashMap<String, Object>();
			urlParam.put("cntrcSeq", cntrcSeq);

			ShortUrlVO suvo = ShortUrlVO.ShortUrlTypeBuilder()
					.urlType(ShortUrlType.VIEW_PROCESS_WAITING_PAYMENT)
					.parameters(urlParam)
					.build();
			shortUrlService.insert(suvo);
			log.debug("suvo:{}", suvo);

			//메일 발송
			MailType mailType = MailType.WAIT_PAYMENT;
			LocaleType locale = "001".equals(nation) ? LocaleType.KO : LocaleType.EN;
			Map<String, Object> mailData = new HashMap<String, Object>();
			mailData.put("cntrcName", cntrcName);
			mailData.put("buttonLink", suvo.getRequestUrl());
			mailData.put("buttonName", "계약 보기");

			MailVO mailVo = mailService.sendTemplateMail(
					toEmail, membSeq,
					mailType, locale, mailData,
					null, null, null);

			log.debug("mailVo:{}", mailVo);

		}
	}

	//구매대금 결제 완료 안내 발송 테스트
	@RequestMapping("/completePaymentMail.do")
	public void testCompletePaymentMail(
			HttpServletRequest request,
			HttpServletResponse response) {

		String nation = "001";  //한국어
		long membSeq = 1;		//판매기업담당자 시퀀스
		String toEmail = "test@test.com";	//판매기업 담당자 이메일

		long cntrcSeq = 150;	//계약시퀀스
		String fmtCntrcRgtDttm = "23년 12월 27일";	//계약생성일
		String cntrcName = "결제생성 대기중 1-2";	//계약명

		String purchaseCompName = "이알앤에스";	//구매기업명
		String salesCompName = "나이스디엔알";	//판매기업명


		{
			//Short URL 생성
			Map<String, Object> urlParam = new HashMap<String, Object>();
			urlParam.put("cntrcSeq", cntrcSeq);

			ShortUrlVO suvo = ShortUrlVO.ShortUrlTypeBuilder()
					.urlType(ShortUrlType.VIEW_PROCESS_PAYMENT_COMPLETE)
					.parameters(urlParam)
					.build();
			shortUrlService.insert(suvo);
			log.debug("suvo:{}", suvo);

			//메일 발송
			MailType mailType = MailType.COMPLETE_PAYMET;
			LocaleType locale = "001".equals(nation) ? LocaleType.KO : LocaleType.EN;
			Map<String, Object> mailData = new HashMap<String, Object>();
			mailData.put("fmtCntrcRgtDttm", fmtCntrcRgtDttm);
			mailData.put("cntrcName", cntrcName);
			mailData.put("purchaseCompName", purchaseCompName);
			mailData.put("salesCompName", salesCompName);
			mailData.put("buttonLink", suvo.getRequestUrl());
			mailData.put("buttonName", "계약 보기");

			MailVO mailVo = mailService.sendTemplateMail(
					toEmail, membSeq,
					mailType, locale, mailData,
					null, null, null);

			log.debug("mailVo:{}", mailVo);

		}
	}

	//판매대금 지급 완료 안내 발송 테스트
	@RequestMapping("/completeContractMail.do")
	public void testCompleteContractMail(
			HttpServletRequest request,
			HttpServletResponse response) {

		String nation = "001";  //한국어
		long membSeq = 1;		//판매기업담당자 시퀀스
		String toEmail = "test@test.com";	//판매기업 담당자 이메일

		long cntrcSeq = 150;	//계약시퀀스
		String fmtCntrcRgtDttm = "23년 12월 27일";	//계약생성일
		String cntrcName = "결제생성 대기중 1-2";	//계약명

		String salesCompName = "나이스디엔알";	//판매기업명


		{
			//Short URL 생성
			Map<String, Object> urlParam = new HashMap<String, Object>();
			urlParam.put("cntrcSeq", cntrcSeq);

			ShortUrlVO suvo = ShortUrlVO.ShortUrlTypeBuilder()
					.urlType(ShortUrlType.VIEW_COMPLETION_CONTRACT)
					.parameters(urlParam)
					.build();
			shortUrlService.insert(suvo);
			log.debug("suvo:{}", suvo);

			//메일 발송
			MailType mailType = MailType.COMPLETE_SETTLE;
			LocaleType locale = "001".equals(nation) ? LocaleType.KO : LocaleType.EN;
			Map<String, Object> mailData = new HashMap<String, Object>();
			mailData.put("fmtCntrcRgtDttm", fmtCntrcRgtDttm);
			mailData.put("cntrcName", cntrcName);
			mailData.put("salesCompName", salesCompName);
			mailData.put("buttonLink", suvo.getRequestUrl());
			mailData.put("buttonName", "계약 보기");

			MailVO mailVo = mailService.sendTemplateMail(
					toEmail, membSeq,
					mailType, locale, mailData,
					null, null, null);

			log.debug("mailVo:{}", mailVo);

		}
	}
}
