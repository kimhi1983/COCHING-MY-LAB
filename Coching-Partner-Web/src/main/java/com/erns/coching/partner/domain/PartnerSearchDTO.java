package com.erns.coching.partner.domain;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>파트너사 검색 DTO</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class PartnerSearchDTO extends BaseSearchDTO implements IReqDto {

	 private String allSrch;

	 private long ptnSeq;
	 private String ptnName;
	 private String nation;
	 private String ptnLic;
	 private String pageUrl;
	 private String delYn;
}
