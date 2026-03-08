package com.erns.es.common.type;

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

	LOGIN 					("001_000001", "로그인"),
	LOGOUT 					("001_000002", "로그아웃"),


	ADMIN_MEMBER_SELECT		("999_000001", "테스트111"),
	ADMIN_MEMBER_ADD		("999_000010", "관리자계정 생성"),
	ADMIN_MEMBER_UDT		("999_000011", "관리자계정 수정"),
	ADMIN_MEMBER_DEL		("999_000012", "관리자계정 삭제"),

	BOARD_MST_ADD			("910_000001", "게시판마스터 생성"),
	BOARD_MST_UDT			("910_000001", "게시판마스터 수정"),

	BOARD_ADD				("910_000101", "게시글 생성"),
	BOARD_UDT				("910_000102", "게시글 수정"),
	BOARD_UDT_DEL_YN		("910_000103", "게시글 삭제여부 수정"),

	TERMS_ADD				("911_000001", "약관생성"),
	TERMS_UDT				("911_000002", "약관수정"),

	EVENT_ADD 				("450_00001", "이벤트 생성"),
	EVENT_UDT 				("450_00002", "이벤트 수정"),
	EVENT_UDT_DEL			("450_00003", "이벤트 삭제여부 변경"),

	BANNER_ADD 				("460_00001", "배너 생성"),
	BANNER_UDT				("460_00002", "배너 수정"),
	BANNER_UDT_ORDER		("460_00003", "배너 순서 수정"),
	BANNER_UDT_DEL_YN		("460_00004", "배너 삭제"),

	POPUP_ADD 				("470_00001", "팝업 생성"),
	POPUP_UDT				("470_00002", "팝업 수정"),
	POPUP_UDT_ORDER			("470_00003", "팝업 순서 수정"),
	POPUP_UDT_DEL_YN		("470_00004", "팝업 삭제"),

	BIN_ADD					("480_00001", "BIN 생성"),
	BIN_UDT					("480_00002", "BIN 수정"),

	MID_ADD					("490_00001", "MID 생성"),
	MID_UDT					("490_00002", "MID 수정"),

	MLTLN_ADD					("500_00001", "MLTLN 생성"),
	MLTLN_UDT					("500_00002", "MLTLN 수정"),

	VERSION_ADD				("930_00001"	, "버전 생성"),
	VERSION_UDT				("930_00002"	, "버전 수정"),

	TAX_UDT					("940_00001"	, "누진세율 수정"),

	PROP_UDT				("950_00001"	, "시스템설정 갱신"),
	;

	private String code;
	private String nameKo;

	public static UserLogType find(String name) {
		for(UserLogType e : UserLogType.values()) {
			if(e.name().equals(name)) return e;
		}
		return null;
	}

	public static UserLogType findByCode(String code) {
		for(UserLogType e : UserLogType.values()) {
			if(e.code.equals(code)) return e;
		}
		return null;
	}
}
