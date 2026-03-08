package com.erns.coching.board.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.erns.coching.board.domain.vg.ValidBoard0011;
import com.erns.coching.board.domain.vg.ValidBoard0012;
import com.erns.coching.board.domain.vg.ValidBoard0020;
import com.erns.coching.board.domain.vg.ValidBoard0030;
import com.erns.coching.board.domain.vg.ValidBoard0040;
import com.erns.coching.board.domain.vg.ValidBoardComment0011;
import com.erns.coching.board.domain.vg.ValidBoardComment0020;
import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>게시판 검색</p>
 *
 * @author hw.park@erns.co.kr
 *
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardSearchDTO extends BaseSearchDTO implements IReqDto{

	@NotBlank(groups = {
			ValidBoard0011.class,
			ValidBoard0020.class,
			ValidBoard0030.class,
			ValidBoardComment0011.class,
	}, message = "boardMstId 누락")
	private String boardMstId;
    
	@NotNull(groups = {
			ValidBoard0012.class,
			ValidBoard0030.class,
			ValidBoard0040.class,
			ValidBoardComment0011.class,
			ValidBoardComment0020.class,
	}, message = "boardSeq 누락")
	private long boardSeq;

	private String boardType;
	
	private String boardMstIdL;
	
	private String boardNameL;

	private String titleL;
	
	private String delYn;
	
	private String cateCd;
	
	private String fixYn;

	private long rgtr;

	private boolean isMyBoard;
}
