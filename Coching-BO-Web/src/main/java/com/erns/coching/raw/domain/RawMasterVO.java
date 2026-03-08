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
 * <p>원료 마스터 VO
 * T_RAW_MST</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class RawMasterVO {

	protected long rawSeq;			//원료 SEQ
	protected long ptnSeq;			//원료 SEQ
	protected String rawName;		//원료명
	protected String prodCompany;	//제조사
	protected String prodCountry;	//제조국
	protected String supplier;		//공급사
	protected String weight;		//무게

	protected String useYn;			//사용여부
	protected String delYn;			//삭제여부
	protected String permitYn; 		//관리자 허용여부

	protected long rgtr;			//등록자
	protected Date rgtDttm;			//등록일
	protected long chnr;			//수정자
	protected Date chngDttm;		//수정일

}
