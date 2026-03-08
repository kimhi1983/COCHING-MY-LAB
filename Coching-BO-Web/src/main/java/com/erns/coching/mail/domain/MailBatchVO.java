package com.erns.coching.mail.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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

import java.util.Map;

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
@Slf4j
public class MailBatchVO{

	protected static final ObjectMapper objectMapper = new ObjectMapper();

	private long batchSeq;
	private String result;
	private String resultData;
	private int errCnt;
	private String sendData;
	private long membSeq;
	private String toEmail;		//TO_MAIL - 수신자 이메일
	private String sendMail;	//SEND_MAIL - 송신자 이메일

	private String title; 		//제목
	private String content; 	//내용
	private String fileId;
	private Map<String, Object> resultMap;

	/**
	 * 메일 전송 결과
	 * @param batchSeq
	 * @param result
	 * @param resultMap
	 */
	@Builder(builderClassName = "MailMessageBuilder", builderMethodName = "MailMessageBuilder")
	public MailBatchVO(long batchSeq, String result, Map<String, Object> resultMap) {
		Assert.notNull(batchSeq	, "batchSeq must not be null");
		Assert.notNull(result	, "result must not be null");

		this.batchSeq = batchSeq;
		this.result = result;
		setResultMap(resultMap);
	}

	/**
	 * 에러 결과 map 처리
	 * @param resultMap
	 */
	private void setResultMap(Map<String, Object> resultMap) {
		this.resultMap = resultMap;

		try {
			this.resultData = objectMapper.writeValueAsString(resultMap);
		} catch (Exception e) {
			log.error("resultMap 변환중 오류가 발생했습니다. resultMap={}", resultMap, e);
			// throw new IllegalStateException("ResponseData 변환중 오류가 발생했습니다.");
			this.resultData = "{}";
		}
	}
}
