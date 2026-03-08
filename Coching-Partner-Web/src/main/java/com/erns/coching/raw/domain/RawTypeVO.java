package com.erns.coching.raw.domain;

import java.util.Date;

import org.springframework.util.Assert;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * <p>원료 구분 정보 VO
 * T_RAW_DOC</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class RawTypeVO {

	protected long rawSeq;			//원료 SEQ
	protected String grpCode;		//그룹코드
	protected String code;			//코드
	protected long rgtr;			//등록자
	protected Date rgtDttm;			//등록일

	protected void setFromRawTypeSetDTO(RawTypeSetDTO fromDto) {
		this.rawSeq = fromDto.getRawSeq();
		this.grpCode = fromDto.getGrpCode();
		this.code = fromDto.getCode();
	}

	/**
	 * 원료 구분 등록 Builder
	 * @param fromDto
	 */
	@Builder(builderClassName = "AddRawTypeBuilder", builderMethodName = "AddRawTypeBuilder")
	public RawTypeVO(RawTypeSetDTO fromDto, long seq) {
		Assert.notNull(fromDto, "fromDto must not be null");

		this.setFromRawTypeSetDTO(fromDto);
		this.rgtr = seq;
	}
}
