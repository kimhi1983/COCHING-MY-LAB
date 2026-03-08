package com.erns.coching.common.type;

public enum ApiResultError {

	NO_ERROR				("0000"
			, "OK"
			, "Ok"),

	NO_AUTH_ERROR				("401"
			, "권한이 없습니다."
			, "You don't have permission."),

	LOGIN_ERR_NOT_FOUND_USER 	("100404"
			, "사용자 아이디 혹은 비밀번호가 틀렸습니다."
			, "Invalid username or password."),
	LOGIN_ERR_NOT_MATCH_PASSWD	("100401"
			, "사용자 아이디 혹은 비밀번호가 틀렸습니다."
			, "Invalid username or password."),
	LOGIN_ERR_BLOCKED_USER 		("100403"
			, "사용 중지된 사용자 입니다."
			, "The user account has been suspended."),
	LOGIN_ERR_NOT_USED_USER 	("100405"
			, "사용 할 수 없는 계정 입니다."
			, "The account is not available for use."),
	LOGIN_ERR_PASS_BLOCKED 		("100406"
			, "비밀번호 5회 오류로 사용중지된 계정입니다."
			, "The account has been suspended due to 5 consecutive password errors."),
	LOGIN_ERR_PASS_MUST_CHANAGE	("100407"
			, "비밀번호 재설정이 필요합니다.\n재설정 후 다시 로그인 하십시오."
			, "Password reset is required.\nAfter resetting, please log in again."),
	LOGIN_ERR_CANNOT_CHANAGE_INIT_PASS	("100407"
			, "인증되지 않은 계정입니다."
			, "The account is not authenticated."),
	LOGIN_ERR_OLD_PASS_EQUALS	("100408"
			, "비밀번호가 이전비밀번호와 동일합니다.\n다른 비밀번호를 입력해 주십시오."
			, "The password is the same as the previous one.\nPlease enter a different password."),
	LOGIN_ERR_DORMANT	 		("100409"
			, "휴면회원입니다."
			, "The account is in dormant status."),
	LOGIN_ERR_NOT_FOUND_USER_ID	("100410"
			, "이메일이 일치하지 않습니다."
			, "The email does not match."),
	LOGIN_ERR_NOT_VERIFICATION_EMAIL("100411"
			, "이메일 인증이 완료되지 않은 계정입니다."
			, "Email verification has not been completed for the account."),
	LOGIN_ERR_WHITDRAW_USER		("100412"
			, "탈회한 계정입니다."
			, "This account has been withdrawn."),
	LOGIN_ERR_NOT_FOUND_EMAIL ("100420"
			, "일치하는 이메일이  없습니다."
			, "No matching email found."),
	LOGIN_ERR_REQUIRED			("100500"
			, "로그인이 필요합니다."
			, "Login is required."),
	LOGIN_ERR_UNUSED			("100600"
			, "미사용 처리된 계정입니다."
			, "This account has been marked as unused."),

	MEMB_ERR_ADMIN_MEMB_ADD		("900001"
			, "관리자계정 생성에 실패했습니다."
			, "Failed to create administrator account."),
	ATTACH_ERROR				("910001"
			, "첨부파일 관련 시스템 오류가 발생하였습니다."
			, "A system error related to attachments has occurred."),

	BOARD_ERR_ADD_MST			("200101"
			, "게시판마스터 생성에 실패했습니다."
			, "Failed to create the board master."),
	BOARD_ERR_UDT_MST			("200102"
			, "게시판마스터 수정에 실패했습니다."
			, "Failed to modify the board master."),



	ERROR_INVALID_LOGIN			("9201"
			, "로그인정보가 유효하지 않습니다."
			, "Invalid login credentials."),

	BAD_REQUEST					("9400"
			, "잘못된 요청입니다."
			, "Invalid request."),
	NO_AUTH_TOKEN_EXPIRED		("9401"
			, "토큰이 만료되었습니다."
			, "The token has expired."),
	NO_AUTH_TOKEN_INVALID		("9402"
			, "토큰정보가 유효하지 않습니다."
			, "The token information is invalid."),
	ERROR_ACCESS_DENIED			("9403"
			, "실행 권한이 없습니다."
			, "No execution permission."),
	NO_AUTH_TOKEN_BLACKLIST		("9403"
			, "차단된 토큰입니다."
			, "Blocked token."),
	NO_AUTH_REFRESH_TOKEN_INVALID("9403"
			, "토큰정보가 유효하지 않습니다."
			, "Invalid token information."),
	NO_AUTH_PERMISSION_DENIED	("9403"
			, "사용 권한이 없습니다."
			, "No authorization to use."),
	//ERROR_NO_JWT				("9401" , "토큰값이 누락되었습니다."),
	ERROR_INVALID_JWT			("9403"
			, "토큰값이 유효하지 않습니다."
			, "The token value is not valid."),
	ERROR_ERROR_JWT				("9403"
			, "토큰값이 유효하지 않습니다."
			, "The token value is not valid."),
	ERROR_EXPIRED_JWT			("9403"
			, "토큰값이 만료되었습니다."
			, "The token value has expired."),
	ERROR_INVALID_ISS			("9403"
			, "토큰값의 ISS가 유효하지 않습니다."
			, "The ISS of the token value is not valid."),
	ERROR_INVALID_NO_JTI		("9403"
			, "토큰값의 Jti 고유식별자가 누락되었습니다."
			, "The Jti of the token value is missing."),
	ERROR_INVALID_JTI			("9403"
			, "토큰값의 Jti 고유식별자가 유효하지 않습니다."
			, "The Jti of the token value is not valid."),


	NO_AUTH_NOT_FOUND_USER		("9404"
			, "유효하지 않은 사용자 정보입니다."
			, "Invalid user information."),


	ERROR_SERVER_ERROR			("9500"
			, "서버 오류가 발생하였습니다."
			, "A server error has occurred."),
	ERROR_CALL_API				("9801"
			, "API 호출오류."
			, "API call error."),

	ERROR_PARAMETERS			("9901"
			, "파라미터 오류."
			, "Parameter error."),
	ERROR_NOT_SUPPORTED_METHOD	("9902"
			, "지원하지 않는 Method 입니다."
			, "Unsupported method."),
	ERROR_INTERNAL_API_PARAMETERS("9903"
			, "내부 API 파라미터 오류."
			, "Internal API parameter error."),


	JOIN_ERR					("20000"
			, "회원가입중 오류가 발생했습니다."
			, "An error occurred during the registration process."),
	JOIN_ERR_EMAIL_OVERLAP			("20001"
			, "이미 가입된 이메일입니다."
			, "The Email is already registered."),
	JOIN_ERR_ID_OVERLAP			("20002"
			, "사용할 수 없는 아이디입니다."
			, "The username is not available."),
	JOIN_ERR_TERMS				("20003"
			, "필수 약관에 동의가 필요합니다."
			, "Agreement to the mandatory terms is required."),
	JOIN_ERR_NO_TERMS			("20004"
			, "약관 동의가 누락되었습니다."
			, "Agreement to the terms is missing."),
	JOIN_ERR_ID_INVALID			("20005"
			, "사용할 수 없는 아이디입니다."
			, "The username is not available."),
	ERROR_REG_COMP_NO			("20006"
			, "미등록 기업입니다."
			, "The company is not registered."),
	ERROR_REG_USER_OVERLAP		("20007"
			, "기등록 고객입니다."
			, "The customer is already registered."),
	ERROR_REG_USER_NO			("20008"
			, "미등록 고객입니다."
			, "The customer is not registered."),
	JOIN_ERR_NOT_MATCH_PASSWD	("20009"
			, "현재 비밀번호가 일치하지 않습니다."
			, "The current password does not match."),

	ERROR_MATCH_ACCOUNT_HOLDER ("20010"
			, "일치하는 예금주가 없습니다."
			, "No matching account holder found."),

	ERROR_MODIFY_ACCOUNT ("20011"
			, "진행 계약이 있을 시 계좌변경은 불가합니다."
			, "Account changes are not possible while a contract is in progress."),

	JOIN_ERR_REJOIN1			("20901"
			, "탈회한 회원입니다."
			, "The member has withdrawn from the membership."),

	WITHDRAW_ERR_INVALID_TOKEN	("21001"
			, "탈회정보가 올바르지 않습니다."
			, "Withdrawal information is not correct."),


	//가입정보
	ERROR_INVALID_JOIN_INFO		("9301"
			, "회원가입정보가 유효하지 않습니다."
			, "The registration information is not valid."),

	ERROR_INVALID_BANK_JOIN_INFO ("19403"
			, "연동정보가 유효하지 않습니다."
			, "The integration information is not valid."),


	ERROR_DEFAULT				("9999"
			, "오류가 발생하였습니다."
			, "An error has occurred."),


	RAW_ERR_NOT_MANAGER ("30001"
			, "해당 원료 담당자가 아닙니다."
			, "Not in charge of the raw material."),

	RAW_ERR_NOT_EXCEL_DATA ("30002"
			, "업로드된 파일이 없습니다."
			, "There are no files uploaded."),

	RAW_ERR_EXCEL_ROW_LIMIT ("30003"
			, "10000줄 이하로 업로드 해주세요."
			, "Please upload up to 10000 lines."),

	;

	private String code;
	private String messageKo;
	private String messageEn;

	private ApiResultError(String code, String messageKo, String messageEn) {
		this.code = code;
		this.messageKo = messageKo;
		this.messageEn = messageEn;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return messageKo;
	}

	public String getMessage(LocaleType localeType) {
		if(localeType != null) {
			if (LocaleType.EN.equals(localeType)) {
				return messageEn;
			} else {
				return messageKo;
			}
		}

		return messageKo;
	}

	public static ApiResultError findByCode(String code) {
		for(ApiResultError e : values()) {
			if(e.code.equals(code)) return e;
		}
		return null;
	}

	public static boolean isOk(String code) {
		return ApiResultError.NO_ERROR.code.equals(code);
	}

}
