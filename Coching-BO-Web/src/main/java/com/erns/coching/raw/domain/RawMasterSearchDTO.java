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
	private String rawNameL;
	private String ptnNameL;
	private String membNameL;

	private long rawSeq;
	private long rawDetailSeq;
	private long ptnSeq;
	private String ptnName;
	private String rawName;		//원료명
	private long membSeq;		//담당자
	private String membName;
	private String prodCompany;	//제조사
	private String prodCountry;	//제조국
	private String supplier;		//공급사
	private String useYn;			//사용여부
	private String exclude;    // 제외 할 조건
	private String membUseYn;	//사용자 계정 사용여부

	private String incIngNameYn; // 성분명 포함 여부
}
