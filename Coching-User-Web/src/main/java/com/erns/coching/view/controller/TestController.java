package com.erns.coching.view.controller;

import com.erns.coching.common.type.LocaleType;
import com.erns.coching.common.type.MailType;
import com.erns.coching.common.type.ShortUrlType;
import com.erns.coching.mail.domain.MailVO;
import com.erns.coching.mail.service.MailService;
import com.erns.coching.url.domain.ShortUrlVO;
import com.erns.coching.url.service.ShortUrlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 * <p>테스트 컨트롤러</p>
 *
 * @author Hunwoo Park
 *
 */
@Slf4j
@RestController
@RequestMapping("/common/test") //TODO : 배포시 제거 필요
public class TestController {

    @Autowired
    private ShortUrlService shortUrlService;

    @Autowired
    private MailService mailService;

    //메일 테스트
    @RequestMapping("/mailCheck.do")
    public void testSendmailCheck(
            HttpServletRequest request,
            HttpServletResponse response) {

        String checkToken = UUID.randomUUID().toString();
        //Short URL 생성
        Map<String, Object> urlParam = new HashMap<String, Object>();
        urlParam.put("membSeq", 14);

        ShortUrlVO suvo = ShortUrlVO.ShortUrlTypeBuilder()
                .urlType(ShortUrlType.VIEW_RESET_USER_PASSWD)
                .token(checkToken) //check token
                .parameters(urlParam)
                .build();
        shortUrlService.insert(suvo);
        log.debug("suvo:{}", suvo);

        //메일 발송
        String relationNo = String.valueOf(14);
        MailType mailType = MailType.RESET_USER_PASSWD;
        LocaleType locale = LocaleType.KO;
        Map<String, Object> mailData = new HashMap<String, Object>();
        mailData.put("buttonLink", suvo.getRequestUrl());
        mailData.put("buttonName", "비밀번호 변경하기");

        MailVO mailVo =  mailService.sendTemplateMail(
                "dsnam@erns.co.kr", 14,
                mailType, locale, null, 0, mailData,
                null, relationNo, null);

        log.debug("mailVo:{}", mailVo);
    }
}
