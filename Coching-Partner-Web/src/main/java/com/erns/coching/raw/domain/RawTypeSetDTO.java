package com.erns.coching.raw.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 *
 * <p>원료 구분 설정 DTO</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RawTypeSetDTO {

	protected long rawSeq;			//원료 SEQ
	protected String grpCode;		//그룹코드
	protected String code;			//코드
	protected long rgtr;			//등록자
	protected Date rgtDttm;			//등록일

}
