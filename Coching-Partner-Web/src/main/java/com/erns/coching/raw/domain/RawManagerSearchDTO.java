package com.erns.coching.raw.domain;

import javax.validation.constraints.NotNull;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.raw.domain.vg.ValidSearchRawManager0001;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>원료 자료 검색 DTO</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RawManagerSearchDTO extends BaseSearchDTO implements IReqDto {

	@NotNull(groups = {ValidSearchRawManager0001.class},
			message = "rawSeq 누락")
	 private long rawSeq;
	 private long membSeq;		//담당자
	 private String permitYn; 		//관리자 허용여부
}
