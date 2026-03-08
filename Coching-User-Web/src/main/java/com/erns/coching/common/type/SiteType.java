package com.erns.coching.common.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum SiteType {

	USER_WEB 					("USER_WEB", "사용자 WEB"),
	PARTNER_WEB 				("PARTNER_WEB", "파트너 WEB"),
	ADMIN_WEB					("ADMIN_WEB", "관리자 WEB")

	;

	private String code;
	private String nameKo;

}
