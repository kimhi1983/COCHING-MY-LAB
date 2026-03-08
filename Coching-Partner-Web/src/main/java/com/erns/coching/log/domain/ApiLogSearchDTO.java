package com.erns.coching.log.domain;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * <p>API 로그 검색 DTO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiLogSearchDTO extends BaseSearchDTO implements IReqDto {
	
	private String fromDate;

	private String toDate;

	private String siteType;

	@Builder.Default
	private long membSeq = -1;

	private String logType;
	private String logTypeRL;

	private String routerNm;
	private String routerNmL;

	private String accessIp;
	private String accessIpL;

	private String procStatus;

	private String apiUri;
	private String apiUriL;

	private String membIdL;
	private String membNameL;
	
	private String userSearch;
}
