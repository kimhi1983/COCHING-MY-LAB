package com.erns.coching.raw.domain;

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * <p>원료 구분 정보 VO
 * T_RAW_DOC</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class RawTypeVO {

	protected long rawSeq;			//원료 SEQ
	protected String grpCode;		//그룹코드
	protected String code;			//코드
	protected long rgtr;			//등록자
	protected Date rgtDttm;			//등록일

}
