package com.erns.coching.raw.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.Assert;

import java.util.Date;

/**
 *
 * <p>원료 성분 정보 VO
 * T_RAW_ELM_INF</p>
 *
 * @author Dasom Nam
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class RawDetailVO {

	protected long rawDetailSeq;			//원료 상세 SEQ
	protected long rawSeq;			//원료 SEQ
	protected long membSeq;			//담당자 SEQ
	protected String title;			//타이틀
	protected String hashtag;			//해쉬태그
	protected String fileId;			//파일 id
	protected String rawDesc;			//text 설명
	protected String rawDetail;		//Editor 설명
	protected long veiwCnt;			//조회수
	protected long requestCnt;		//요청수
	protected String permitYn;		//관리자 허용여부
	protected String useYn;			//사용여부
	protected String delYn;			//삭제여부
	protected long rgtr;				//등록자
	protected Date rgtDttm;			//등록일
	protected long chnr;				//수정자
	protected Date chngDttm;			//수정일
	protected long dltr;				//삭제자
	protected Date dltDttm;			//삭제일

	/**
	 * 요청 카운트 증가 Builder
	 * @param rawSeq
	 * @param membSeq
	 */
	@Builder(builderClassName = "AddRequestCntBuilder", builderMethodName = "AddRequestCntBuilder")
	public RawDetailVO(long rawSeq, long membSeq) {
	    Assert.notNull(rawSeq, "rawSeq must not be null");
		Assert.notNull(membSeq, "membSeq must not be null");

		this.rawSeq = rawSeq;
		this.membSeq = membSeq;
	}

}
