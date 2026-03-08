package com.erns.coching.mail.domain;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.erns.coching.common.model.MailData;
import com.erns.coching.message.domain.BatchMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
 * <p>Mail 전송 VO
 * TB_OW_BATCH_FCM_MAIL</p>
 *
 * @author Hunwoo Park
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString()
public class MailVO extends BatchMessage {

	private long batchSeq;
	@Setter
	@JsonProperty("to")
	private String toEmail;		//TO_MAIL - 수신자 이메일
	private String priority;	//PRIORITY - 우선순위

	//@JsonRawValue
	private String title; 		//제목
	private String content; 		//내용

	private long mailSeq;

	private String fileId;

	@Setter
	private String relationNo;

	@Default
	private long membSeq = 0;	//MEMB_SEQ

	private static ObjectMapper OBJ_MAPPER = new ObjectMapper();

	/**
	 * 메일전송용
	 * @param toEmail
	 * @param sendDate
	 * @param priority
	 * @param data
	 * @param membSeq
	 * @throws JsonProcessingException
	 */
	@SuppressWarnings("unchecked")
	@Builder(builderClassName = "MailMessageBuilder", builderMethodName = "MailMessageBuilder")
	public MailVO(String toEmail, String sendDate, String priority, MailData data, long membSeq, long mailSeq, String fileId) throws JsonProcessingException {
		Assert.notNull(toEmail, "toEmail must not be null");
		//Assert.notNull(priority, "priority must not be null");
		Assert.notNull(data, "data must not be null");
		Assert.notNull(membSeq, "userSeq must not be null");

		this.sendDate = sendDate;
		this.membSeq = membSeq;
		this.toEmail = toEmail;
		this.priority = StringUtils.hasText(priority) ? priority : "normal";
		this.title = data.title;
		this.content = data.content;
		this.mailSeq = mailSeq;
		this.fileId = fileId;
	}

}
