package com.erns.coching.raw.domain;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>원료 성분 검색 DTO</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RawElmSearchDTO extends BaseSearchDTO implements IReqDto {

	 private String allSrch;

	 private long rawElmSeq;		//원료 성분 SEQ
	 private long rawSeq;			//원료 SEQ
	 private String useYn;			//사용여부
	 private String delYn;			//삭제여부

}
