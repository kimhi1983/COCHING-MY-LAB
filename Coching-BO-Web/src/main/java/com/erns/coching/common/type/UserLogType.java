package com.erns.coching.common.type;

import com.fasterxml.jackson.annotation.JsonFormat;

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
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum UserLogType {
	
	DEFAULT				 						("000_000000", true, "[unknown]"),

	LOGIN 										("001_000001", true, "로그인"),
	LOGOUT 										("001_000002", true, "로그아웃"),
	LOGIN_REFRESH_TOKEN 			("001_000003", true, "Refresh Token"),
	LOGIN_ACCESS_TOKEN 				("001_000004", true, "Access Token"),
	LOGIN_GET_USER_ID 				("001_000050", true, "사용자 ID 검색"),
	LOGIN_REQ_RESET_PASSWD 		("001_000060", true, "비밀번호 변경 요청"),
	LOGIN_RESET_PASSWD 				("001_000070", true, "비밀번호 변경"),

	//회원관리
	MEMBER_MY_INFO						("002_000011", true, "사용자 정보 로드"),
	MEMBER_PARTNER_USER_LIST	("002_000015", true, "파트너 사용자 목록 검색"),
	MEMBER_COMPANY_INFO				("002_000016", true, "기업 정보 로드"),
	MEMBER_WITHDRAW_INFO			("002_000017", true, "탈회 정보 로드"),
	
	MEMBER_MY_INFO_SET				("002_000030", true, "사용자 정보 수정"),
	MEMBER_PARTNER_INFO_SET		("002_000031", true, "파트너 정보 수정"),	
	MEMBER_PW_CHNG_DTTM_RESET	("002_000033", true, "비밀번호 변경일 초기화"),
	MEMBER_USEYN_SET					("002_000033", true, "사용여부 설정"),
	MEMBER_PW_INIT						("002_000034", true, "비밀번호 초기화"),
	MEMBER_PW_CHANGE					("002_000035", true, "비밀번호 변경"),
	MEMBER_DELETE							("002_000040", true, "사용자 삭제"),
	MEMBER_WITHDRAW						("002_000041", true, "탈회 처리"),

	MEMBER_USER_LIST					("002_001011", true, "사용자 목록 조회"),
	MEMBER_USER_GET						("002_001012", true, "사용자 조회"),
	MEMBER_USER_ADD						("002_001020", true, "사용자 생성"),
	MEMBER_USER_UDT						("002_001030", true, "사용자 수정"),
	MEMBER_USER_UDT_STATE				("002_001034", true, "사용자 상태 수정"),
	MEMBER_USER_DEL						("002_001040", true, "사용자 삭제"),

	MEMBER_EMULATOR						("002_001090", true, "사용자 에뮬레이터"),


	//사용자/파트너
	JOIN_USER									("003_000021", true, "사용자 회원가입"),
	JOIN_PARTNER							("003_000022", true, "파트너사 회원가입"),
	JOIN_CHK_DUPLICATE				("003_000028", true, "회원가입 중복 체크"),		

	//파트너사
	PARTNER_LIST							("004_000011", true, "파트너사 목록 검색"),
	PARTNER_GET								("004_000012", true, "파트너사 단건 검색"),
	PARTNER_NAME_LIST					("004_000013", true, "파트너사 NAME 목록 검색"),
	PARTNER_INFO							("004_000015", true, "파트너 정보 로드"),
	PARTNER_ADD								("004_000020", true, "파트너사 생성"),
	PARTNER_UDT								("004_000030", true, "파트너사 수정"),
	PARTNER_DEL								("004_000040", true, "파트너사 삭제"),

	

			
	//쪽지
	MESSAGE_LIST					("101_000011", true, "쪽지 목록 검색"),
	MESSAGE_GET						("101_000012", true, "쪽지 단건 검색"),
	MESSAGE_SEND					("101_000020", true, "쪽지 보내기"),
	MESSAGE_DEL						("101_000041", true, "쪽지 삭제"),

	//알림
	NOTIFICATION_LIST			("102_000011", true, "알림 목록 검색"),
	NOTIFICATION_CHK_YN		("102_000012", true, "알림 확인 여부 업데이트"),
	

	//검색관련
	ES_COCHING_TABLE_RESET	("200_900020", true, "코칭 원료 테이블 초기화"),

	SEARCH_INGREDIENTS			("201_100011", true, "성분 목록 검색"),
	SEARCH_INGREDIENT				("201_100012", true, "성분 단건 검색"),
	SEARCH_INGREDIENT_ALL		("201_100013", true, "전체 성분명 목록"),
	SEARCH_INGREDIENT_S_ALL	("201_100014", true, "전체 성분명 목록 JSON"),
	SEARCH_RAWS 						("201_101011", true, "원료 목록 검색"),
	SEARCH_RAW 							("201_101012", true, "원료 단건 검색"),
	SEARCH_PRODUCTS					("201_102021", true, "상품 목록 검색"),
	SEARCH_PRODUCT					("201_102022", true, "상품 단건 검색"),
	SEARCH_TVS 							("201_104011", true, "코칭TV 목록 검색"),
	SEARCH_TV 							("201_104012", true, "코칭TV 단건 검색"),

	ES_COCHING_RAW_LIST			("201_201011", true, "코칭 원료 DB 목록"),
	ES_COCHING_RAW_GET			("201_201012", true, "코칭 원료 DB 단건 검색"),
	ES_COCHING_RAW_INDICES	("201_201013", true, "코칭 원료 DB 인덱스 조회"),
	ES_COCHING_RAW_RESET_TABLE("201_201020", true, "코칭 원료 DB 테이블 초기화"),
	ES_COCHING_RAW_CREATE_INDEX("201_201021", true, "코칭 원료 DB 인덱스 생성"),

	ES_COCHING_TV_LIST			("201_401011", true, "코칭 TV DB 목록"),
	ES_COCHING_TV_GET				("201_401012", true, "코칭 TV DB 단건 검색"),
	ES_COCHING_TV_INDICES		("201_401013", true, "코칭 TV DB 인덱스 조회"),
	ES_COCHING_TV_RESET_TABLE("201_401020", true, "코칭 TV DB 테이블 초기화"),
	ES_COCHING_TV_CREATE_INDEX("201_401021", true, "코칭 TV DB 인덱스 생성"),

	ES_COCHING_HW_BRAND_RESET_TABLE("201_501020", true, "코칭 상품 브랜드 DB 테이블 초기화"),

	
	//찜하기
	FAVORITE_MY_LIST				("202_000011", true, "찜한 목록 검색"),
	FAVORITE_MY_TOGGLE			("202_000012", true, "찜하기"),
	FAVORITE_MY_DEL					("202_000013", true, "찜목록 삭제"),

	//최근본원료
	USER_RECENT_RAW_LIST		("203_000011", true, "최근 본 원료 목록 검색"),
	USER_RECENT_RAW_DEL			("203_000012", true, "최근 본 원료 삭제"),

	//검색관련
	RECOMMEND_KEYWORD_LIST	("204_000010", true, "추천 키워드 목록 검색"),
	RECOMMEND_KEYWORD_GET		("204_000012", true, "추천 키워드 단건 검색"),
	RECOMMEND_KEYWORD_ADD		("204_000020", true, "추천검색어 등록"),
	RECOMMEND_KEYWORD_UDT		("204_000030", true, "추천검색어 수정"),
	RECOMMEND_KEYWORD_UDT_ORDER("204_000031", true, "추천검색어 순서 수정"),
	RECOMMEND_KEYWORD_DEL		("204_000040", true, "추천검색어 삭제"),

	
	//원료 관련
	RAW_LIST								("210_000011", true, "원료 목록 검색"),
	RAW_GET									("210_000012", true, "원료 단건 검색"),
	RAW_DETAIL							("210_000013", true, "원료 자료 상세 검색"),
	RAW_ELM_LIST						("210_000013", true, "원료 성분 목록 검색"),
	RAW_DOC_LIST						("210_000015", true, "원료 서류 목록 검색"),
	RAW_MANAGER_LIST				("210_000016", true, "원료 담당자 목록 검색"),
	RAW_GARBAGE_LIST				("210_000017", true, "원료 삭제 목록 검색"),
	RAW_CM_TYPE_LIST				("210_000018", true, "원료 성분 구분 코드 목록"),

	RAW_ADD									("210_000020", true, "원료 등록"),
	RAW_ADD_DETAIL					("210_000021", true, "원료 상세 등록"),
	RAW_PREVIEW_UPLOAD_FILE	("210_000022", true, "원료 미리보기 자료 등록"),
	RAW_SET									("210_000030", true, "원료 수정"),
	RAW_SET_DETAIL					("210_000031", true, "원료 상세 수정"),
	RAW_SET_USE_YN					("210_000032", true, "원료 사용여부 수정"),
	RAW_SET_DEL_YN					("210_000033", true, "원료 삭제여부 수정"),
	RAW_SET_MANAGER					("210_000034", true, "원료 담당자 수정"),
	RAW_SET_STATE					("210_000035", true, "원료 상태 수정"),

	RAW_TYPE_LIST						("210_002011", true, "원료 구분 목록 검색"),
	RAW_TYPE_GET						("210_002012", true, "원료 타입 상세"),
	RAW_TYPE_CK_CODE				("210_002013", true, "원료 타입 코드 확인"),
	RAW_TYPE_ADD						("210_002020", true, "원료 타입 생성"),
	RAW_TYPE_UDT						("210_002030", true, "원료 타입 수정"),
	RAW_TYPE_UDT_STATE					("210_002034", true, "원료 타입 상태 수정"),
	RAW_TYPE_DEL						("210_002040", true, "원료 타입 삭제"),


	//게시판
	BOARD_MST_LIST		("801_001011", true, "게시판 마스터 목록 검색"),
	BOARD_MST_GET			("801_001012", true, "게시판 마스터 검색"),
	BOARD_MST_ADD			("801_001020", true, "게시판 마스터 생성"),
	BOARD_MST_UDT			("801_001030", true, "게시판 마스터 수정"),
	BOARD_MST_UDT_STATE		("801_001034", true, "게시판 마스터 상태 수정"),

	BOARD_LIST				("801_001011", true, "게시물 목록 검색"),
	BOARD_GET					("801_001012", true, "게시물 검색"),
	BOARD_MY_LIST			("801_001015", true, "나의 게시물 목록 검색"),
	BOARD_INQR_LIST			("801_001016", true, "1:1문의 게시물 목록 검색"),
	BOARD_INQR_GET					("801_001017", true, "1:1문의 게시물 검색"),
	BOARD_ADD					("801_001020", true, "게시물 등록"),
	BOARD_INQR_ADD			("801_001021", true, "1:1문의 게시물 등록"),
	BOARD_UPLOAD_FILE	("801_001022", true, "게시물 첨부파일 등록"),
	BOARD_SET					("801_001030", true, "게시물 수정"),
	BOARD_UDT_DEL_YN	("801_001031", true, "게시글 삭제여부 수정"),
	BOARD_INQR_SET			("801_001032", true, "1:1문의 게시물 수정"),
	BOARD_INQR_UDT_DEL_YN	("801_001033", true, "1:1문의 게시글 삭제여부 수정"),
	BOARD_DEL					("801_001040", true, "게시물 삭제"),
	

	//게시물 코멘트
	BOARD_COMMENT_LIST("801_003011", true, "게시물 댓글 목록 검색"),
	BOARD_COMMENT_GET	("801_003012", true, "게시물 댓글 검색"),
	BOARD_COMMENT_ADD	("801_003020", true, "게시물 댓글 등록"),
	BOARD_COMMENT_SET	("801_003030", true, "게시물 댓글 수정"),
	

	//배너
	BANNER_MST_LIST		("802_001011", true, "배너 마스터 목록 검색"),
	BANNER_MST_GET		("802_001012", true, "배너 마스터 단건 검색"),
	BANNER_MST_UDT_STATE("802_001034", true, "배너 마스터 상태 수정"),

	BANNER_LIST			  ("802_002011", true, "배너 목록 검색"),
	BANNER_GET				("802_002012", true, "배너 단건 검색"),
	BANNER_ADD 				("802_002020", true, "배너 생성"),
	BANNER_UDT				("802_002030", true, "배너 수정"),
	BANNER_CLICK			("802_002032", true, "배너 클릭"),	
	BANNER_UDT_ORDER	("802_002033", true, "배너 순서 수정"),
	BANNER_UDT_STATE	("802_002034", true, "배너 상태 수정"),
	BANNER_UDT_DEL_YN	("802_002035", true, "배너 삭제 설정"),
	BANNER_DEL				("802_002040", true, "배너 삭제"),
	
	BANNER_AD_LIST		("802_102011", true, "광고 배너 목록 검색"),
	BANNER_AD_GET			("802_102012", true, "광고 배너 단건 검색"),
	BANNER_AD_CLICK		("802_102032", true, "광고 배너 클릭"),

	//팝업
	POPUP_MST_LIST		("803_001011", true, "팝업 목록 검색"),
	POPUP_MST_GET			("803_001012", true, "팝업 단건 검색"),
	POPUP_MST_UDT_STATE			("803_001034", true, "팝업 마스터 상태 수정"),

	POPUP_LIST				("803_002011", true, "팝업 목록 검색"),
	POPUP_GET					("803_002012", true, "팝업 단건 검색"),
	POPUP_ADD 				("803_002020", true, "팝업 생성"),
	POPUP_UDT					("803_002030", true, "팝업 수정"),
	POPUP_CLICK				("803_002032", true, "팝업 클릭"),
	POPUP_UDT_ORDER		("803_002033", true, "팝업 순서 수정"),
	POPUP_UDT_STATE		("803_002034", true, "팝업 상태 수정"),
	POPUP_UDT_DEL_YN	("803_002035", true, "팝업 삭제 설정"),
	POPUP_DEL					("803_002040", true, "팝업 삭제"),

	//이벤트
	EVENT_ADD 				("804_002020", true, "이벤트 생성"),
	EVENT_UDT 				("804_002030", true, "이벤트 수정"),
	EVENT_UDT_DEL			("804_002031", true, "이벤트 삭제여부 변경"),
	
	//코칭TV
	COCHINGTV_LIST			  ("805_001011", true, "코칭TV 목록 검색"),
	COCHINGTV_GET				("805_001012", true, "코칭TV 단건 검색"),
	COCHINGTV_YOUTUBE_GET				("805_001013", true, "코칭TV 유튜브 API 검색"),
	COCHINGTV_ADD 				("805_001020", true, "코칭TV 생성"),
	COCHINGTV_UDT				("805_001030", true, "코칭TV 수정"),
	COCHINGTV_UDT_ORDER	("805_001033", true, "코칭TV 순서 수정"),
	COCHINGTV_UDT_STATE	("805_001034", true, "코칭TV 상태 수정"),
	COCHINGTV_UDT_DEL_YN	("805_001035", true, "코칭TV 삭제 설정"),
	COCHINGTV_DEL				("805_001040", true, "코칭TV 삭제"),

	//약관
	TERMS_LIST				("880_002011", true, "약관 목록 검색"),
	TERMS_GET					("880_002012", true, "약관 상세 검색"),
	TERMS_ADD					("880_002020", true, "약관생성"),
	TERMS_UDT					("880_002030", true, "약관수정"),

	//기타
	COMMON_API_PARTNER_BASE_URL	("890_000011", true, "원료사 base url 검색"),	
	COMMON_API_USER_BASE_URL		("890_000012", true, "사용자 base url 검색"),	
	COMMON_API_EMAIL_LIST				("890_000013", true, "이메일 리스트 검색"),

	//관리자계정
	ADMIN_MEMBER_LIST			("900_001011", true, "관리자계정 목록"),
	ADMIN_MEMBER_GET			("900_001012", true, "관리자계정 조회"),
	ADMIN_MEMBER_CK_ID		("900_001013", true, "관리자계정 중복 조회"),
	ADMIN_MEMBER_ADD			("900_001020", true, "관리자계정 생성"),
	ADMIN_MEMBER_UDT			("900_001030", true, "관리자계정 수정"),
	ADMIN_MEMBER_UDT_STATE		("900_001034", true, "팝업 상태 수정"),
	ADMIN_MEMBER_DEL			("900_001040", true, "관리자계정 삭제"),

	//파일
	FILE_DOWNLOAD			("901_000011", true, "파일 다운로드"),
	FILE_DOWNLOAD_IMG	("901_000012", true, "파일 이미지 다운로드"),
	FILE_DOWNLOAD_EIMG("901_000013", true, "파일 이미지 다운로드"),
	FILE_UPLOAD				("901_000020", true, "파일 업로드"),
	FILE_PDF_IMAGE_INFO("901_000101",true, "PDF IMAGE 다운로드"),

	//다국어
	MLTLN_LIST				("902_000011", true, "다국어 설정 목록"),
	MLTLN_ITEM_GET		("902_000012", true, "다국어 설정 상세"),
	MLTLN_IS_HAVE_CODE("902_000013", true, "다국어 키 확인"),
	MLTLN_EXPORT			("902_000014", true, "다국어 export"),
	MLTLN_GET					("902_000019", true, "다국어 설정 로드"),
	MLTLN_ADD					("902_000020", true, "MLTLN 생성"),
	MLTLN_IMPORT			("902_000029", true, "MLTLN 업로드"),	
	MLTLN_UDT					("902_000030", true, "MLTLN 수정"),

	//사용자 로그
	USER_LOG_LIST			("903_000011", true, "사용자 로그 목록"),
	USER_LOG_GET			("903_000012", true, "사용자 로그 상세"),

	//RND
	RND_LAB_MASTER_LIST	("981_001011", true, "RND LAB 마스터 목록 검색"),
	RND_LAB_MASTER_GET	("981_001012", true, "RND LAB 마스터 상세 검색"),
	RND_LAB_MASTER_ADD	("981_001020", true, "RND LAB 마스터 생성"),
	RND_LAB_MASTER_UDT	("981_001030", true, "RND LAB 마스터 수정"),
	RND_LAB_MASTER_DEL	("981_001040", true, "RND LAB 마스터 삭제"),
	RND_AI_LAB_RES_LIST	("981_001013", true, "RND AI 결과 목록 검색"),
	RND_AI_PRSC_RESULT_V1	("981_001010", true, "RND AI 처방 결과 검색"),

	//공통코드
	CODE_MST_LIST			("990_001011", true, "공통코드 그룹 목록 검색"),
	CODE_MST_GET			("990_001012", true, "공통코드 그룹 검색"),
	CODE_MST_UDT_STATE		("990_001034", true, "공통코드 상태 수정"),
	
	CODE_LIST					("990_002011", true, "공통코드 검색"),
	CODE_ENUM					("990_002012", true, "공통코드 열거형 검색"),
	CODE_ADDRESS			("990_002013", true, "주소 검색"),
	CODE_RAW_CMTYPE		("990_002014", true, "성분 구분 코드 검색"),
	CODE_UDT_ORDER		("990_002033", true, "코드 순서 수정"),
	CODE_UDT_STATE		("990_002034", true, "코드 상태 수정"),

	//시스템 관련
	PROP_LIST							("991_001011", true, "시스템설정 목록 검색"),
	PROP_GET							("991_001012", true, "시스템설정 단건 검색"),	
	PROP_ADD							("991_001020", true, "시스템설정 생성"),
	PROP_UDT							("991_002030", true, "시스템설정 갱신"),
	PROP_DEL							("991_002040", true, "시스템설정 삭제"),

	;
	
	private String code;
	private boolean logging;
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
