package com.erns.coching.partner.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.erns.coching.board.domain.vg.ValidBoard0003;
import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
*
* <p>파트너사 설정 DTO</p>
*
* @author Kyungmin Lee
*
*/
@Getter
@Setter
@ToString
@NoArgsConstructor
public class PartnerSetDTO extends AbstractReqDTO implements IReqDto {
	
	private long ptnSeq;		//파트너사SEQ
	private String ptnName;		//파트너사명
	private String nation;		//파트너사명
	private String ptnLic;		//사업자번호
	private String pageUrl;		//홈페이지URL
	private String logoFileId;	//로고이미지

	@NotBlank(groups = {
            ValidBoard0003.class
        }, message = "삭제여부 누락")
    @Pattern(groups = {
    		ValidBoard0003.class
		}, message = "삭제여부 형식오류", regexp = "Y|N")
	private String delYn;			//삭제여부
	
	private String recruitYn;		// 대리점 모집 여부
	
}
