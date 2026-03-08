package com.erns.coching.rnd.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * <p>연구실 성분 정보 VO
 * t_lab_element_inf</p>
 *
 * @author Hunwoo Park
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class LabElementInfVO {

	private long labEleSeq;    // lab_ele_seq
	private long labMstSeq;    // lab_mst_seq
	private Integer rawElmId;  // raw_elm_id
	private String rawElmKr;   // raw_elm_kr
	private String rawElmEn;   // raw_elm_en

}

