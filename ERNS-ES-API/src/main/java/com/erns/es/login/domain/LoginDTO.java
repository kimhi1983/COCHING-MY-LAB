package com.erns.es.login.domain;

import javax.validation.constraints.NotEmpty;

import com.erns.es.common.model.IReqDto;
import com.erns.es.login.domain.vg.ValidGroupLogin0001;

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
public class LoginDTO implements IReqDto{

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
