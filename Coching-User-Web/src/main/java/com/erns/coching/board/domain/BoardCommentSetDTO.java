package com.erns.coching.board.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.erns.coching.board.domain.vg.ValidBoardComment0020;
import com.erns.coching.board.domain.vg.ValidBoardComment0030;
import com.erns.coching.board.domain.vg.ValidBoardComment0040;
import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * <p>게시물 코멘트 설정 DTO</p>
 *
 * @author Hunwoo Park
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class BoardCommentSetDTO extends AbstractReqDTO implements IReqDto{
	
    @NotNull(groups = {
    		ValidBoardComment0030.class,
    		ValidBoardComment0040.class
        }, message = "boardCmtSeq 누락")
	private long boardCmtSeq;
	
    @NotNull(groups = {
    		ValidBoardComment0020.class
        }, message = "boardSeq 누락")
	private long boardSeq;

	private String writer;

	private String content;
	
	@NotBlank(groups = {
			ValidBoardComment0040.class
	    }, message = "삭제여부 누락")
	@Pattern(groups = {
			ValidBoardComment0040.class
		}, message = "삭제여부 형식오류", regexp = "Y|N")
	private String delYn;
	
	private String adminDelCd;
	private String adminDelRsn;

	private long refId;
    
	private int cmtOrd;
	private int sortOrd;
	
	@Pattern(groups = {
	}, message = "비밀댓글 여부 형식오류", regexp = "Y|N")
	private String secretYn;

	private long rgtr; //등록자
	private long chnr; //수정자
	
	private String replyYn; //대댓글여부
	private long boardWriterSeq; //(알림용)글작성자 seq
	private long cmtWriterSeq; //(대댓글 알림용)댓글작성자 seq
	private String boardTitle; //(알림용)글제목
	
}
