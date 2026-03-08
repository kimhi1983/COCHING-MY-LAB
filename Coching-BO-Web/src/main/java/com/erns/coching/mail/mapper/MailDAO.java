package com.erns.coching.mail.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.mail.domain.MailBatchVO;
import com.erns.coching.mail.domain.MailVO;

/**
 * 메일전송
 */
@Mapper
public interface MailDAO {

	public int updateSuccess(MailBatchVO param);
	public int updateFail(MailBatchVO param);
	public List<MailBatchVO> selectWillSend(int maxCount);
	
	public int insert(MailVO param);
}
