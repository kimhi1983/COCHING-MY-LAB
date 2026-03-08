package com.erns.coching.raw.domain;

import java.util.Date;

import org.springframework.util.Assert;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

	protected long rawDetailSeq;	//원료 상세 SEQ
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

	protected void setFromRawElmSetDTO(RawDetailSetDTO fromDto) {
		this.rawSeq = fromDto.getRawSeq();
		this.membSeq = fromDto.getMembSeq();
		this.title = fromDto.getTitle();
		this.hashtag = fromDto.getHashtag();
		this.fileId = fromDto.getFileId();
		this.rawDesc = fromDto.getRawDesc();
		this.rawDetail = fromDto.getRawDetail();
		this.useYn= fromDto.getUseYn();
		this.delYn= fromDto.getDelYn();
	}

	/**
	 * 원료 설명 등록 Builder
	 * @param fromDto
	 */
	@Builder(builderClassName = "SetRawDetailBuilder", builderMethodName = "SetRawDetailBuilder")
	public RawDetailVO(RawDetailSetDTO fromDto) {
	    Assert.notNull(fromDto, "fromDto must not be null");

	    this.setFromRawElmSetDTO(fromDto);
		this.rgtr = fromDto.getMembSeq();
		this.chnr = fromDto.getMembSeq();
	}

	/**
	 * 원료 허용,사용,삭제여부 변경 Builder
	 * @param fromDto
	 */
	@Builder(builderClassName = "UpdateRawDetailYnBuilder", builderMethodName = "UpdateRawDetailYnBuilder")
	public RawDetailVO(RawDetailSetDTO fromDto, long seq) {
		Assert.notNull(fromDto, "fromDto must not be null");

		this.rawDetailSeq = fromDto.getRawDetailSeq();
		this.permitYn = fromDto.getPermitYn();
		this.useYn = fromDto.getUseYn();
		this.delYn = fromDto.getDelYn();
		this.chnr = seq;
		this.dltr = seq;
	}

	/**
	 * 원료 설명 변경 Builder
	 * @param fromDto
	 */
	@Builder(builderClassName = "ChangeRawDetailBuilder", builderMethodName = "ChangeRawDetailBuilder")
	public RawDetailVO(RawDetailVO fromDto, long membSeq, long seq) {
		Assert.notNull(fromDto, "fromDto must not be null");

		this.rawSeq = fromDto.getRawSeq();
		this.membSeq = membSeq;
		this.title = fromDto.getTitle();
		this.hashtag = fromDto.getHashtag();
		this.fileId = fromDto.getFileId();
		this.rawDesc = fromDto.getRawDesc();
		this.rawDetail = fromDto.getRawDetail();
		this.useYn= fromDto.getUseYn();
		this.rgtr = seq;
	}
}
