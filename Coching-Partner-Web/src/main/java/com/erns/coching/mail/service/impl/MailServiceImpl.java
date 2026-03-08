package com.erns.coching.mail.service.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.common.type.LocaleType;
import com.erns.coching.common.type.MailType;
import com.erns.coching.mail.domain.MailVO;
import com.erns.coching.mail.mapper.MailDAO;
import com.erns.coching.mail.service.MailService;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * <p>Mail 전송 Service</p>
 *
 * @author Ndasom
 *
 */
@Slf4j
@Service
@Transactional
public class MailServiceImpl implements MailService {

	@Autowired
	private MailDAO dao;

	@Value("#{'${server.base.url:https://www.coching.co.kr}'.replaceAll('\\s+', '').split(',')}")
	private String SERVER_BASE_URL = "";

	@Autowired
	private ResourceLoader resourceLoader;

	@Override
	public int insert(MailVO param) {
		return dao.insert(param);
	}

	@Override
	public MailVO sendTemplateMail(
			String toEmail, long membSeq,
			MailType mailType,
			LocaleType locale,
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
		//1.host 내용변경
		contents = contents.replace("##$${{baseUrl}}$$##", SERVER_BASE_URL);

		//2.mailData 값 변경
		for(String keyVal : mailData.keySet()) {
			String targetVar = String.format("$${{%s}}$$", keyVal);
			String replaceVal = mailData.getOrDefault(keyVal, "").toString();

			log.debug("{}=>{}",targetVar, replaceVal);
			contents = contents.replace(targetVar, replaceVal);
		}
		//log.debug("{}=>{}",contents);

		//이메일 보내기
		MailVO mailVo = MailVO.MailMessageBuilder()
				.toEmail(toEmail)
				.title(title)
				.content(contents)
				.membSeq(membSeq)
				.sendMail(sendMail)
				.relationNo(relationNo)
				.fileId(fileId)
				.build();

		insert(mailVo);

		return mailVo;
	}
}
