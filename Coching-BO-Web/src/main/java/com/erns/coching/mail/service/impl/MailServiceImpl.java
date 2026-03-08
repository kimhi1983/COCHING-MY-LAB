package com.erns.coching.mail.service.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.erns.coching.common.model.MailData;
import com.erns.coching.common.type.LocaleType;
import com.erns.coching.common.type.MailType;
import com.erns.coching.mail.domain.MailBatchVO;
import com.erns.coching.mail.domain.MailVO;
import com.erns.coching.mail.mapper.MailDAO;
import com.erns.coching.mail.service.MailService;

import lombok.extern.slf4j.Slf4j;

import javax.mail.internet.MimeMessage;

/**
 *
 * <p>Mail 전송 Service</p>
 *
 * @author Ndasom
 *
 */
@Slf4j
@Service
public class MailServiceImpl implements MailService {
	
	@Value("${coching.mail.isTest:true}") private boolean isMailTest;
	@Value("${coching.mail.toMail:true}") private String testToMail;
	@Value("#{'${server.base.url:https://www.coching.co.kr}'.replaceAll('\\s+', '').split(',')}")
	private String SERVER_BASE_URL = "";

	@Autowired
	private MailDAO dao;
	
	@Autowired
	private ResourceLoader resourceLoader;

    @Value("${coching.mail.sender:kpros@kpros.kr}") private String mSenderEmail;
	@Value("${mail.backup.host:192.168.1.104}") private String backupHost;
    @Value("${mail.backup.port:25}") private int backupPort;
    @Value("${mail.backup.username:}") private String backupUsername;
    @Value("${mail.backup.password:}") private String backupPassword;
    @Value("${mail.backup.auth:false}") private boolean backupAuth;
    @Value("${mail.backup.starttls.enable:true}") private boolean backupStarttls;
    @Value("${mail.backup.ssl.enable:false}") private boolean backupSsl;

    @Autowired
    private JavaMailSender primaryMailSender;  // 기본 SMTP
    private JavaMailSender backupMailSender;   // 백업 SMTP
    private String errorMsg;


	@Override
	public int updateSuccess(MailBatchVO param) {
		return dao.updateSuccess(param);
	}

	@Override
	public int updateFail(MailBatchVO param) {
		return dao.updateFail(param);
	}

	@Override
	public List<MailBatchVO> selectWillSend(int maxCount) {
		return dao.selectWillSend(maxCount);
	}

	@Override
	public void sendMail(int maxCount) {

        List<MailBatchVO> sendList = selectWillSend(maxCount);

        int sendCount = sendList.size();
        log.info("get mail count : {}", sendCount);

        if(sendCount <= 0){
            return;
        }

        if(sendCount > 0){
            for(MailBatchVO mail : sendList){
                String mToEmail = mail.getToEmail(); //받는사람
                String mTitle = mail.getTitle(); //타이틀
                String mContent = mail.getContent(); //컨텐츠

                boolean isSuccess = sendWithMailSender(primaryMailSender, mSenderEmail, mToEmail, mTitle, mContent);

                if (!isSuccess) {
                    //hiworks로 실패 시 erns 메일서버로 다시 전송
                    this.backupMailSender = configureBackupMailSender();
                    isSuccess = sendWithMailSender(backupMailSender, mSenderEmail, mToEmail, mTitle, mContent);
                }

                if (!isSuccess) {
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("errMsg", errorMsg);
                    //모두 실패 시 오류 메세지 처리
                    MailBatchVO failVo = MailBatchVO
                            .MailMessageBuilder()
                            .batchSeq(mail.getBatchSeq())
                            .result("F")
                            .resultMap(resultMap)
                            .build();
                    updateFail(failVo);
                }else{
                    //성공 시 상태, 날짜 업데이트
                	MailBatchVO successVo = MailBatchVO
                            .MailMessageBuilder()
                            .batchSeq(mail.getBatchSeq())
                            .result("S")
                            .build();
                    updateSuccess(successVo);
                }
            }
        }
	}

	private boolean sendWithMailSender(JavaMailSender mailSender, String mSenderEmail, String mToEmail, String mTitle, String mContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper mailHelper = new MimeMessageHelper(message, true, "UTF-8");

            mailHelper.setFrom(mSenderEmail);
            mailHelper.setTo(mToEmail);
            mailHelper.setSubject(mTitle);
            mailHelper.setText(mContent, true);

            mailSender.send(message);
            return true;
        } catch (Exception e) {
            log.error("Error! sendWithMailSender", e);
            if (mailSender instanceof JavaMailSenderImpl) {
                JavaMailSenderImpl senderImpl = (JavaMailSenderImpl) mailSender;
                String currentHost = senderImpl.getHost();

                // HiWorks 메일 서버에서 오류 발생 시 로그 저장
                if ("smtp.hiworks.com".equalsIgnoreCase(currentHost)) {
                    log.error("🚨 HiWorks 메일 전송 실패! SMTP Host: " + currentHost, e);
                    errorMsg = e.getMessage();
                }
            }
            return false;
        }
    }

    // 백업 SMTP 설정
    private JavaMailSender configureBackupMailSender() {
        JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
        mailSenderImpl.setHost(backupHost);  // 백업 SMTP 서버
        mailSenderImpl.setPort(backupPort);  // TLS 사용 (SSL일 경우 465)
        mailSenderImpl.setUsername(backupUsername);
        mailSenderImpl.setPassword(backupPassword);
        mailSenderImpl.setProtocol("smtp");

        Properties props = mailSenderImpl.getJavaMailProperties();
        props.put("mail.smtp.auth", backupAuth);
        props.put("mail.smtp.starttls.enable", backupStarttls);

        mailSenderImpl.setJavaMailProperties(props);
        return mailSenderImpl;
    }
    
	@Override
	public MailVO sendTemplateMail(
			String toEmail, long membSeq,
			MailType mailType,
			LocaleType locale,
			String sendDate,
			long mailSeq,
			Map<String, Object> mailData,
			String sendMail, String relationNo, String fileId) {

		String title = LocaleType.KO == locale ? mailType.getNameKo() : mailType.getNameEn();

		//content 템플릿 파일 로드
		String contents = "";
		String templatePath = LocaleType.KO == locale ? mailType.getMailKoTemplatePath() : mailType.getMailEnTemplatePath();
		StringBuilder stringBuilder = new StringBuilder();
		Resource resource = resourceLoader.getResource(templatePath);
		try (InputStream inputStream = resource.getInputStream();
			 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

			String line;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line).append("\n");
			}
			contents = stringBuilder.toString();
		}catch(Exception e) {
			contents = "";
		}

		//contents 내용변경
		//1.host, mailName 내용변경
		contents = contents.replace("##$${{baseUrl}}$$##", SERVER_BASE_URL);
		contents = contents.replace("$${{mailName}}$$", LocaleType.KO == locale ? mailType.getNameKo() : mailType.getNameEn());

		//2.mailData 값 변경
		for(String keyVal : mailData.keySet()) {
			String targetVar = String.format("$${{%s}}$$", keyVal);
			String replaceVal = mailData.getOrDefault(keyVal, "").toString();

			log.debug("{}=>{}",targetVar, replaceVal);
			contents = contents.replace(targetVar, replaceVal);
		}
		//log.debug("{}=>{}",contents);

		try {
			if(isMailTest){
				//테스트 일 경우 toMail 고정
				toEmail = testToMail;
			}
			MailData fpd = new MailData(title, contents);
			MailVO fpm = MailVO.MailMessageBuilder()
					.membSeq(membSeq)
					.sendDate(sendDate) //예약전송일 YYYY-MM-DD HH24:MI:SS
					.toEmail(toEmail)
					.priority(null)
					.data(fpd)
					.mailSeq(mailSeq)
					.fileId(fileId != "" ? fileId : null)
					.build();
			log.debug("mailVo:{}", fpm);

			dao.insert(fpm);

			return fpm;
		}catch(Exception e) {
			log.error("Error! sendMailForUser", e);
			return null;
		}
	}
	
}
