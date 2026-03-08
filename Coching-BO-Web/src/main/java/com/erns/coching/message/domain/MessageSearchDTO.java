package com.erns.coching.message.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.message.domain.vg.ValidMessage0011;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>쪽지 검색 DTO</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class MessageSearchDTO extends BaseSearchDTO implements IReqDto {

	@NotBlank(groups = {
			ValidMessage0011.class,
    	}, message = "messageSeq 누락")
    @Min(groups = {
    		ValidMessage0011.class,
		},
		value = 1,
		message = "messageSeq 누락")
	private long messageSeq;		//쪽지 SEQ
		
	private long sender;			//발신자
	private long receiver;			//수신자
	
	private String contentL;		//내용	
	private String state;			//상태
	
//	private String refCode;			//참조 코드
//	private long refSeq;	 		//참조 SEQ
//    
//	private String delYn;			//삭제 여부
//	private String blockYn;			//차단 여부
	
	private String mode;	//모드 ('sent', 'recv')
}
