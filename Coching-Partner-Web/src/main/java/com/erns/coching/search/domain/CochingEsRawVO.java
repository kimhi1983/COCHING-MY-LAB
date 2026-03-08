package com.erns.coching.search.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRawValue;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class CochingEsRawVO {

	@JsonIgnore
	private String job;	//index, delete, update

	private long rawSeq;
	private String rawName;
	private String prodCompany;
	private String prodCountry;
	private String prodCountryName;
	private String supplier;
	private String weight;
	private String rgtDttm;
	private String ptnName;
	private int ptnSeq;
	private String ptnMembProfileId;
	private String nation;
	private String ptnLic;
	private int rawElemInfoCount;

	@Setter
	private int viewSumCnt;
	private String thumbnailId;

	@JsonRawValue
	private String docinfo;

	@JsonRawValue
	private String rawTypeInfo;

	@JsonRawValue
	private String rawElemInfo;

	@JsonRawValue
	private String rawDetailInfo;
}
