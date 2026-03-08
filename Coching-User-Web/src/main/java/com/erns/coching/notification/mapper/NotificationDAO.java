package com.erns.coching.notification.mapper;

import java.util.List;
import java.util.Map;

import com.erns.coching.notification.domain.NotificationSetDTO;
import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.notification.domain.NotificationSearchDTO;
import com.erns.coching.notification.domain.NotificationVO;


/**
 *
 * <p>원료목록 정보 Mapper</p>
 *
 * @author Kyungmin Lee
 *
 */
@Mapper
public interface NotificationDAO {

	public List<Map<String, Object>> getList(NotificationSearchDTO param);
	public int getListCount(NotificationSearchDTO param);

	public int insert(NotificationVO param);
	public int updateChkYn(NotificationSetDTO param);

}
