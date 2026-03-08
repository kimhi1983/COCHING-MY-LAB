package com.erns.coching.cochingtv.domain;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import com.erns.coching.cochingtv.domain.vg.ValidCochingtv0032;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * <p>코칭TV 복수 설정 DTO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CochingtvMultipleSetDTO {
	@NotEmpty(groups = {
			ValidCochingtv0032.class
    	}, message = "list 누락")
	private List<CochingtvSetDTO> list;
}
