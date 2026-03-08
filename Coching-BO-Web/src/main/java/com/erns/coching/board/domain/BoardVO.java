package com.erns.coching.board.domain;

import java.util.Date;

import org.springframework.util.Assert;

import com.erns.coching.login.domain.LoginUserDTO;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * <p>게시판 VO
 * T_BOARD_INF</p>
 *
 * @author Hunwoo Park
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class BoardVO {
	protected long boardSeq;
	protected String boardMstId;
	protected String cateCd;
	protected String title;
	protected String content;
	protected String writer;
	protected int sortOrd;
	protected String delYn;
	protected String adminDelCd;
	protected String adminDelRsn;
	protected int views;
	protected String fromDate;
	protected String toDate;
	protected String fixYn;
	protected long rgtr;
	protected Date rgtDttm;
	protected long chnr;
	protected Date chngDttm;

	protected void setFromBoardSetDTO(BoardSetDTO fromDto) {
		this.boardSeq = fromDto.getBoardSeq();
	    this.boardMstId = fromDto.getBoardMstId();

	    this.cateCd = fromDto.getCateCd();
	    this.title = fromDto.getTitle();
	    this.content = fromDto.getContent();
	    this.sortOrd = fromDto.getSortOrd();
	    this.delYn = fromDto.getDelYn();
	}

	/**
	 * 게시판 등록 Builder
	 * @param fromDto
	 */
	@Builder(builderClassName = "AddBoardBuilder", builderMethodName = "AddBoardBuilder")
	public BoardVO(BoardSetDTO fromDto) {
	    Assert.notNull(fromDto, "fromDto must not be null");

	    this.setFromBoardSetDTO(fromDto);
	    this.boardSeq = 0L;
	    this.delYn = "N";

	    this.writer = fromDto.getWriter();
	    this.rgtr = fromDto.getRgtr();
	    this.chnr = fromDto.getChnr();
	}

	/**
	 * 게시판 수정 Builder
	 * @param boardSeq
	 * @param fromDto
	 * @param updateUser
	 */
	@Builder(builderClassName = "SetBoardBuilder", builderMethodName = "SetBoardBuilder")
	public BoardVO(long boardSeq, BoardSetDTO fromDto) {
	    Assert.notNull(fromDto, "fromDto must not be null");

	    this.setFromBoardSetDTO(fromDto);
	    this.boardSeq = boardSeq;
	    this.delYn = "N";	
	    
	    this.writer = fromDto.getWriter();
	    this.rgtr = fromDto.getRgtr();
	    this.chnr = fromDto.getChnr();
	}

	/**
	 * 게시판 삭제 Builder
	 * @param boardSeq
	 * @param delYn
	 * @param updateUser
	 */
	@Builder(builderClassName = "UpdateDelYnBuilder", builderMethodName = "UpdateDelYnBuilder")
	public BoardVO(long boardSeq, String delYn, LoginUserDTO updateUser) {
	    Assert.notNull(delYn, "delYn must not be null");
	    Assert.notNull(updateUser, "updateUser must not be null");

	    this.boardSeq = boardSeq;
	    this.delYn = delYn;

	    this.writer = updateUser.getUserName();
	    this.chnr = updateUser.getSeq();
	}
}
