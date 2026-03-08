package com.erns.coching.mltln.domain;

import java.util.Date;

import org.springframework.util.Assert;

import com.erns.coching.login.domain.LoginUserDTO;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <p>다국어 VO</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class MltlnVO {

	protected String code;
	protected String codeNmKo;
	protected String codeNmEn;
	protected long rgtr;
	protected Date rgtDttm;

	private void setFromMltlnidSetDTO(MltlnSetDTO fromDto) {
		this.code = fromDto.getCode();
	    this.codeNmKo = fromDto.getCodeNmKo();
	    this.codeNmEn = fromDto.getCodeNmEn();
	}

	/**
	 * MLTLN 등록 Builder
	 * @param fromDto
	 * @param addUser
	 */
	@Builder(builderClassName = "AddMltlnBuilder", builderMethodName = "AddMltlnBuilder")
	public MltlnVO(MltlnSetDTO fromDto, LoginUserDTO addUser) {
	    Assert.notNull(fromDto, "fromDto must not be null");
	    Assert.notNull(addUser, "addUser must not be null");

	    this.setFromMltlnidSetDTO(fromDto);

	    this.rgtr = addUser.getSeq();
	}

	/**
	 * MLTLN 설정
	 * @param code
	 * @param fromDto
	 * @param updateUser
	 */
	@Builder(builderClassName = "SetMltlnBuilder", builderMethodName = "SetMltlnBuilder")
	public MltlnVO(String code, MltlnSetDTO fromDto, LoginUserDTO updateUser) {
	    Assert.notNull(fromDto, "fromDto must not be null");
	    Assert.notNull(updateUser, "updateUser must not be null");

	    this.setFromMltlnidSetDTO(fromDto);
	    this.code = code;

//	    this.chnr = updateUser.getSeq();
	}

}
