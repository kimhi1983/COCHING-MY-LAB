package com.erns.coching.mail.service;

import java.util.List;
import java.util.Map;

import com.erns.coching.common.type.LocaleType;
import com.erns.coching.common.type.MailType;
import com.erns.coching.mail.domain.MailBatchVO;
import com.erns.coching.mail.domain.MailVO;

/**
 *
 * <p>Mail 전송 Service</p>
 *
 * @author Hunwoo Park
 *
 */
public interface MailService {

	public int updateSuccess(MailBatchVO param);

	public int updateFail(MailBatchVO param);

	public List<MailBatchVO> selectWillSend(int maxCount);

	public void sendMail(int maxCount);
	
	public MailVO sendTemplateMail(String toEmail, long membSeq, MailType mailType, LocaleType locale, String sendDate, long mailSeq, Map<String, Object> mailData,	String sendMail, String relationNo, String fileId);
}
