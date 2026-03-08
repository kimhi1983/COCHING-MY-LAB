package com.erns.coching.raw.domain;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>원료 마스터 검색 DTO</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RawMasterSearchDTO extends BaseSearchDTO implements IReqDto {

	 private String allSrch;

	 private long rawSeq;
	 private long ptnSeq;
	 private String rawName;		//원료명
	 private Integer membSeq;		//담당자
	 private String prodCompany;	//제조사
	 private String prodCountry;	//제조국
	 private String supplier;		//공급사
	 private String useYn;			//사용여부
	 private String rawMemb;
	 private Integer requestCnt;
	 private String delYn;
	 private String permitYn;
}
