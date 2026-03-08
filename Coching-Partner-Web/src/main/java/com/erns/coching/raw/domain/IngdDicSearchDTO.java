package com.erns.coching.raw.domain;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>성분 사전 검색 DTO</p>
 *
 * @author Auto Generated
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class IngdDicSearchDTO extends BaseSearchDTO implements IReqDto {

	private Integer id;		//성분 ID
	private String repName;		//대표명
	private String repNameEn;	//대표명(영문)

}

