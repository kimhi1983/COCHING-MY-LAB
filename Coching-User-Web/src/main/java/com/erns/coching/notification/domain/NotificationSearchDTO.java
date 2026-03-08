package com.erns.coching.notification.domain;

import java.util.Date;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>알림목록 검색 DTO</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class NotificationSearchDTO extends BaseSearchDTO implements IReqDto {

    private long notiSeq;		//알림 SEQ
    private long membSeq;		//회원 SEQ 
    private String refCode;		//참조 CD 
    private long refSeq;		//참조 SEQ  
    private String content;		//내용 
    private String chkYn;		//확인 여부
    private long rgtr;			//등록자
    private Date rgtDttm;		//등록일 
    
}
