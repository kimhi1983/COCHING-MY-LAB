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
 * <p>원료 마스터 VO
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
public class RawMasterVO {

	protected long rawSeq;			//원료 SEQ
	protected long ptnSeq;			//원료 SEQ
	protected String rawName;		//원료명
	protected String prodCompany;	//제조사
	protected String prodCountry;	//제조국
	protected String supplier;		//공급사
	protected String weight;		//무게

	protected String useYn;			//사용여부
	protected String delYn;			//삭제여부
	protected String permitYn; 		//관리자 허용여부

	protected long rgtr;			//등록자
	protected Date rgtDttm;			//등록일
	protected long chnr;			//수정자
	protected Date chngDttm;		//수정일

	protected void setFromRawMasterSetDTO(RawMasterSetDTO fromDto) {
		this.rawSeq = fromDto.getRawSeq();
		this.ptnSeq = fromDto.getPtnSeq();
		this.rawName = fromDto.getRawName();
		this.prodCompany = fromDto.getProdCompany();
		this.prodCountry = fromDto.getProdCountry();
		this.supplier = fromDto.getSupplier();
		this.weight = fromDto.getWeight();
	}

	/**
	 * 원료 마스터 등록 Builder
	 * @param fromDto
	 */
	@Builder(builderClassName = "AddRawMasterBuilder", builderMethodName = "AddRawMasterBuilder")
	public RawMasterVO(RawMasterSetDTO fromDto, LoginUserDTO addUser) {
	    Assert.notNull(fromDto, "fromDto must not be null");

	    this.setFromRawMasterSetDTO(fromDto);
		this.rgtr = addUser.getSeq();
		this.chnr = addUser.getSeq();
	}

	/**
	 * 원료 마스터 사용여부 Builder
	 * @param fromDto
	 */
	@Builder(builderClassName = "UpdateRawMasterUseBuilder", builderMethodName = "UpdateRawMasterUseBuilder")
	public RawMasterVO(RawMasterSetDTO fromDto, long membSeq, long ptnSeq) {
		Assert.notNull(fromDto, "fromDto must not be null");

		this.rawSeq = fromDto.getRawSeq();
		this.ptnSeq = ptnSeq;
		this.useYn = fromDto.getUseYn();
		this.chnr = membSeq;
	}
}
