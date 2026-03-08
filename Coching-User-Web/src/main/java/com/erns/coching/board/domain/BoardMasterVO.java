package com.erns.coching.board.domain;

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
*
* <p>게시판 마스터 VO
* T_BOARD_MST</p>
*
* @author Hunwoo Park
*
*/
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class BoardMasterVO {

	protected String boardMstId;
	protected String boardName;
	protected String boardType;
	protected String boardDesc;
	protected String useYn;
	protected long rgtr;
	protected Date rgtDttm;
	protected long chnr;
	protected Date chngDttm;
}
