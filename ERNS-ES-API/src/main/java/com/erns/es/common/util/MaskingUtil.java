package com.erns.es.common.util;

import org.springframework.util.StringUtils;

public class MaskingUtil {

	private static final String EMAIL_PATTERN = "([\\w.])(?:[\\w.]*)(@.*)";
	private static final String LASTNAME_PATTERN = "(?<=.{0}).";
	private static final String LAST_6_CHAR_PATTERN = "(.{6}$)";
	private static final String LAST_ID_PATTERN = "(.{3}$)";
	private static final String LAST_NAME_PATTERN = "(?<=.{1}).";

	/**
	 * 이메일 마스킹
	 * @param email
	 * @return
	 */
	public static String maskingEmail(String email) {
		if(!StringUtils.hasText(email)) {
			return "";
		}

		return email.replaceAll(EMAIL_PATTERN, "$1****$2");
	}

	/**
	 * 주민번호 마스킹
	 * @param certNum
	 * @return
	 */
	public static String maskingCertNum(String certNum) {
		if(!StringUtils.hasText(certNum)) {
			return "";
		}

		return certNum.replaceAll(LAST_6_CHAR_PATTERN, "******");
	}

	/**
	 * 사용자 이름 마스킹
	 * @param certNum
	 * @return
	 */
	public static String maskingUserName(String userName) {
		if(!StringUtils.hasText(userName)) {
			return "";
		}

		return userName.replaceAll(LASTNAME_PATTERN , "*");
	}

	/**
	 * 사용자 아이디 마스킹
	 * @param certNum
	 * @return
	 */
	public static String maskingUserId(String userId) {
		if(!StringUtils.hasText(userId)) {
			return "";
		}

		return userId.replaceAll(LAST_ID_PATTERN , "***");
	}

	/**
	 * 사용자 아이디 마스킹 - 자릿수만큼 자르고 마스킹
	 * @param userId
	 * @param maxLength
	 * @return
	 */
	public static String maskingUserId(String userId, int maxLength) {
		if(!StringUtils.hasText(userId)) {
			return "";
		}

		int orgLength = userId.length();
		String rUserId = userId.substring(0, Math.min(maxLength, orgLength));

		return rUserId.replaceAll(LAST_ID_PATTERN , "***");
	}

	/**
	 * 사용자 이름 마스킹 - 성 제외
	 * @param userId
	 * @return
	 */
	public static String maskingUserLastName(String userName) {
		if(!StringUtils.hasText(userName)) {
			return "";
		}

		return userName.replaceAll(LAST_NAME_PATTERN , "*");
	}
}
