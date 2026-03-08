package com.erns.coching.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 사용자 로그
 * @author hw.park@erns.co.kr
 *
 * 사용자 로그 타입 정의
 *
 */
@Getter
@AllArgsConstructor
public enum UserLogType {

	DEFAULT				 						("000_000000", "[unknown]"),

	LOGIN 										("001_000001", "로그인"),
	LOGOUT 										("001_000002", "로그아웃"),
	LOGIN_REFRESH_TOKEN 			("001_000003", "Refresh Token"),
	LOGIN_ACCESS_TOKEN 				("001_000004", "Access Token"),
	LOGIN_GET_USER_ID 				("001_000050", "사용자 ID 검색"),
	LOGIN_REQ_RESET_PASSWD 		("001_000060", "비밀번호 변경 요청"),
	LOGIN_RESET_PASSWD 				("001_000070", "비밀번호 변경"),

	//회원관리
	MEMBER_MY_INFO						("002_000011", "사용자 정보 로드"),
	MEMBER_PARTNER_USER_LIST	("002_000015", "파트너 사용자 목록 검색"),
	MEMBER_COMPANY_INFO				("002_000016", "기업 정보 로드"),
	MEMBER_WITHDRAW_INFO			("002_000017", "탈회 정보 로드"),

	MEMBER_MY_INFO_SET				("002_000030", "사용자 정보 수정"),
	MEMBER_PARTNER_INFO_SET		("002_000031", "파트너 정보 수정"),
	MEMBER_PW_CHNG_DTTM_RESET	("002_000033", "비밀번호 변경일 초기화"),
	MEMBER_USEYN_SET					("002_000033", "사용여부 설정"),
	MEMBER_PW_INIT						("002_000034", "비밀번호 초기화"),
	MEMBER_PW_CHANGE					("002_000035", "비밀번호 변경"),
	MEMBER_DELETE							("002_000040", "사용자 삭제"),
	MEMBER_WITHDRAW						("002_000041", "탈회 처리"),

	MEMBER_USER_LIST					("002_001011", "사용자 목록 조회"),
	MEMBER_USER_GET						("002_001012", "사용자 조회"),
	MEMBER_USER_ADD						("002_001020", "사용자 생성"),
	MEMBER_USER_UDT						("002_001030", "사용자 수정"),
	MEMBER_USER_DEL						("002_001040", "사용자 삭제"),

	MEMBER_EMULATOR						("002_001090", "사용자 에뮬레이터"),


	//사용자/파트너
	JOIN_USER									("003_000021", "사용자 회원가입"),
	JOIN_PARTNER							("003_000022", "파트너사 회원가입"),
	JOIN_CHK_DUPLICATE				("003_000028", "회원가입 중복 체크"),

	//파트너사
	PARTNER_LIST							("004_000011", "파트너사 목록 검색"),
	PARTNER_GET								("004_000012", "파트너사 단건 검색"),
	PARTNER_NAME_LIST					("004_000013", "파트너사 NAME 목록 검색"),
	PARTNER_INFO							("004_000015", "파트너 정보 로드"),
	PARTNER_ADD								("004_000020", "파트너사 생성"),
	PARTNER_UDT								("004_000030", "파트너사 수정"),
	PARTNER_DEL								("004_000040", "파트너사 삭제"),




	//쪽지
	MESSAGE_LIST					("101_000011", "쪽지 목록 검색"),
	MESSAGE_GET						("101_000012", "쪽지 단건 검색"),
	MESSAGE_SEND					("101_000020", "쪽지 보내기"),
	MESSAGE_DEL						("101_000041", "쪽지 삭제"),

	//알림
	NOTIFICATION_LIST			("102_000011", "알림 목록 검색"),
	NOTIFICATION_CHK_YN		("102_000012", "알림 확인 여부 업데이트"),


	//검색관련
	ES_COCHING_TABLE_RESET	("200_900020", "코칭 원료 테이블 초기화"),

	SEARCH_INGREDIENTS			("201_100011", "성분 목록 검색"),
	SEARCH_INGREDIENT				("201_100012", "성분 단건 검색"),
	SEARCH_INGREDIENT_ALL		("201_100013", "전체 성분명 목록"),
	SEARCH_INGREDIENT_S_ALL	("201_100014", "전체 성분명 목록 JSON"),
	SEARCH_RAWS 						("201_101011", "원료 목록 검색"),
	SEARCH_RAW 							("201_101012", "원료 단건 검색"),
	SEARCH_PRODUCTS					("201_102021", "상품 목록 검색"),
	SEARCH_PRODUCT					("201_102022", "상품 단건 검색"),

	ES_COCHING_RAW_LIST			("201_201011", "코칭 원료 DB 목록"),
	ES_COCHING_RAW_GET			("201_201012", "코칭 원료 DB 단건 검색"),
	ES_COCHING_RAW_INDICES	("201_201013", "코칭 원료 DB 인덱스 조회"),
	ES_COCHING_RAW_RESET_TABLE("201_201020", "코칭 원료 DB 테이블 초기화"),
	ES_COCHING_RAW_CREATE_INDEX("201_201021", "코칭 원료 DB 인덱스 생성"),




	//찜하기
	FAVORITE_MY_LIST				("202_000011", "찜한 목록 검색"),
	FAVORITE_MY_TOGGLE			("202_000012", "찜하기"),
	FAVORITE_MY_DEL					("202_000013", "찜목록 삭제"),

	//최근본원료
	USER_RECENT_RAW_LIST		("203_000011", "최근 본 원료 목록 검색"),
	USER_RECENT_RAW_DEL			("203_000012", "최근 본 원료 삭제"),

	//검색관련
	RECOMMEND_KEYWORD_LIST	("204_000010", "추천 키워드 목록 검색"),
	RECOMMEND_KEYWORD_GET		("204_000012", "추천 키워드 단건 검색"),
	RECOMMEND_KEYWORD_ADD		("204_000020", "추천검색어 등록"),
	RECOMMEND_KEYWORD_UDT		("204_000030", "추천검색어 수정"),
	RECOMMEND_KEYWORD_UDT_ORDER("204_000031", "추천검색어 순서 수정"),
	RECOMMEND_KEYWORD_DEL		("204_000040", "추천검색어 삭제"),


	//원료 관련
	RAW_LIST								("210_000011", "원료 목록 검색"),
	RAW_GET									("210_000012", "원료 단건 검색"),
	RAW_DETAIL							("210_000013", "원료 자료 상세 검색"),
	RAW_ELM_LIST						("210_000013", "원료 성분 목록 검색"),
	RAW_DOC_LIST						("210_000015", "원료 서류 목록 검색"),
	RAW_MANAGER_LIST				("210_000016", "원료 담당자 목록 검색"),
	RAW_GARBAGE_LIST				("210_000017", "원료 삭제 목록 검색"),
	RAW_CM_TYPE_LIST				("210_000018", "원료 성분 구분 코드 목록"),
	RAW_EX_THUMB_LIST				("210_000019", "원료 썸네일 예시 이미지 목록"),
	RAW_PARTNER_THUMB_LIST				("210_000020", "원료 파트너 썸네일 이미지 목록"),
	RAW_PARTNER_THUMB_ADD				("210_000021", "원료 파트너 썸네일 이미지 등록"),
	RAW_PARTNER_THUMB_DEL				("210_000022", "원료 파트너 썸네일 이미지 삭제"),

	RAW_ADD									("210_000020", "원료 등록"),
	RAW_ADD_DETAIL					("210_000021", "원료 상세 등록"),
	RAW_PREVIEW_UPLOAD_FILE	("210_000022", "원료 미리보기 자료 등록"),
	RAW_EXCEL_UPLOAD						("210_000023", "원료 엑셀 업로드"),
	RAW_EXCEL_RESULT_DOWNLOAD			("210_000024", "원료 엑셀 결과 다운로드"),
	RAW_SET									("210_000030", "원료 수정"),
	RAW_SET_DETAIL					("210_000031", "원료 상세 수정"),
	RAW_SET_USE_YN					("210_000032", "원료 사용여부 수정"),
	RAW_SET_DEL_YN					("210_000033", "원료 삭제여부 수정"),
	RAW_SET_MANAGER					("210_000034", "원료 담당자 수정"),

	RAW_TYPE_LIST						("210_002011", "원료 구분 목록 검색"),
	RAW_TYPE_GET						("210_002012", "원료 타입 상세"),
	RAW_TYPE_CK_CODE				("210_002013", "원료 타입 코드 확인"),
	RAW_TYPE_ADD						("210_002020", "원료 타입 생성"),
	RAW_TYPE_UDT						("210_002030", "원료 타입 수정"),
	RAW_TYPE_DEL						("210_002040", "원료 타입 삭제"),


	//게시판
	BOARD_MST_LIST		("801_001011", "게시판 마스터 목록 검색"),
	BOARD_MST_GET			("801_001012", "게시판 마스터 검색"),
	BOARD_MST_ADD			("801_001020", "게시판 마스터 생성"),
	BOARD_MST_UDT			("801_001030", "게시판 마스터 수정"),

	BOARD_LIST				("801_001011", "게시물 목록 검색"),
	BOARD_GET					("801_001012", "게시물 검색"),
	BOARD_MY_LIST			("801_001015", "나의 게시물 목록 검색"),
	BOARD_ADD					("801_001020", "게시물 등록"),
	BOARD_SET					("801_001030", "게시물 수정"),
	BOARD_UDT_DEL_YN	("801_001031", "게시글 삭제여부 수정"),
	BOARD_DEL					("801_001040", "게시물 삭제"),

	//게시물 코멘트
	BOARD_COMMENT_LIST("801_003011", "게시물 댓글 목록 검색"),
	BOARD_COMMENT_GET	("801_003012", "게시물 댓글 검색"),
	BOARD_COMMENT_ADD	("801_003020", "게시물 댓글 등록"),
	BOARD_COMMENT_SET	("801_003030", "게시물 댓글 수정"),


	//배너
	BANNER_MST_LIST		("802_001011", "배너 마스터 목록 검색"),
	BANNER_MST_GET		("802_001012", "배너 마스터 단건 검색"),

	BANNER_LIST			  ("802_002011", "배너 목록 검색"),
	BANNER_GET				("802_002012", "배너 단건 검색"),
	BANNER_ADD 				("802_002020", "배너 생성"),
	BANNER_UDT				("802_002030", "배너 수정"),
	BANNER_CLICK			("802_002032", "배너 클릭"),
	BANNER_UDT_ORDER	("802_002033", "배너 순서 수정"),
	BANNER_UDT_STATE	("802_002034", "배너 상태 수정"),
	BANNER_UDT_DEL_YN	("802_002035", "배너 삭제 설정"),
	BANNER_DEL				("802_002040", "배너 삭제"),

	BANNER_AD_LIST		("802_102011", "광고 배너 목록 검색"),
	BANNER_AD_GET			("802_102012", "광고 배너 단건 검색"),
	BANNER_AD_CLICK		("802_102032", "광고 배너 클릭"),

	//팝업
	POPUP_MST_LIST		("803_001011", "팝업 목록 검색"),
	POPUP_MST_GET			("803_001012", "팝업 단건 검색"),

	POPUP_LIST				("803_002011", "팝업 목록 검색"),
	POPUP_GET					("803_002012", "팝업 단건 검색"),
	POPUP_ADD 				("803_002020", "팝업 생성"),
	POPUP_UDT					("803_002030", "팝업 수정"),
	POPUP_CLICK				("803_002032", "팝업 클릭"),
	POPUP_UDT_ORDER		("803_002033", "팝업 순서 수정"),
	POPUP_UDT_STATE		("803_002034", "팝업 상태 수정"),
	POPUP_UDT_DEL_YN	("803_002035", "팝업 삭제 설정"),
	POPUP_DEL					("803_002040", "팝업 삭제"),

	//이벤트
	EVENT_ADD 				("804_002020", "이벤트 생성"),
	EVENT_UDT 				("804_002030", "이벤트 수정"),
	EVENT_UDT_DEL			("804_002031", "이벤트 삭제여부 변경"),

	//약관
	TERMS_LIST				("880_002011", "약관 목록 검색"),
	TERMS_GET					("880_002012", "약관 상세 검색"),
	TERMS_ADD					("880_002020", "약관생성"),
	TERMS_UDT					("880_002030", "약관수정"),

	//기타
	COMMON_API_PARTNER_BASE_URL	("890_000011", "원료사 base url 검색"),
	COMMON_API_USER_BASE_URL		("890_000012", "사용자 base url 검색"),
	COMMON_API_EMAIL_LIST				("890_000013", "이메일 리스트 검색"),

	//관리자계정
	ADMIN_MEMBER_LIST			("900_001011", "관리자계정 목록"),
	ADMIN_MEMBER_GET			("900_001012", "관리자계정 조회"),
	ADMIN_MEMBER_CK_ID		("900_001013", "관리자계정 중복 조회"),
	ADMIN_MEMBER_ADD			("900_001020", "관리자계정 생성"),
	ADMIN_MEMBER_UDT			("900_001030", "관리자계정 수정"),
	ADMIN_MEMBER_DEL			("900_001040", "관리자계정 삭제"),

	//파일
	FILE_DOWNLOAD			("901_000011", "파일 다운로드"),
	FILE_DOWNLOAD_IMG	("901_000012", "파일 이미지 다운로드"),
	FILE_DOWNLOAD_EIMG("901_000013", "파일 이미지 다운로드"),
	FILE_UPLOAD				("901_000020", "파일 업로드"),
	FILE_PDF_IMAGE_INFO("901_000101", "PDF IMAGE 다운로드"),

	//다국어
	MLTLN_LIST				("902_000011", "다국어 설정 목록"),
	MLTLN_ITEM_GET		("902_000012", "다국어 설정 상세"),
	MLTLN_IS_HAVE_CODE("902_000013", "다국어 키 확인"),
	MLTLN_EXPORT			("902_000014", "다국어 export"),
	MLTLN_GET					("902_000019", "다국어 설정 로드"),
	MLTLN_ADD					("902_000020", "MLTLN 생성"),
	MLTLN_IMPORT			("902_000029", "MLTLN 업로드"),
	MLTLN_UDT					("902_000030", "MLTLN 수정"),

	//사용자 로그
	USER_LOG_LIST			("903_000011", "사용자 로그 목록"),
	USER_LOG_GET			("903_000012", "사용자 로그 상세"),

	//공통코드
	CODE_MST_LIST			("990_001011", "공통코드 그룹 목록 검색"),
	CODE_MST_GET			("990_001012", "공통코드 그룹 검색"),

	CODE_LIST					("990_002011", "공통코드 검색"),
	CODE_ENUM					("990_002012", "공통코드 열거형 검색"),
	CODE_ADDRESS			("990_002013", "주소 검색"),
	CODE_RAW_CMTYPE		("990_002014", "성분 구분 코드 검색"),

	//시스템 관련
	PROP_LIST							("991_001011"	, "시스템설정 목록 검색"),
	PROP_GET							("991_001012"	, "시스템설정 단건 검색"),
	PROP_ADD							("991_001020"	, "시스템설정 생성"),
	PROP_UDT							("991_002030"	, "시스템설정 갱신"),
	PROP_DEL							("991_002040"	, "시스템설정 삭제"),

	;

	private String code;
	private String nameKo;

	public static UserLogType find(String name) {
		for(UserLogType e : values()) {
			if(e.name().equals(name)) return e;
		}
		return null;
	}

	public static UserLogType findByCode(String code) {
		for(UserLogType e : values()) {
			if(e.code.equals(code)) return e;
		}
		return null;
	}
}
