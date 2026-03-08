package com.erns.coching.rawType.domain;

import javax.validation.constraints.NotNull;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.rawType.domain.vg.ValidRawType0032;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
*
* <p>원료 마스터 설정 DTO</p>
*
* @author Kyungmin Lee
*
*/
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RawTypeSetDTO extends AbstractReqDTO implements IReqDto {

    @NotNull(groups = {
		ValidRawType0032.class
    }, message = "typeSeq 누락")
	private long typeSeq;
	private String grpCode;        //그룹명
	private String code;            //타입코드
	private String codeNmKo;        //타입명 한글
	private String codeNmEn;        //타입명 영어
	private String inptType;		//입력타입
	private String useYn;            //사용여부
	private long rgtr;            //등록자
	private	long chnr;			//수정자

}
