package com.erns.es.common.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum LocaleType {

	KO	("ko", "한국어"),
	EN	("en", "영어")
	;

	private String code;
	private String nameKo;

}
