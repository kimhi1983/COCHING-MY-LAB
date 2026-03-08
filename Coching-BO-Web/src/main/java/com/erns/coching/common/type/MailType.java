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

	JOIN 				("JOIN"
			, "회원가입 인증 안내"
			, "MEMBERSHIP VERIFICATION GUIDE"
			, "classpath:/mail/template/ko/mail_01.html"
			, "classpath:/mail/template/en/mail_01.html"
	),
	CREATE_CONTRACT		("CREATE_CONTRACT"
			, "구매계약 생성 안내"
			, "GUIDE CREATING PURCHASE CONTRACT"
			, "classpath:/mail/template/ko/mail_02.html"
			, "classpath:/mail/template/en/mail_02.html"
	),
	WAIT_PAYMENT		("WAIT_PAYMENT"
			, "구매계약 결제 대기 안내"
			, "WAITING PURCHASE CONTRACT PAYMENT"
			, "classpath:/mail/template/ko/mail_03.html"
			, "classpath:/mail/template/en/mail_03.html"
	),
	COMPLETE_PAYMET		("COMPLETE_PAYMET"
			, "구매대금 결제 완료 안내"
			, "INFORMATION PURCHASE PAYMENT COMPLETION"
			, "classpath:/mail/template/ko/mail_04.html"
			, "classpath:/mail/template/en/mail_04.html"
	),
	COMPLETE_SETTLE		("COMPLETE_SETTLE"
			, "판매대금 지급 완료 안내"
			, "INFORMATION COMPLETION PAYMENT SALES PRICE"
			, "classpath:/mail/template/ko/mail_05.html"
			, "classpath:/mail/template/en/mail_05.html"
	),
	SEND_INQUIRY		("SEND_INQUIRY"
			, "1:1 문의 답변 완료 안내"
			, "Reply to inquiry"
			, "classpath:/mail/template/ko/mail_07.html"
			, "classpath:/mail/template/en/mail_07.html"
	),
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
