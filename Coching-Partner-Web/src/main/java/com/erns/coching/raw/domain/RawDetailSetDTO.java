package com.erns.coching.raw.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.raw.domain.vg.ValidSetRaw0005;
import com.erns.coching.raw.domain.vg.ValidSetRawDetail0001;

import com.erns.coching.raw.domain.vg.ValidSetRawDetail0002;
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

	@Min(groups = {
			ValidSetRaw0005.class,
			ValidSetRawDetail0002.class
	},
			value = 1,
			message = "rawDetailSeq 누락")
	private long rawDetailSeq;			//원료 상세 SEQ
	@NotNull(groups = {ValidSetRawDetail0001.class,
			ValidSetRawDetail0002.class},
			message = "rawSeq 누락")
	@Min(groups = {
			ValidSetRawDetail0001.class,
			ValidSetRaw0005.class
	},
			value = 1,
			message = "rawSeq 누락")
	private long rawSeq;			//원료 SEQ
	@NotNull(groups = {ValidSetRawDetail0001.class},
			message = "membSeq 누락")
	@Min(groups = {
			ValidSetRaw0005.class
	},
			value = 1,
			message = "membSeq 누락")
	private long membSeq;			//담당자 SEQ
	@NotNull(groups = {ValidSetRawDetail0001.class},
			message = "title 누락")
	private String title;			//타이틀
	private String hashtag;			//해쉬태그
	private String fileId;			//파일 id
	private String rawDesc;			//text 설명
	private String rawDetail;		//Editor 설명
	private	String strDelFileIds;	//삭제파일리스트
	private	String strDetailFiles;	//detail 파일리스트 String
	private String permitYn;		//관리자 허용여부
	@NotNull(groups = {ValidSetRawDetail0001.class,
			ValidSetRawDetail0001.class},
			message = "useYn 누락")
	private String useYn;			//사용여부
	private String delYn;			//삭제여부
	private String fileType;
}
