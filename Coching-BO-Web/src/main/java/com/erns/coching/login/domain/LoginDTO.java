package com.erns.coching.login.domain;

import javax.validation.constraints.NotEmpty;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.login.domain.vg.ValidGroupLogin0001;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * <p>로그인 DTO</p>
 *
 * @author Hunwoo Park
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class LoginDTO extends AbstractReqDTO implements IReqDto{

	@NotEmpty(groups = {
			ValidGroupLogin0001.class
	    }, message = "userId 누락")
	protected String userId;

	@NotEmpty(groups = {
			ValidGroupLogin0001.class
	    }, message = "password 누락")
	protected String password;


	protected boolean remainLogin = false;


}
