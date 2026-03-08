package com.erns.coching.mail.service;

import java.util.Map;

import com.erns.coching.common.type.LocaleType;
import com.erns.coching.common.type.MailType;
import com.erns.coching.mail.domain.MailVO;

/**
 *
 * <p>Mail 전송 Service</p>
 *
 * @author Hunwoo Park
 *
 */
public interface MailService {

	public int insert(MailVO param);

	/**
	 * 메일전송
	 * 메일타입, 로케일등으로 템플릿 메일을 발송한다.
	 *
	 * @param toEmail	받는사람 email
	 * @param membSeq	보내는 사람 membSeq
	 * @param mailType	메일타입
	 * @param locale	로케일 타입
	 * @param mailData	메일에 replace 할 변수 및 값
	 * @param sendMail	보내는 사람 email (유효한 값이 아니면, webmaster@data.co.kr 로 자동설정)
	 * @param relationNo relationNo
	 * @param fileId fileId
	 * @return
	 */
	public MailVO sendTemplateMail(
			String toEmail, long membSeq,
			MailType mailType,
			LocaleType locale,
			Map<String, Object> mailData,
			String sendMail, String relationNo, String fileId);
}
