package com.erns.coching.common.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 이메일 타입
 * @author hw.park@erns.co.kr
 *
 * 사용자 로그 타입 정의
 *
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum MailType {

	CREATE_REQUEST 				("CREATE_REQUEST"
			, "원료 요청"
			, "Raw Material Request"
			, "classpath:/mail/template/ko/mail_02.html"
			, "classpath:/mail/template/en/mail_02.html"
	),
	SEND_REQUEST		("SEND_REQUEST"
			, "원료 요청 자료 회신"
			, "Reply to raw material request data"
			, "classpath:/mail/template/ko/mail_01.html"
			, "classpath:/mail/template/en/mail_01.html"
	),
	RESET_USER_PASSWD	("RESET_USER_PASSWD"
			, "비밀번호 변경요청 안내"
			, "PASSWORD CHANGE REQUEST GUIDE"
			, "classpath:/mail/template/ko/mail_90.html"
			, "classpath:/mail/template/en/mail_90.html"
	),
	RESET_USER_PASSWD_5ERROR	("RESET_USER_PASSWD"
			, "비밀번호 변경요청 안내"
			, "PASSWORD CHANGE REQUEST GUIDE"
			, "classpath:/mail/template/ko/mail_91.html"
			, "classpath:/mail/template/en/mail_91.html"
	)
	;

	private String code;
	private String nameKo;
	private String nameEn;
	private String mailKoTemplatePath;
	private String mailEnTemplatePath;

	public static MailType find(String name) {
		for(MailType e : values()) {
			if(e.name().equals(name)) return e;
		}
		return null;
	}

	public static MailType findByCode(String code) {
		for(MailType e : values()) {
			if(e.code.equals(code)) return e;
		}
		return null;
	}
}
