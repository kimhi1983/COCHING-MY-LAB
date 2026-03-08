package com.erns.coching.mail.service.impl;

import com.erns.coching.common.model.MailData;
import com.erns.coching.common.type.LocaleType;
import com.erns.coching.common.type.MailType;
import com.erns.coching.mail.domain.MailVO;
import com.erns.coching.mail.mapper.MailDAO;
import com.erns.coching.mail.service.MailService;
import com.erns.coching.member.domain.MemberVO;
import com.erns.coching.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
	private MemberService memberService;

	@Autowired
	private ResourceLoader resourceLoader;

	@Override
	public int insert(MailVO param) {
		return dao.insert(param);
	}

	@Override
	public int insert(Collection<MailVO> list) {
		if(list == null || list.size() < 1) {
			return 0;
		}

		int retCnt = 0;
		for(MailVO item : list) {
			if(item != null) {
				this.insert(item);
				retCnt++;
			}
		}

		return retCnt;
	}

	@Override
	public int insert(MailVO[] array) {
		if(array == null || array.length < 1) {
			return 0;
		}

		int retCnt = 0;
		for(MailVO item : array) {
			if(item != null) {
				this.insert(item);
				retCnt++;
			}
		}

		return retCnt;
	}

	@Override
	public int updateSuccess(MailVO param) {
		return dao.updateSuccess(param);
	}

	@Override
	public int updateSuccess(Collection<MailVO> list) {
		if(list == null || list.size() < 1) {
			return 0;
		}

		int retCnt = 0;
		for(MailVO item : list) {
			if(item != null) {
				this.updateSuccess(item);
				retCnt++;
			}
		}

		return retCnt;
	}

	@Override
	public int updateSuccess(MailVO[] array) {
		if(array == null || array.length < 1) {
			return 0;
		}

		int retCnt = 0;
		for(MailVO item : array) {
			if(item != null) {
				this.updateSuccess(item);
				retCnt++;
			}
		}

		return retCnt;
	}

	@Override
	public int updateFail(MailVO param) {
		return dao.updateFail(param);
	}

	@Override
	public int updateFail(Collection<MailVO> list) {
		if(list == null || list.size() < 1) {
			return 0;
		}

		int retCnt = 0;
		for(MailVO item : list) {
			if(item != null) {
				this.updateFail(item);
				retCnt++;
			}
		}

		return retCnt;
	}

	@Override
	public int updateFail(MailVO[] array) {
		if(array == null || array.length < 1) {
			return 0;
		}

		int retCnt = 0;
		for(MailVO item : array) {
			if(item != null) {
				this.updateFail(item);
				retCnt++;
			}
		}

		return retCnt;
	}

	@Override
	public List<MailVO> selectWillSend(int maxCount) {
		return dao.selectWillSend(maxCount);
	}

	@Override
	public void setSendMailForUser(long membSeq, String title, String content) {
		setSendMailForUser(membSeq, title, content, null, 0, null);
	}

	@Override
	public void setSendMailForUser(long membSeq, String title, String content, String sendDate, long mailSeq, String fileId) {
		MemberVO user = MemberVO
				.MemberMailBuilder()
				.membSeq(membSeq)
				.build();
		user = memberService.load(user);
    	List<MailVO> sendMailList = new ArrayList<>();

    	try {
			MailData fpd = new MailData(title, content);
			MailVO fpm = MailVO.MailMessageBuilder()
					.membSeq(membSeq)
					.sendDate(sendDate) //예약전송일 YYYY-MM-DD HH24:MI:SS
					.toEmail(user.getEmail())
					.priority(null)
					.data(fpd)
					.mailSeq(mailSeq)
					.fileId(fileId != "" ? fileId : null)
					.build();

			sendMailList.add(fpm);
    	}catch(Exception e) {
    		log.error("Error! sendMailForUser", e);
    	}

    	if(!sendMailList.isEmpty()) {
    		insert(sendMailList);
    	}

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

			insert(fpm);

			return fpm;
		}catch(Exception e) {
			log.error("Error! sendMailForUser", e);
			return null;
		}
	}
}
