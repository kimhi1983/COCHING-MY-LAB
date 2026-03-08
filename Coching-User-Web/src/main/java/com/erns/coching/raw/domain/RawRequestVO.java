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
public class RawRequestVO {

    private long rawRequestSeq;		//원료 요청 내역 SEQ
    private long rawDetailSeq;			//원료 자료 SEQ
    private long ptnSeq;			//원료사 SEQ
    private String ptnName;		    //회사명
    private String phone;			//연락처
    private String email;			//이메일
    private String address;			//샘플 수령 주소
    private String reqDetail;		//요청 사항
    private String messageType;		//요청 타입 쪽지
    private long rgtr;				//등록자
    private Date rgtDttm;			//등록일

    protected void setFromRawRequestSetDTO(RawRequestSetDTO fromDto) {
        this.rawDetailSeq = fromDto.getRawDetailSeq();
        this.ptnSeq = fromDto.getPtnSeq();
        this.ptnName = fromDto.getPtnName();
        this.phone = fromDto.getPhone();
        this.email = fromDto.getEmail();
        this.address = fromDto.getAddress();
        this.reqDetail = fromDto.getReqDetail();
        this.messageType = fromDto.getMessageType();
    }

    @Builder(builderClassName = "AddRequestBuilder", builderMethodName = "AddRequestBuilder")
    public RawRequestVO(RawRequestSetDTO fromDto) {
        Assert.notNull(fromDto, "fromDto must not be null");

        setFromRawRequestSetDTO(fromDto);
        this.rgtr = fromDto.getRgtr();

    }

}
