package com.erns.coching.message.service;

import java.util.List;
import java.util.Map;

import com.erns.coching.message.domain.MessageSearchDTO;
import com.erns.coching.message.domain.MessageVO;

/**
 *
 * <p>쪽지 기본정보 Service</p>
 *
 * @author Kyungmin Lee
 *
 */
public interface MessageService {

	public List<Map<String, Object>> getList(MessageSearchDTO param);
	public int getListCount(MessageSearchDTO param);

	public Map<String, Object> load(MessageSearchDTO param);

	public int insert(MessageVO param);
	
	public int updateState(MessageSearchDTO param);
	
	public int delete(long messageSeq);
	
}
