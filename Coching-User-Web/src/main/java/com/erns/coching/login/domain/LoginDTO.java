package com.erns.coching.login.domain;

import javax.validation.constraints.NotEmpty;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.login.domain.vg.ValidGroupLogin0001;
import com.erns.coching.login.domain.vg.ValidGroupLogin0003;

import com.erns.coching.login.domain.vg.ValidGroupLogin0004;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>로그인 DTO</p>
 *
 * @author heejinyu@erns.co.kr
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class LoginDTO extends AbstractReqDTO implements IReqDto{

	@NotEmpty(groups = {
			ValidGroupLogin0001.class,
			ValidGroupLogin0003.class
	    }, message = "id 누락")
	protected String id;

	@NotEmpty(groups = {
			ValidGroupLogin0001.class
	    }, message = "password 누락")
	protected String password;

	@NotEmpty(groups = {
			ValidGroupLogin0003.class,
			ValidGroupLogin0004.class
	}, message = "email 누락")
	protected  String email;

	protected  String ptnSeq;

	protected  String membName;


	protected boolean remainLogin = false;


}
