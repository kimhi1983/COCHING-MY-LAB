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
* <p>게시판 코멘트 VO
* T_BOARD_CMT</p>
*
* @author Hunwoo Park
*
*/
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class BoardCommentVO {
	protected long boardCmtSeq;
	protected long boardSeq;
	protected String writer;
	protected String content;
	protected String delYn;
	protected String adminDelCd;
	protected String adminDelRsn;
	protected long refId;
	protected int cmtOrd;
	protected int sortOrd;
	protected String secretYn;
	
	protected long rgtr;
	protected Date rgtDttm;
	protected long chnr;
	protected Date chngDttm;
	
	protected String replyYn;
	protected long boardWriterSeq;
	protected long cmtWriterSeq;
	private String boardTitle;

	protected void setFromBoardCommentSetDTO(BoardCommentSetDTO fromDto) {
		this.boardCmtSeq = fromDto.getBoardCmtSeq();
		this.boardSeq = fromDto.getBoardSeq();
	    this.content = fromDto.getContent();
	    this.delYn = fromDto.getDelYn();
	    this.refId = fromDto.getRefId();
	    this.cmtOrd = fromDto.getCmtOrd();
	    this.sortOrd = fromDto.getSortOrd();
	    this.secretYn = fromDto.getSecretYn();
	    this.boardWriterSeq = fromDto.getBoardWriterSeq();
	    this.cmtWriterSeq = fromDto.getCmtWriterSeq();
	    this.boardTitle = fromDto.getBoardTitle();
	}

	/**
	 * 게시판 코멘트 등록 Builder
	 * @param fromDto
	 */
	@Builder(builderClassName = "AddBoardCommentBuilder", builderMethodName = "AddBoardCommentBuilder")
	public BoardCommentVO(BoardCommentSetDTO fromDto) {
	    Assert.notNull(fromDto, "fromDto must not be null");

	    this.setFromBoardCommentSetDTO(fromDto);
	    this.delYn = "N";

	    this.writer = fromDto.getWriter();
	    this.replyYn = fromDto.getReplyYn();
	    this.rgtr = fromDto.getRgtr();
	    this.chnr = fromDto.getChnr();
	}

	/**
	 * 게시판 코멘트 수정 Builder
	 * @param boardSeq
	 * @param fromDto
	 * @param updateUser
	 */
	@Builder(builderClassName = "SetBoardCommentBuilder", builderMethodName = "SetBoardCommentBuilder")
	public BoardCommentVO(long boardSeq, BoardCommentSetDTO fromDto) {
	    Assert.notNull(fromDto, "fromDto must not be null");

	    this.setFromBoardCommentSetDTO(fromDto);
	    this.delYn = "N";	
	    
	    this.writer = fromDto.getWriter();
	    this.rgtr = fromDto.getRgtr();
	    this.chnr = fromDto.getChnr();
	}

	/**
	 * 게시판 코멘트 삭제 Builder
	 * @param boardSeq
	 * @param delYn
	 * @param updateUser
	 */
	@Builder(builderClassName = "UpdateDelYnBuilder", builderMethodName = "UpdateDelYnBuilder")
	public BoardCommentVO(long boardCmtSeq, String delYn, LoginUserDTO updateUser) {
	    Assert.notNull(delYn, "delYn must not be null");
	    Assert.notNull(updateUser, "updateUser must not be null");

	    this.boardCmtSeq = boardCmtSeq;
	    this.delYn = delYn;

	    this.writer = updateUser.getUserName();
	    this.chnr = updateUser.getSeq();
	}
}
