package com.erns.coching.common.type;

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

	public static LocaleType findByCode(String code) {
		for(LocaleType e : values()) {
			if(e.code.equals(code)) return e;
		}
		return null;
	}
}
