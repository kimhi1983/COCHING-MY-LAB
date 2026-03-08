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
 * <p>원료 성분 정보 VO
 * T_RAW_ELM_INF</p>
 *
 * @author Dasom Nam
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class RawElmVO {

	private long rawElmSeq;			//원료 성분 SEQ
	private long rawSeq;			//원료 SEQ
	private long rawElmId;			//마스터 성분 ID
	private long rgtr;				//등록자
	private Date rgtDttm;			//등록일
	private long chnr;				//수정자
	private Date chngDttm;			//수정일

}
