package com.erns.coching.terms.domain;

import java.sql.Date;

import org.springframework.util.Assert;

import com.erns.coching.login.domain.LoginUserDTO;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 * <p>약관정보 VO
 * T_TERMS_INF</p>
 *
 * @author Hunwoo Park
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TermsVO {

	protected String termsCd;
	protected String version;
	protected String termsType;
	protected String content;
	protected String useYn;
	protected String requiredYn;
	protected String fromDate;
	protected String toDate;
	protected String remarks;

	protected long rgtr;
	protected Date rgtDttm;
	protected long chnr;
	protected Date chngDttm;

	protected void setFromTermsSetDTO(TermsSetDTO fromDto) {
		this.termsCd = fromDto.getTermsCd();
		this.version = fromDto.getVersion();

		this.termsType = fromDto.getTermsType();
		this.content = fromDto.getContent();
		this.useYn = fromDto.getUseYn();
		this.requiredYn = fromDto.getRequiredYn();
		this.fromDate = fromDto.getFromDate();
		this.toDate = fromDto.getToDate();
		this.remarks = fromDto.getRemarks();
	}

	/**
	 * 약관정보 등록
	 * @param fromDto
	 * @param addUser
	 */
	@Builder(builderClassName = "AddTermsdBuilder", builderMethodName = "AddTermsdBuilder")
	public TermsVO(TermsSetDTO fromDto, LoginUserDTO addUser) {
	    Assert.notNull(fromDto, "fromDto must not be null");
	    Assert.notNull(addUser, "addUser must not be null");

	    this.setFromTermsSetDTO(fromDto);
	    this.version = fromDto.getNewVersion();
	    this.useYn = "Y";

	    this.rgtr = addUser.getSeq();
	    this.chnr = addUser.getSeq();
	}

	/**
	 * 약관정보 수정
	 * @param termsCd
	 * @param version
	 * @param fromDto
	 * @param updateUser
	 */
	@Builder(builderClassName = "UpdateTermsdBuilder", builderMethodName = "UpdateTermsdBuilder")
	public TermsVO(String termsCd, String version, TermsSetDTO fromDto, LoginUserDTO updateUser) {
	    Assert.notNull(fromDto, "fromDto must not be null");
	    Assert.notNull(updateUser, "updateUser must not be null");

	    this.setFromTermsSetDTO(fromDto);
	    this.termsCd = termsCd;
	    this.version = version;
	    this.useYn = fromDto.getUseYn();

	    this.chnr = updateUser.getSeq();
	}
}
