package com.erns.coching.common.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum RecentRefCode {
  RAW 					("raw", "원료"),
	PROD 			    ("prod", "제품"),
	;

	private String code;
	private String nameKo;
}
