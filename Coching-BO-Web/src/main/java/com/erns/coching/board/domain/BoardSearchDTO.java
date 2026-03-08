package com.erns.coching.board.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.erns.coching.board.domain.vg.ValidBoard0001;
import com.erns.coching.board.domain.vg.ValidBoardComment0011;
import com.erns.coching.board.domain.vg.ValidBoardMaster0002;
import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>게시판마스터 검색 DTO</p>
 * 
 * @author heejinyu@erns.co.kr
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class BoardSearchDTO extends BaseSearchDTO implements IReqDto{

    @NotBlank(groups = {
        ValidBoardMaster0002.class, ValidBoard0001.class
    }, message = "boardMstId 누락")
	private String boardMstId;
    
    @NotNull(groups = {
    		ValidBoardComment0011.class
    }, message = "boardSeq 누락")
    private long boardSeq;

	private String boardType;
	
	private String boardMstIdL;
	
	private String boardNameL;

	private String titleL;
	
	private String delYn;
	
	private String cateCd;
	
	private String fixYn;
}
