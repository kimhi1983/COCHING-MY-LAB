package com.erns.coching.message.domain;

import java.util.Date;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>쪽지 검색 DTO</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class MessageSearchDTO extends BaseSearchDTO implements IReqDto {

	private long rawSeq;			//원료 SEQ
	private long messageSeq;		//쪽지 SEQ
	private long sender;			//발신자
	private long receiver;			//수신자
	private String content;			//내용
	private String state;			//상태
    private String secretYn;		//비밀댓글여부
    private long rawRequestSeq;  	//원료요청내역 SEQ
	private String blockYn;			//차단 여부
	private Date rgtDttm;			//등록일
}
