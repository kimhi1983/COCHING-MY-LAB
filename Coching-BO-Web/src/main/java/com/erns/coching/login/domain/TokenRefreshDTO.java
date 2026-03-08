package com.erns.coching.login.domain;

import javax.validation.constraints.NotEmpty;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.login.domain.vg.ValidGroupLogin0002;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * <p>토큰 재발급 DTO</p>
 *
 * @author Hunwoo Park
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TokenRefreshDTO extends AbstractReqDTO implements IReqDto{

	@NotEmpty(groups = {
			ValidGroupLogin0002.class
	    }, message = "accessToken 누락")
	private String accessToken;

	@NotEmpty(groups = {
			ValidGroupLogin0002.class
	    }, message = "refreshToken 누락")
	private String refreshToken;
}
