package com.erns.coching.popup.domain;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.popup.domain.vg.ValidPopup0031;
import com.erns.coching.popup.domain.vg.ValidPopup0032;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * <p>팝업업 복수 설정 DTO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class PopupMultipleSetDTO extends AbstractReqDTO implements IReqDto {

	@NotEmpty(groups = {
			ValidPopup0031.class,
			ValidPopup0032.class,
    	}, message = "list 누락")
	private List<PopupSetDTO> list;

}