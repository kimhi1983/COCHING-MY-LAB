package com.erns.coching.mail.mapper;

import com.erns.coching.mail.domain.MailVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 *
 * <p>Push 전송 Mapper</p>
 *
 * @author Hunwoo Park
 *
 */
@Mapper
public interface MailDAO {

	public int insert(MailVO param);

	public int updateSuccess(MailVO param);
	public int updateFail(MailVO param);

	public List<MailVO> selectWillSend(int maxCount);
}
