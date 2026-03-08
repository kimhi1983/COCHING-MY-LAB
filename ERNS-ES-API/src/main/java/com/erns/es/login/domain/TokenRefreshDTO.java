package com.erns.es.login.domain;

import javax.validation.constraints.NotEmpty;

import com.erns.es.common.model.IReqDto;
import com.erns.es.login.domain.vg.ValidGroupLogin0002;

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
public class TokenRefreshDTO implements IReqDto{

	@NotEmpty(groups = {
			ValidGroupLogin0002.class
	    }, message = "accessToken 누락")
	private String accessToken;

	@NotEmpty(groups = {
			ValidGroupLogin0002.class
	    }, message = "refreshToken 누락")
	private String refreshToken;
}
