package com.erns.coching.raw.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 *
 * <p>원료 자료 설정 DTO</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RawDocSetDTO {

	protected long rawSeq;			//원료 SEQ
	protected String docCode;		//서류코드
	protected String docFileId;		//파일 ID
	protected long rgtr;			//등록자
	protected Date rgtDttm;			//등록일

}
