package com.erns.coching.code.domain;

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * <p>공통코드 VO
 * T_CODE_INF</p>
 *
 * @author Hunwoo Park
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class CodeVO {

    private String code;
    private String grpCode;
    private String codeName;
    private String etc1;
    private String etc2;
    private String etc3;
    private String etc4;
    private String etc5;
    private String etcCmmt;
    private String codeDesc;
    private String remarks;
    private int sortOrd;
    private String useYn;

    private long rgtr;
    private Date rgtDttm;
    private long chnr;
    private Date chngDttm;
}
