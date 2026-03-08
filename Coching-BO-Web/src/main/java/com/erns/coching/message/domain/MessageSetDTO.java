package com.erns.coching.message.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.message.domain.vg.ValidMessage0020;
import com.erns.coching.message.domain.vg.ValidMessage0030;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
*
* <p>쪽지 설정 DTO</p>
*
* @author Kyungmin Lee
*
*/
@Getter
@Setter
@ToString
@NoArgsConstructor
public class MessageSetDTO extends AbstractReqDTO implements IReqDto {
	
	@NotBlank(groups = {
			ValidMessage0030.class,
    	}, message = "messageSeq 누락")
    @Min(groups = {
    		ValidMessage0030.class,
		},
		value = 1,
		message = "messageSeq 누락")
	private long messageSeq;		//쪽지 SEQ
	
	@NotBlank(groups = {
			ValidMessage0020.class,
			ValidMessage0030.class,
    	}, message = "sender 누락")
    @Min(groups = {
    		ValidMessage0020.class,
    		ValidMessage0030.class,
		},
		value = 1,
		message = "sender 누락")
	private long sender;			//발신자
	
	@NotBlank(groups = {
			ValidMessage0020.class,
			ValidMessage0030.class,
    	}, message = "receiver 누락")
    @Min(groups = {
    		ValidMessage0020.class,
    		ValidMessage0030.class,
		},
		value = 1,
		message = "receiver 누락")	
	private long receiver;			//수신자
	
	@NotBlank(groups = {
			ValidMessage0020.class,
			ValidMessage0030.class,
    	}, message = "content 누락")
	private String content;			//내용
	
	private String state;			//상태
	
    private long refSeq;  			//참조 SEQ
}
