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
	LOGIN_ERR_NOT_FOUND_DATA 	("100402"
			, "이메일주소 또는 아이디가 일치하지 않습니다. 다시 한번 확인해주세요."
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
			, "이미 가입된 아이디입니다."
			, "The username is already registered."),
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

	ERROR_REG_COMP_OVERLAP		("20010"
			, "사업자 등록번호가 중복입니다."
			, "The business registration number is duplicate."),

	ERROR_MODIFY_ACCOUNT ("20011"
			, "진행 계약이 있을 시 계좌변경은 불가합니다."
			, "Account changes are not possible while a contract is in progress."),

	JOIN_ERR_REJOIN1			("20901"
			, "탈회한 회원입니다."
			, "The member has withdrawn from the membership."),

	WITHDRAW_ERR_INVALID_TOKEN	("21001"
			, "탈회정보가 올바르지 않습니다."
			, "Withdrawal information is not correct."),



	//Contract
	ERROR_EXIST_CONTRACT ("22001"
			, "생성한 계약이 있는 회원입니다."
			, "The member has an existing contract."),

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







	CARD_SUCCESS				("3001"
			, "카드 결제 성공"
			, "Card payment success"),

	CARD_NUM_ERR				("3011"
			, "카드번호 오류"
			, "Card number error"),

	CARD_MERCHANT_UNVERIFIED	("3012"
			, "카드가맹점 정보 미확인"
			, "Card merchant information not verified"),

	CARD_MERCHANT_NOT_OPEN		("3013"
			, "카드 가맹점 개시 안됨"
			, "Card merchant not open"),

    CARD_MERCHANT_INFO_ERR		("3014"
    		, "카드가맹점 정보 오류"
    		, "Card merchant information error"),

    CARD_EXPIRY_DATE_ERR		("3021"
    		, "유효기간 오류"
    		, "Expiry date error"),

    CARD_INSTALLMENT_ERR		("3022"
    		, "할부개월 오류"
    		, "Installment months error"),

    CARD_INSTALLMENT_LIMIT		("3023"
    		, "할부개월 한도 초과"
    		, "Exceed installment limit"),

    CARD_NO_INTEREST			("3031"
    		, "무이자할부 카드 아님"
    		, "No interest installment card"),

    CARD_INVALID_INTEREST		("3032"
    		, "무이자할부 불가 개월수"
    		, "Invalid interest installment months"),

    CARD_NO_INTEREST_MERCHANT	("3033"
    		, "무이자할부 가맹점 아님"
    		, "No interest installment merchant"),

    CARD_NO_INTEREST_NOT_SET	("3034"
    		, "무이자할부 구분 미설정"
    		, "No interest installment category not set"),

    CARD_AMOUNT_ERR				("3041"
    		, "금액 오류(1000원 미만 신용카드 승인 불가)"
    		, "Amount error (Credit card approval not allowed for less than 1000 won)"),

    CARD_FOREIGN_NOT_REGISTERED_MERCHANT("3051"
    		, "해외카드 미등록 가맹점"
    		, "Foreign card not registered merchant"),

    CARD_CURRENCY_CODE_ERR		("3052"
    		, "통화코드 오류"
    		, "Currency code error"),

    CARD_UNCONFIRMED_FOREIGN	("3053"
    		, "확인 불가 해외카드"
    		, "Unconfirmed foreign card"),

    CARD_EXCHANGE_RATE_ERR		("3054"
    		, "환률전환오류"
    		, "Exchange rate conversion error"),

    CARD_USD_APPROVAL_NOT_ALLOWED("3055"
    		, "인증시 달러승인 불가"
    		, "USD approval not allowed during authentication"),

    CARD_DOMESTIC_NOT_ALLOWED_USD("3056"
    		, "국내카드 달러승인불가"
    		, "Domestic card USD approval not allowed"),

    CARD_UNCONFIRMED			("3057"
    		, "인증 불가카드"
    		, "Unconfirmed card"),

    CARD_KM_SAFE_MERCHANT("3061"
    		, "국민카드 인터넷안전결제 적용 가맹점"
    		, "KB Card Internet Safe Payment applicable merchant"),

    CARD_APPROVAL_NUMBER_ERR	("3062"
    		, "신용카드 승인번호 오류"
    		, "Credit card approval number error"),

    CARD_NOT_MERCHANT_ACQUIRING	("3071"
    		, "매입요청 가맹점 아님"
    		, "Not a merchant acquiring request"),

    CARD_TID_MISMATCH			("3072"
    		, "매입요청 TID 정보 불일치"
    		, "Acquiring request TID information mismatch"),

    CARD_PREVIOUS_ACQUIRING		("3073"
    		, "기매입 거래"
    		, "Previous acquiring transaction"),

    CARD_BALANCE_ERR			("3081"
    		, "카드 잔액 값 오류"
    		, "Card balance value error"),

    CARD_AFFILIATE_NOT_USABLE	("3091"
    		, "제휴카드 사용불가 가맹점"
    		, "Affiliate card not usable merchant"),

    CARD_FAILURE_RESPONSE		("3095"
    		, "카드사 실패 응답"
    		, "Card issuer failure response")

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
