package com.erns.coching.board.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.erns.coching.board.domain.vg.ValidBoard0021;
import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * <p>1:1 문의 게시물 설정 DTO</p>
 *
 * @author Hunwoo Park
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class BoardInquirySetDTO extends AbstractReqDTO implements IReqDto{

    @NotNull(groups = {
        }, message = "boardSeq 누락")
	@Min(groups = {
		},
		value = 1,		
		message = "boardSeq 누락")
	private long boardSeq;

    @NotBlank(groups = {    	
    		ValidBoard0021.class
        }, message = "boardMst 누락")
	private String boardMstId;

	private String cateCd;

    @NotBlank(groups = {
        }, message = "제목 누락")
	private String title;

    @NotBlank(groups = {
        }, message = "내용 누락")
	private String content;

	private String writer;
	private String phone;
	private String email;

	private int orgSeq;
	private int sortOrd;

    @NotBlank(groups = {
        }, message = "삭제여부 누락")
    @Pattern(groups = {
		}, message = "삭제여부 형식오류", regexp = "Y|N")
	private String delYn;
	private String adminDelCd;
	private String adminDelRsn;
	private int views;
	private String fromDate;
	private String toDate;

	@Pattern(groups = {
		}, message = "고정여부 형식오류", regexp = "Y|N")
	private String fixYn;

	private long rgtr; //등록자
	private long chnr; //수정자
	private	String strDelFileIds;	//삭제파일리스트
}
