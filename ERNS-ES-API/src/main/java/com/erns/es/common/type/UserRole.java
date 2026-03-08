package com.erns.es.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserRole {
	ES_USER("ROLE_ES_USER"),
	ADMIN("ROLE_ADMIN");


    private String value;
}
