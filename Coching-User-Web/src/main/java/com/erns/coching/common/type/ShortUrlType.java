package com.erns.coching.common.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ShortUrlType {

	VIEW_RAW_REQUEST("VIEW_RAW_REQUEST"
			, "원료 요청"
			, "/coching-user/requestRawDetail"
			, "GET"
			),
	VIEW_RAW_REQUEST_REPLY("VIEW_RAW_REQUEST_REPLY"
			, "원료 요청 자료 회신"
			, "/coching-user/requestRawDetail"
			, "GET"
	),

	VIEW_RESET_USER_PASSWD("VIEW_RESET_USER_PASSWD"
			, "비밀번호 변경"
			, "/common/login/resetPasswd.do"
			, "POST"
			)
	;

	private String code;
	private String desc;
	private String targetUrl;
	private String method;

}
