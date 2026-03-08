package com.erns.coching.raw.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * <p>성분 사전 정보 VO
 * T_INGD_DIC</p>
 *
 * @author Auto Generated
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class IngdDicVO {

	private Integer id;			//성분 ID
	private String repName;		//대표명
	private String repNameEn;	//대표명(영문)

}

