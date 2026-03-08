package com.erns.coching.raw.domain;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
*
* <p>원료 성분 설정 DTO</p>
*
* @author Kyungmin Lee
*
*/
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RawElmSetDTO extends AbstractReqDTO implements IReqDto {

	private long rawElmSeq;			//원료 성분 SEQ
	private long rawSeq;			//원료 SEQ
	private long rawElmId;		//마스터 성분 ID
	private long sortOrd;
}
