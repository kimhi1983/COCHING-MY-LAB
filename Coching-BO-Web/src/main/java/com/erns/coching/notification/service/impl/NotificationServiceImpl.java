package com.erns.coching.notification.service.impl;

import java.util.List;
import java.util.Map;

import com.erns.coching.common.type.NotificationType;
import com.erns.coching.notification.domain.NotificationSetDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.notification.domain.NotificationSearchDTO;
import com.erns.coching.notification.domain.NotificationVO;
import com.erns.coching.notification.mapper.NotificationDAO;
import com.erns.coching.notification.service.NotificationService;

/**
 *
 * <p>알림목록 기본정보 Service</p>
 *
 * @author Kyungmin Lee
 *
 */
@Slf4j
@Transactional
@Service
public class NotificationServiceImpl implements NotificationService {

	@Autowired
	private NotificationDAO dao;

	@Override
	public List<Map<String, Object>> getList(NotificationSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(NotificationSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public int insert(NotificationVO param) {
		return dao.insert(param);
	}

	@Override
	public int updateChkYn(NotificationSetDTO param) {
		return dao.updateChkYn(param);
	}

	@Override
	public void insertNoti(NotificationType notiType, long refSeq, long membSeq, String title, String detail, long rgtr){

		try {
			String content = "";
			if(NotificationType.REQUEST.equals(notiType) || NotificationType.INQUIRY_SEND.equals(notiType)) {
				content = detail;
			}else if(NotificationType.SOURCING_CMT.equals(notiType)){
				String splitTitle = (title.length() > 6) ? title.substring(0, 6) + "..." : title;
				content = "[" + splitTitle + "]" + detail;
			}else{
				content = "[" + title + "]" + detail;
			}

			NotificationVO notificationVO = NotificationVO
					.AddNotificationBuilder()
					.membSeq(membSeq)
					.refCode(notiType.getCode())
					.refSeq(refSeq)
					.content(content)
					.rgtr(rgtr)
					.build();

			insert(notificationVO);
		}catch (Exception e){
			log.error("Error! addNotifiaction", e);
		}
	}
}
