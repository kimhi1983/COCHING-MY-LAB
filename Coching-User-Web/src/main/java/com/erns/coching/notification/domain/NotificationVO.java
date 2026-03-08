package com.erns.coching.notification.domain;

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.Assert;

/**
 *
 * <p>알림 목록 VO
 * T_NOTIFICATION</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class NotificationVO {

    private long notiSeq;		//알림 SEQ
    private long membSeq;		//받는사람
    private String refCode;		//참조 CD
    private long refSeq;		//참조 SEQ
    private String content;		//내용
    private String chkYn;		//확인 여부
    private long rgtr;			//보내는사람
    private Date rgtDttm;		//등록일

	protected void setFromNotificationSetDTO(NotificationSetDTO fromDto) {
		this.notiSeq = fromDto.getNotiSeq();
		this.membSeq = fromDto.getMembSeq();
		this.refCode = fromDto.getRefCode();
		this.refSeq = fromDto.getRefSeq();
		this.content = fromDto.getContent();
		this.chkYn = fromDto.getChkYn();
	}

    /**
     * 알림 등록
     * @param membSeq
     * @param refCode
     * @param refSeq
     * @param content
     * @param rgtr
     */
    @Builder(builderClassName = "AddNotificationBuilder", builderMethodName = "AddNotificationBuilder")
    public NotificationVO(long membSeq, String refCode, long refSeq, String content, long rgtr) {
        Assert.notNull(membSeq, "membSeq must not be null");
        Assert.notNull(refCode, "refCode must not be null");

        this.membSeq = membSeq;
        this.refCode = refCode;
        this.refSeq = refSeq;
        this.content = content;
        this.rgtr = rgtr;
    }

}
