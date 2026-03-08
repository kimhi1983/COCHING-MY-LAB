package com.erns.coching.message.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.message.domain.MessageSearchDTO;
import com.erns.coching.message.domain.MessageVO;


/**
 *
 * <p>쪽지 정보 Mapper</p>
 *
 * @author Kyungmin Lee
 *
 */
@Mapper
public interface MessageDAO {

	public List<MessageVO> getListVo(MessageSearchDTO param);
	public List<Map<String, Object>> getList(MessageSearchDTO param);
	public int getListCount(MessageSearchDTO param);

	public Map<String, Object> load(MessageSearchDTO param);

	public int insert(MessageVO param);
	
	public int updateState(MessageVO param);
	public int updateSenderDelYn(MessageVO param);
	public int updateReceiverDelYn(MessageVO param);
	
	public int delete(long messageSeq);

}
