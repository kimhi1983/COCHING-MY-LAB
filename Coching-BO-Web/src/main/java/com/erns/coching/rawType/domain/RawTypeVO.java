package com.erns.coching.rawType.domain;

import org.springframework.util.Assert;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.login.domain.LoginUserDTO;

import lombok.Builder;
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
public class RawTypeVO extends BaseSearchDTO implements IReqDto {

	private long typeSeq = 0L;
	private String grpCode;        //그룹명
	private String code;            //타입코드
	private String codeNmKo;        //타입명 한글
	private String codeNmEn;        //타입명 영어
	private String inptType;		//입력 타입
	private String useYn;            //사용여부
	private Integer sortOrd = 0;			//정렬순서
	private long rgtr;            //등록자
	private String rgtDttm;        //등록일
	private	long chnr;			//수정자
	private String chngDttm;		//수정일

	/**
	 * 원료 타입 등록 Builder
	 * @param fromDto
	 */
	@Builder(builderClassName = "SetTypeBuilder", builderMethodName = "SetTypeBuilder")
	public RawTypeVO(RawTypeSetDTO fromDto) {
		Assert.notNull(fromDto, "fromDto must not be null");

		this.typeSeq = fromDto.getTypeSeq();
		this.grpCode = fromDto.getGrpCode();
		this.code = fromDto.getCode();
		this.codeNmKo = fromDto.getCodeNmKo();
		this.codeNmEn = fromDto.getCodeNmEn();
		this.inptType = fromDto.getInptType();
		this.useYn = fromDto.getUseYn();
		this.rgtr = fromDto.getRgtr();
		this.chnr = fromDto.getChnr();
	}
	
	/**
	 * 원료 상태수정 등록 Builder
	 * @param fromDto
	 */
	@Builder(builderClassName = "SetTypeStateBuilder", builderMethodName = "SetTypeStateBuilder")
	public RawTypeVO(RawTypeSetDTO fromDto, LoginUserDTO updateUser) {
		Assert.notNull(fromDto, "fromDto must not be null");

		this.typeSeq = fromDto.getTypeSeq();
		this.useYn = fromDto.getUseYn();
		
		this.chnr = updateUser.getSeq();
	}

}
