package com.erns.coching.board.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.erns.coching.board.domain.vg.ValidBoard0011;
import com.erns.coching.board.domain.vg.ValidBoard0012;
import com.erns.coching.board.domain.vg.ValidBoard0017;
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
 * <p>1:1 문의 게시판 검색</p>
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
public class BoardInquirySearchDTO extends BaseSearchDTO implements IReqDto{

	@NotBlank(groups = {
	}, message = "boardMstId 누락")
	private String boardMstId;
    
	@NotNull(groups = {
		ValidBoard0017.class
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

	private String locale;
}
