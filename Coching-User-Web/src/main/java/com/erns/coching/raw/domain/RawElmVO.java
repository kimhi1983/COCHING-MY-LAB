package com.erns.coching.raw.domain;

import com.erns.coching.login.domain.LoginUserDTO;
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
 * <p>원료 성분 정보 VO
 * T_RAW_ELM_INF</p>
 *
 * @author Dasom Nam
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class RawElmVO {

	private long rawElmSeq;			//원료 성분 SEQ
	private long rawSeq;			//원료 SEQ
	private long rawElmId;			//마스터 성분 ID
	private long rgtr;				//등록자
	private Date rgtDttm;			//등록일
	private long chnr;				//수정자
	private Date chngDttm;			//수정일

	protected void setFromRawElmSetDTO(RawElmSetDTO fromDto) {
		this.rawElmSeq = fromDto.getRawElmSeq();
		this.rawSeq = fromDto.getRawSeq();
		this.rawElmId = fromDto.getRawElmId();
	}

	/**
	 * 원료 성분 등록 Builder
	 * @param fromDto
	 */
	@Builder(builderClassName = "AddRawElmBuilder", builderMethodName = "AddRawElmBuilder")
	public RawElmVO(RawElmSetDTO fromDto, LoginUserDTO addUser) {
	    Assert.notNull(fromDto, "fromDto must not be null");

	    this.setFromRawElmSetDTO(fromDto);
		this.rgtr = addUser.getSeq();
		this.chnr = addUser.getSeq();
	}
}
