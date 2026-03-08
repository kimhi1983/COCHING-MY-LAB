package com.erns.coching.raw.domain;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
*
* <p>원료 성분 설정 DTO</p>
*
* @author Kyungmin Lee
*
*/
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RawDetailSetDTO extends AbstractReqDTO implements IReqDto {

	private long rawDetailSeq;			//원료 상세 SEQ

	private long rawSeq;			//원료 SEQ

	private long membSeq;			//담당자 SEQ
	private String title;			//타이틀
	private String hashtag;			//해쉬태그
	private String fileId;			//파일 id
	private String rawDesc;			//text 설명
	private String rawDetail;		//Editor 설명
	private	String strDelFileIds;	//삭제파일리스트
	private String permitYn;		//관리자 허용여부
	private String useYn;			//사용여부
	private String delYn;			//삭제여부
	private String rawName;			//원료명
	private String supplier;		//공급사
}
