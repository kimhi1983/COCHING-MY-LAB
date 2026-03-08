package com.erns.coching.board.domain;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import com.erns.coching.board.domain.vg.ValidBoard0040;
import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * <p>게시판 복수 설정 DTO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class BoardMultipleSetDTO extends AbstractReqDTO implements IReqDto {

	@NotEmpty(groups = {
		ValidBoard0040.class
    }, message = "list 누락")
	private List<BoardSetDTO> list;

}