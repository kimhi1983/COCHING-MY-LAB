package com.erns.coching.raw.domain;

import javax.validation.constraints.NotNull;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.raw.domain.vg.ValidSetRawReply0001;
import com.erns.coching.raw.domain.vg.ValidSetRawReply0002;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
*
* <p>원료 요청 타입 설정 DTO</p>
*
* @author Kyungmin Lee
*
*/
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RawRequestReplySetDTO extends AbstractReqDTO implements IReqDto {

	@NotNull(groups = {ValidSetRawReply0002.class},
			message = "rawReplySeq 누락")
	private long rawReplySeq;		//원료 요청 답변 내역 SEQ
	@NotNull(groups = {ValidSetRawReply0001.class},
			message = "rawRequestSeq 누락")
	private long rawRequestSeq;		//원료 요청 내역 SEQ
	private String dispatchCode;    //요청 타입
	private String dispatchDate;    //날짜 직접입력
	private String contents;        //메세지
	private long refSeq;			//상위 seq
	private int replyCmtOrd;		//depth별 ord
	@NotNull(groups = {ValidSetRawReply0002.class},
			message = "delYn 누락")
	private String delYn;
	private long rgtr;				//등록자
	private long chnr;				//수정자
	private String strDelFileIds;	//삭제파일리스트

	private String ptnName;			//원료사 이름
	private String rawName;			//원료 이름
	private long membSeq;			//요청자
	private long managerSeq;		//담당자
}
