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
public class RawRequestTypeVO {

    private long rawRequestSeq;		//원료 요청 내역 SEQ
    private String code;            //요청 타입
    private long rgtr;				//등록자
    private Date rgtDttm;			//등록일

    protected void setFromRawRequestTypeSetDTO(RawRequestTypeSetDTO fromDto) {
        this.rawRequestSeq = fromDto.getRawRequestSeq();
        this.code = fromDto.getCode();
    }

    @Builder(builderClassName = "AddRequestTypeBuilder", builderMethodName = "AddRequestTypeBuilder")
    public RawRequestTypeVO(RawRequestTypeSetDTO fromDto, long seq) {
        Assert.notNull(fromDto, "fromDto must not be null");

        setFromRawRequestTypeSetDTO(fromDto);
        this.rgtr = seq;

    }

}
