package com.erns.coching.board.domain;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import com.erns.coching.board.domain.vg.ValidBoardMaster0032;
import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * <p>게시판마스터 복수 설정 DTO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class BoardMasterMultipleSetDTO extends AbstractReqDTO implements IReqDto {

	@NotEmpty(groups = {
		ValidBoardMaster0032.class
    }, message = "list 누락")
	private List<BoardMasterSetDTO> list;

}