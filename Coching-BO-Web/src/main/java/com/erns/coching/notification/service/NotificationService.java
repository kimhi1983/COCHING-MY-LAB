package com.erns.coching.notification.service;

import java.util.List;
import java.util.Map;

import com.erns.coching.common.type.NotificationType;
import com.erns.coching.notification.domain.NotificationSearchDTO;
import com.erns.coching.notification.domain.NotificationSetDTO;
import com.erns.coching.notification.domain.NotificationVO;

/**
 *
 * <p>알림목록 기본정보 Service</p>
 *
 * @author Kyungmin Lee
 *
 */
public interface NotificationService {

	public List<Map<String, Object>> getList(NotificationSearchDTO param);
	public int getListCount(NotificationSearchDTO param);

	public int insert(NotificationVO param);
	public int updateChkYn(NotificationSetDTO param);

	public void insertNoti(NotificationType notiType, long refSeq, long membSeq, String title, String detail, long rgtr);
}
