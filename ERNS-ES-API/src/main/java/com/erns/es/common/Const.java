package com.erns.es.common;

/**
 *
 * <p>기본적으로 사용되는 상수값을 지정</p>
 *
 * @author Hunwoo Park
 *
 */
public class Const {

	//페이징 처리 기본 값
	public static final int DEFAULT_PAGESIZE = 10;
    public static final int DEFAULT_ROWSIZE = 10;

    public static final String MB_ERROR_CODE = "errorCode";

    //HTML Resource Ver.
    public static final String HTML_RES_VER = "20211124"; //Jsp에서 css,js 로드시 version값 : 브라우저캐시 문제 회피용

    public static final String APP_PREFIX = "BPBW"; //BPSP BackOfiice Web

    public static final int ADMIN_PASSWD_ERROR_MAX_COUNT = 5;


    //Auth
    public static final String KEY_AUTH_TOKEN = "Authorization";
    public static final String KEY_COOKIE_AUTH_TOKEN = "AuthT";
    public static final String AUTH_TOKEN_PREFIX = "Bearer ";
    public static final String AUTH_TOKEN_ACCESS = "accessToken";
    public static final String AUTH_TOKEN_REFRESH = "refreshToken";

    public static final boolean DELETE_PHYSICAL_FILE = false;

    //Short URL
    public static final String SHORT_URL_BASE_URI = "/common/su/tu";
    public static final String SEC_SHORT_URLTOKEN = "short_url_token";
    public static final String SEC_SHORT_URL_ID = "short_url_id";
}
