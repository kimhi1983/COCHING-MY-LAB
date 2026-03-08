package com.erns.coching.mail.domain;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * <p>Mail 전송 VO</p>
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString()
public class MailVO{

	private String toEmail;		//TO_MAIL - 수신자 이메일
	private String sendMail;	//SEND_MAIL - 송신자 이메일

	private String title; 		//제목
	private String content; 	//내용

	@Setter
	private String relationNo;

	private String fileId;

	@Default
	private long membSeq = 0;	//MEMB_SEQ

	/**
	 * 메일전송용
	 * @param toEmail
	 * @param title
	 * @param content
	 * @param membSeq
	 * @param mailSeq
	 * @throws JsonProcessingException
	 */
	@Builder(builderClassName = "MailMessageBuilder", builderMethodName = "MailMessageBuilder")
	public MailVO(String toEmail, String title, String content, long membSeq,
			String sendMail, String relationNo, String fileId) {
		Assert.notNull(toEmail	, "toEmail must not be null");
		Assert.notNull(membSeq	, "membSeq must not be null");
		Assert.notNull(title	, "title must not be null");
		Assert.notNull(content	, "content must not be null");

		this.toEmail = toEmail;
		this.title = title;
		this.content = content;
		this.membSeq = membSeq;

		this.sendMail = StringUtils.hasText(sendMail) ? sendMail : "webmaster@data.co.kr";
		this.relationNo = relationNo;
		this.fileId = fileId;
	}
}
