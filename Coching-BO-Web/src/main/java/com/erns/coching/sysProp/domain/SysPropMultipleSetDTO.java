package com.erns.coching.sysProp.domain;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.sysProp.domain.vg.ValidSysProp0021;
import com.erns.coching.sysProp.domain.vg.ValidSysProp0040;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>환경변수 다중 SetDTO</p>
 * 
 * @author 	jsjeong@erns.co.kr
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class SysPropMultipleSetDTO extends AbstractReqDTO implements IReqDto{
  
	@NotEmpty(groups = {
	  	ValidSysProp0021.class,
			ValidSysProp0040.class
		}, 
    message = "list 누락")
	private List<SysPropSetDTO> list;
}

