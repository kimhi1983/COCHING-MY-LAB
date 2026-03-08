package com.erns.coching.message.domain;

import java.util.Date;

import org.springframework.util.Assert;

import com.erns.coching.login.domain.LoginUserDTO;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * <p>쪽지 VO
 * T_MESSAGE_INF</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class MessageVO {

	private long messageSeq;		//쪽지 SEQ
	private long sender;			//발신자
	private long receiver;			//수신자
	private String content;			//내용
	private String state;			//상태
    private long refSeq;  			//참조 SEQ
	private String delYn;			//삭제 여부
	private String blockYn;			//차단 여부
	private Date rgtDttm;			//등록일

	protected void setFromMessageSetDTO(MessageSetDTO fromDto) {
		this.messageSeq = fromDto.getMessageSeq();
		this.sender = fromDto.getSender();
		this.receiver = fromDto.getReceiver();
		this.content = fromDto.getContent();
		this.state = fromDto.getState();
		this.refSeq = fromDto.getRefSeq();
	}

	/**
	 * 쪽지 등록 Builder
	 * @param fromDto
	 */
	@Builder(builderClassName = "AddMessageBuilder", builderMethodName = "AddMessageBuilder")
	public MessageVO(MessageSetDTO fromDto, LoginUserDTO addUser) {
	    Assert.notNull(fromDto, "fromDto must not be null");
	    
	    this.setFromMessageSetDTO(fromDto);
		this.sender = addUser.getSeq();
	}
	

}
