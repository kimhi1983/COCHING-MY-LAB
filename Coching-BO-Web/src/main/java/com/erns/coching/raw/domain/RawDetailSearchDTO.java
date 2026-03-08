package com.erns.coching.raw.domain;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>원료 자료 검색 DTO</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RawDetailSearchDTO extends BaseSearchDTO implements IReqDto {


	 private long rawDetailSeq;
	 private long rawSeq;
	 private long membSeq;		//담당자
	private String permitYn;		//관리자 허용여부
	private String useYn;			//사용여부
	private String delYn;			//삭제여부

}
