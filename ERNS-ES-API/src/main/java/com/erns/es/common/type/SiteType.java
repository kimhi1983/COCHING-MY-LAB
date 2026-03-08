package com.erns.es.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SiteType {

	USER_WEB 					("USER_WEB", "사용자 WEB"),
	ADMIN_WEB					("ADMIN_WEB", "관리자 WEB")

	;

	private String code;
	private String nameKo;

}
