package com.erns.coching.message.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.message.domain.vg.ValidMessage0011;
import com.erns.coching.message.domain.vg.ValidMessage0012;
import com.erns.coching.message.domain.vg.ValidMessage0041;
import com.erns.coching.message.domain.vg.ValidMessage0042;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@AllArgsConstructor
public class MessageSearchDTO extends BaseSearchDTO implements IReqDto {

	@Min(groups = {
			ValidMessage0012.class,			
		},
		value = 1,
		message = "messageSeq 누락")
	private long messageSeq;		//쪽지 SEQ
	
	@NotNull(groups = {
			ValidMessage0041.class,
			ValidMessage0042.class,
		},
		message = "messageSeqs 누락")
	private long[] messageSeqs;		//쪽지 SEQ
		
	private long sender;			//발신자
	private long receiver;			//수신자
	
	private String contentL;		//내용	
	private String state;			//상태
	
	private String refCode;			//참조 코드
	private long refSeq;	 		//참조 SEQ
    
	private String receiverDelYn;	//삭제 여부
	private String senderDelYn;		//삭제 여부
	private String blockYn;			//차단 여부
	
	@NotBlank(groups = {
		ValidMessage0011.class,
		ValidMessage0012.class,
	}, message = "mode 누락")
	private String mode;	//모드 ('sent', 'recv')	
	
	private long userMembSeq;		//사용자 SEQ
}
