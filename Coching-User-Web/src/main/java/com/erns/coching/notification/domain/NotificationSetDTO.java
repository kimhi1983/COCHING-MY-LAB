package com.erns.coching.notification.domain;

import java.util.Date;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;

import com.erns.coching.notification.domain.vg.ValidSetNotification0001;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
*
* <p>알림목록 설정 DTO</p>
*
* @author Kyungmin Lee
*
*/
@Getter
@Setter
@ToString
@NoArgsConstructor
public class NotificationSetDTO extends AbstractReqDTO implements IReqDto {

    @NotNull(groups = {ValidSetNotification0001.class},
            message = "notiSeq 누락")
    private long notiSeq;		//알림 SEQ
    private long membSeq;		//회원 SEQ
    private String refCode;		//참조 CD
    private long refSeq;		//참조 SEQ
    private String content;		//내용
    @NotNull(groups = {ValidSetNotification0001.class},
            message = "chkYn 누락")
    private String chkYn;		//확인 여부
    private long rgtr;			//등록자
    private Date rgtDttm;		//등록일

}
