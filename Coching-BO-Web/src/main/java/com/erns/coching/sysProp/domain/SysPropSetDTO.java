package com.erns.coching.sysProp.domain;

import javax.validation.constraints.NotBlank;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.sysProp.domain.vg.ValidSysProp0021;
import com.erns.coching.sysProp.domain.vg.ValidSysProp0040;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>환경변수 SetDTO</p>
 * 
 * @author 	jsjeong@erns.co.kr
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class SysPropSetDTO extends AbstractReqDTO implements IReqDto{
  
	@NotBlank(groups = {
	  	ValidSysProp0021.class,
			ValidSysProp0040.class
		}, 
    message = "sysKey 누락")
	private String sysKey;  
   
   @NotBlank(groups = {
		   ValidSysProp0021.class
		}, 
   	message = "sysValue 누락")
	private String sysValue;  

}
