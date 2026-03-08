package com.erns.coching.common.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 알림 타입
 * @author hw.park@erns.co.kr
 *
 * 사용자 로그 타입 정의
 *
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum NotificationType {

	MESSAGE 			("MSG"
			, "쪽지 수신"
	),
	REQUEST				("REQ"
			, "원료 요청"
	),
	REQUEST_CONFIRM		("REQCOMFIRM"
			, "원료 요청 확인"
	),
	REQUEST_SEND		("REQSEND"
			, "원료 요청 자료 회신"
	),
	REQUEST_MAIL		("REQMAIL"
			, "원료 요청 메일 회신"
	),
	SOURCING_CMT		("SRCCMT"
			, "게시글 댓글"
	),
	INQUIRY_SEND		("INQRSEND"
			, "1:1문의 답변"
	)
	;

	private String code;
	private String title;

	public static NotificationType find(String name) {
		for(NotificationType e : values()) {
			if(e.name().equals(name)) return e;
		}
		return null;
	}

	public static NotificationType findByCode(String code) {
		for(NotificationType e : values()) {
			if(e.code.equals(code)) return e;
		}
		return null;
	}
}
