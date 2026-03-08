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
 * <p>공통코드 마스터 VO
 * T_CODE_MST</p>
 *
 * @author Hunwoo Park
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class CodeMasterVO {

	private String grpCode;			//그룹코드
	private String grpCodeName;		//그룹코드명
	private String codeDesc;		//그룹코드설명
	private String etcCmmt;			//기타코드설명
	private String useYn;			//사용여부
	private String delYn;			//삭제여부
    
    private long rgtr;              //등록자
    private Date rgtDttm;           //등록일
    private long chnr;              //수정자
    private Date chngDttm;          //수정일
}
