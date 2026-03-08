package com.erns.coching.sysProp.domain;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
/**
 * <p>누진세율 SysPropSearchDTO</p>
 *
 * @author 	jsjeong@erns.co.kr
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class SysPropSearchDTO extends BaseSearchDTO implements IReqDto{

	// @NotBlank(groups = {
	//   	ValidSysProp0040.class
	// 	}, 
  //   message = "sysKey 누락")
	private String sysKey;

	private String sysValue;
}
