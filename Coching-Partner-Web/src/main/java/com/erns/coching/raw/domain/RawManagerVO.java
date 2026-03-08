package com.erns.coching.raw.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.Assert;

import java.util.Date;

/**
 *
 * <p>원료 담당자 VO
 * T_RAW_MST</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class RawManagerVO {

	protected long rawSeq;			//원료 SEQ
	protected long membSeq;			//멤버 SEQ

	protected long rgtr;			//등록자
	protected Date rgtDttm;			//등록일

	protected void setFromRawManagerSetDTO(RawManagerSetDTO fromDto) {
		this.rawSeq = fromDto.getRawSeq();
		this.membSeq = fromDto.getMembSeq();
	}

	/**
	 * 원료 구분 등록 Builder
	 * @param fromDto
	 */
	@Builder(builderClassName = "AddRawManagerBuilder", builderMethodName = "AddRawManagerBuilder")
	public RawManagerVO(RawManagerSetDTO fromDto, long seq) {
		Assert.notNull(fromDto, "fromDto must not be null");

		this.setFromRawManagerSetDTO(fromDto);
		this.rgtr = seq;
	}

	/**
	 * 원료 구분 등록 Builder
	 * @param fromDto
	 */
	@Builder(builderClassName = "DeleteManagerBuilder", builderMethodName = "DeleteManagerBuilder")
	public RawManagerVO(RawDetailSetDTO fromDto) {
		Assert.notNull(fromDto, "fromDto must not be null");

		this.rawSeq = fromDto.getRawSeq();
		this.membSeq = fromDto.getMembSeq();
	}
}
