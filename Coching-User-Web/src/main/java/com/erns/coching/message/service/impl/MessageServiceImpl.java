package com.erns.coching.message.service.impl;

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
	public int updateUserDelYn(MessageSearchDTO param) {
		List<MessageVO> delList = dao.getListVo(param);
		
		int delCnt = 0;
		long actMembSeq = param.getUserMembSeq();
		
		
		for(MessageVO mvo : delList) {
			int ret = 0;
			//발신자 삭제
			if (actMembSeq == mvo.getSender()){
				MessageVO delParam = MessageVO.builder()
						.senderDelYn("Y")
						.messageSeq(mvo.getMessageSeq())
						.sender(actMembSeq)
						.build();

				ret = dao.updateSenderDelYn(delParam); //쪽지 삭제
			}

			//수신자 삭제
			if (actMembSeq == mvo.getReceiver()){
				MessageVO delParam = MessageVO.builder()
						.receiverDelYn("Y")
						.messageSeq(mvo.getMessageSeq())
						.receiver(actMembSeq)
						.build();

				ret = dao.updateReceiverDelYn(delParam); //쪽지 삭제
			}			
			delCnt += ret;
		}
		
		return delCnt;
	}

	@Override
	public int delete(long messageSeq) {
		return dao.delete(messageSeq);
	}	
}
