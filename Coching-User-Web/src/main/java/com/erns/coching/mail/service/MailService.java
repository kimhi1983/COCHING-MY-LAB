package com.erns.coching.mail.service;

import com.erns.coching.common.type.LocaleType;
import com.erns.coching.common.type.MailType;
import com.erns.coching.mail.domain.MailVO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * <p>Mail 전송 Service</p>
 *
 * @author Hunwoo Park
 *
 */
public interface MailService {

	public int insert(MailVO param);
	public int insert(Collection<MailVO> list);
	public int insert(MailVO[] array);

	public int updateSuccess(MailVO param);
	public int updateSuccess(Collection<MailVO> list);
	public int updateSuccess(MailVO[] array);

	public int updateFail(MailVO param);
	public int updateFail(Collection<MailVO> list);
	public int updateFail(MailVO[] array);

	public List<MailVO> selectWillSend(int maxCount);

	/**
	 * push 발송
	 * @param membSeq 사용자 seq
	 * @param title	제목
	 * @param content 내용
	 */
	public void setSendMailForUser(long membSeq, String title, String content);

	/**
	 * push 발송
	 * @param membSeq 사용자 seq
	 * @param title	제목
	 * @param content 내용
	 * @param sendDate 예약발송일 : null 이면 즉시발송 or YYYY-MM-DD HH24:MI:SS
	 */
	public void setSendMailForUser(long membSeq, String title, String content, String sendDate, long mailSeq, String fileId);

	public MailVO sendTemplateMail(String toEmail, long membSeq, MailType mailType, LocaleType locale, String sendDate, long mailSeq, Map<String, Object> mailData,	String sendMail, String relationNo, String fileId);
}
