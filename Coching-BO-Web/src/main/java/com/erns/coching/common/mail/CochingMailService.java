package com.erns.coching.common.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CochingMailService {

//    @Value("${mail.backup.host}") private String backupHost;
//    @Value("${mail.backup.port}") private int backupPort;
//    @Value("${mail.backup.username:}") private String backupUsername;
//    @Value("${mail.backup.password}") private String backupPassword;
//    @Value("${mail.backup.auth}") private boolean backupAuth;
//    @Value("${mail.backup.starttls.enable}") private boolean backupStarttls;
//    @Value("${mail.backup.ssl.enable}") private boolean backupSsl;
//
//    @Autowired
//    private JavaMailSender primaryMailSender;  // 기본 SMTP
//    private JavaMailSender backupMailSender;   // 백업 SMTP
//    private String errorMsg;
//
//    public void sendEmail(String mSenderEmail, String mToEmail, String mTitle, String mContent) {
//        boolean isSuccess = sendWithMailSender(primaryMailSender, mSenderEmail, mToEmail, mTitle, mContent);
//
//        if (!isSuccess) {
//            this.backupMailSender = configureBackupMailSender();
//            isSuccess = sendWithMailSender(backupMailSender, mSenderEmail, mToEmail, mTitle, mContent);
//        }
//
//        if (!isSuccess) {
//            //모두 실패 시 오류 메세지 처리
//
//        }
//    }
//
//    private boolean sendWithMailSender(JavaMailSender mailSender, String mSenderEmail, String mToEmail, String mTitle, String mContent) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper mailHelper = new MimeMessageHelper(message, true, "UTF-8");
//
//            mailHelper.setFrom(mSenderEmail);
//            mailHelper.setTo(mToEmail);
//            mailHelper.setSubject(mTitle);
//            mailHelper.setText(mContent, true);
//
//            mailSender.send(message);
//            return true;
//        } catch (Exception e) {
//            log.error("Error! sendWithMailSender", e);
//            if (mailSender instanceof JavaMailSenderImpl) {
//                JavaMailSenderImpl senderImpl = (JavaMailSenderImpl) mailSender;
//                String currentHost = senderImpl.getHost();
//
//                // HiWorks 메일 서버에서 오류 발생 시 로그 저장
//                if ("smtp.hiworks.com".equalsIgnoreCase(currentHost)) {
//                    log.error("🚨 HiWorks 메일 전송 실패! SMTP Host: " + currentHost, e);
//                    errorMsg = e.getMessage();
//                } else {
//                    //둘다 실패 시 에러 로그 update
//
//                }
//            }
//            return false;
//        }
//    }
//
//    // 백업 SMTP 설정
//    private JavaMailSender configureBackupMailSender() {
//        JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
//        mailSenderImpl.setHost(backupHost);  // 백업 SMTP 서버
//        mailSenderImpl.setPort(backupPort);  // TLS 사용 (SSL일 경우 465)
//        mailSenderImpl.setUsername(backupUsername);
//        mailSenderImpl.setPassword(backupPassword);
//        mailSenderImpl.setProtocol("smtp");
//
//        Properties props = mailSenderImpl.getJavaMailProperties();
//        props.put("mail.smtp.auth", backupAuth);
//        props.put("mail.smtp.starttls.enable", backupStarttls);
//
//        mailSenderImpl.setJavaMailProperties(props);
//        return mailSenderImpl;
//    }
}
