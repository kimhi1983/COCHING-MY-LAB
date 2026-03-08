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
 * <p>원료 서류 VO
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
public class RawDocVO {

	protected long rawSeq;			//원료 SEQ
	protected String docCode;		//서류코드
	protected String docFileId;		//파일 ID
	protected long rgtr;			//등록자
	protected Date rgtDttm;			//등록일


}
