package com.erns.coching.view.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.erns.coching.common.mail.CochingMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.erns.coching.common.type.LocaleType;
import com.erns.coching.common.type.MailType;
import com.erns.coching.common.type.ShortUrlType;
import com.erns.coching.join.domain.UserJoinDTO;
import com.erns.coching.mail.domain.MailBatchVO;
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

	@Value("${coching.mail.sender:kpros@kpros.kr}")
	private String mailSender;
	@Autowired
	private CochingMailService mailService;

	//이메일 인증 발송 테스트
//	@RequestMapping("/mailCheck.do")
//	public void testSendmailCheck(
//			HttpServletRequest request,
//			HttpServletResponse response) {
//
//			//메일 발송
//			mailService.sendEmail(mailSender, "dsnam@erns.co.kr", "테스트 메일", "<h1>메일 전송 테스트</h1>");
//
//		log.debug("mail 전송 성공");
//	}
}
