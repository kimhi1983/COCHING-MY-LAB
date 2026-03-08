package com.erns.coching.message.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.message.domain.MessageSearchDTO;
import com.erns.coching.message.domain.MessageVO;
import com.erns.coching.message.mapper.MessageDAO;
import com.erns.coching.message.service.MessageService;

/**
 *
 * <p>쪽지 기본정보 Service</p>
 *
 * @author Kyungmin Lee
 *
 */
@Transactional
@Service
public class MessageServiceImpl implements MessageService {

	@Autowired
	private MessageDAO dao;

	@Override
	public List<Map<String, Object>> getList(MessageSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(MessageSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public Map<String, Object> load(MessageSearchDTO param) {
		return dao.load(param);
	}
	
	@Override
	public int insert(MessageVO param) {
		return dao.insert(param);
	}

	@Override
	public int updateState(MessageVO param) {
		return dao.updateState(param);
	}

	@Override
	public int updateSenderDelYn(Collection<MessageVO> list) {
		int retCnt = 0;
		
		for(MessageVO param : list) {
			retCnt =+ dao.updateReceiverDelYn(param);
		}
		
		return retCnt; 
	}

	@Override
	public int updateReceiverDelYn(Collection<MessageVO> list) {
		int retCnt = 0;
		
		for(MessageVO param : list) {
			retCnt =+ dao.updateReceiverDelYn(param);
		}
		
		return retCnt; 
	}

	@Override
	public int delete(long messageSeq) {
		return dao.delete(messageSeq);
	}
}
