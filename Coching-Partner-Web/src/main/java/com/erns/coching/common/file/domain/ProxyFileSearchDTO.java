package com.erns.coching.common.file.domain;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
*
* <p>프록시파일 검색 DTO</p>
*
* @author Hunwoo Park
*
*/
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProxyFileSearchDTO extends BaseSearchDTO implements IReqDto{

	private String orgUrl; // 원본 URL

	private String refCode; // 파일그룹 코드
	
	private String fileId;
	private String[] fileIds;

	private String fileExt;
	private String[] fileExts;

	private String delYn;

}
