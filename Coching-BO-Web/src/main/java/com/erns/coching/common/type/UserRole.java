package com.erns.coching.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserRole {
	COCHING_USER("ROLE_COCHING_USER"),
	COCHING_PARTNER("ROLE_COCHING_PARTNER"),
	ADMIN("ROLE_ADMIN");


    private String value;
}
