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
 * <p>원료 요청 내역 VO
 * T_RAW_REQUEST</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class RawRequestReplyVO {

    private long rawReplySeq;		//원료 요청 답변 내역 SEQ
    private long rawRequestSeq;		//원료 요청 내역 SEQ
    private String dispatchCode;    //요청 타입
    private String dispatchDate;    //날짜 직접입력
    private String contents;        //메세지
    private long refSeq;            //상위 seq
    private int replyCmtOrd;        //depth 별 순서
    private String delYn;           //삭제여부
    private long rgtr;				//등록자
    private Date rgtDttm;			//등록일
    private long chnr;              //수정자
    private Date chngDttm;           //수정일

    protected void setFromRawRequestTypeSetDTO(RawRequestReplySetDTO fromDto) {
        this.rawRequestSeq = fromDto.getRawRequestSeq();
        this.dispatchCode = fromDto.getDispatchCode();
        this.dispatchDate = fromDto.getDispatchDate();
        this.contents = fromDto.getContents();
        this.refSeq = fromDto.getRefSeq();
        this.replyCmtOrd = fromDto.getReplyCmtOrd();
        this.delYn = fromDto.getDelYn();
        this.rgtr = fromDto.getRgtr();
        this.chnr = fromDto.getChnr();
    }

    @Builder(builderClassName = "AddRequestReplyBuilder", builderMethodName = "AddRequestReplyBuilder")
    public RawRequestReplyVO(RawRequestReplySetDTO fromDto) {
        Assert.notNull(fromDto, "fromDto must not be null");

        setFromRawRequestTypeSetDTO(fromDto);
    }

}
