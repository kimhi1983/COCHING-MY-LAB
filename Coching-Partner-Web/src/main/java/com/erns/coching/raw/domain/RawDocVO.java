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
 * <p>원료 서류 VO
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
public class RawDocVO {

	protected long rawSeq;			//원료 SEQ
	protected String docCode;		//서류코드
	protected String docFileId;		//파일 ID
	protected long rgtr;			//등록자
	protected Date rgtDttm;			//등록일

	protected void setFromRawDocSetDTO(RawDocSetDTO fromDto) {
		this.rawSeq = fromDto.getRawSeq();
		this.docCode = fromDto.getDocCode();
	}

	/**
	 * 원료 구분 등록 Builder
	 * @param fromDto
	 */
	@Builder(builderClassName = "AddRawDocBuilder", builderMethodName = "AddRawDocBuilder")
	public RawDocVO(RawDocSetDTO fromDto, long seq) {
		Assert.notNull(fromDto, "fromDto must not be null");

		this.setFromRawDocSetDTO(fromDto);
		this.rgtr = seq;
	}
}
