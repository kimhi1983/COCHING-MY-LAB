package com.erns.coching.raw.domain;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
*
* <p>원료 마스터 설정 DTO</p>
*
* @author Kyungmin Lee
*
*/
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RawMasterSetDTO extends AbstractReqDTO implements IReqDto {

	private long rawSeq;			//원료 SEQ

	private long ptnSeq;			//원료사 SEQ

	private String rawName;			//원료명
	private String prodCompany;		//제조사
	private String prodCountry;		//제조국
	private String supplier;		//공급사
	private String weight;			//무게

	private String useYn;			//사용여부
	private String delYn;			//삭제여부
	private String permitYn; 		//관리자 허용여부

}
