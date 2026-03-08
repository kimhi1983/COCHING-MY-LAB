package com.erns.coching.rawType.domain;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>원료 마스터 검색 DTO</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RawTypeSearchDTO extends BaseSearchDTO implements IReqDto {

	 private String allSrch;

	private long typeSeq;
	private String grpCode;
	private String grpCodeNm;        //그룹명
	private String code;            //타입코드
	private String codeNmKo;        //타입명 한글
	private String codeNmEn;        //타입명 영어
	private String useYn;            //사용여부
}
