package com.erns.coching.common.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ShortUrlType {

	USER_MAIL_CHECK	("USER_MAIL_CHECK"
			, "회원가입시 사용자 이메일 확인"
			, "/common/join/emailCheck.do"
			, "POST"),
	USER_TERMS_UPDATE	("USER_TERMS_UPDATE"
			, "대리 회원 가입시 약정 동의"
			, "/common/join/instead.do"
			, "POST"),
	VIEW_PROCESS_CONTRACT_AGREEMENT("VIEW_PROCESS_CONTRACT_AGREEMENT"
			, "진행계약조회_동의"
			, "/coching-pc/contract/processing/agreement"
			, "GET"
			),
	VIEW_PROCESS_WAITING_PAYMENT("VIEW_PROCESS_WAITING_PAYMENT"
			, "진행계약_결제대기"
			, "/coching-pc/contract/processing/payment"
			, "GET"
			),
	VIEW_PROCESS_PAYMENT_COMPLETE("VIEW_PROCESS_PAYMENT_COMPLETE"
			, "진행계약_결제완료"
			, "/coching-pc/contract/processing/complete"
			, "GET"
			),
	VIEW_COMPLETION_CONTRACT("VIEW_COMPLETION_CONTRACT"
			, "완료계약_지급완료"
			, "/coching-pc/contract/completion"
			, "GET"
			),
	VIEW_INQUIRY("VIEW_INQUIRY"
			, "1:1문의_답변완료"
			, "/coching-user/board/inqr/view"
			, "GET"
			)
	;

	private String code;
	private String desc;
	private String targetUrl;
	private String method;

}
