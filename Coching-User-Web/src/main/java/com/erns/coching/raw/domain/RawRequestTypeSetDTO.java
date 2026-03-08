package com.erns.coching.raw.domain;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
*
* <p>원료 요청 타입 설정 DTO</p>
*
* @author Kyungmin Lee
*
*/
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RawRequestTypeSetDTO extends AbstractReqDTO implements IReqDto {

	private long rawRequestSeq;			//원료 성분 SEQ
	private String code;		//전송 타입

}
