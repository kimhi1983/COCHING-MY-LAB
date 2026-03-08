package com.erns.es.common.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ShortUrlType {

	TEST	("ERNS_ES_API_TEST1"
			, "테스트 URL"
			, "/test/test.do"
			, "POST"),
	;

	private String code;
	private String desc;
	private String targetUrl;
	private String method;

}
