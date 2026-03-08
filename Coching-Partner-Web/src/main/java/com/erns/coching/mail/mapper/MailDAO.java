package com.erns.coching.mail.mapper;

import com.erns.coching.mail.domain.MailVO;
import org.apache.ibatis.annotations.Mapper;

/**
*
* <p>Mail 전송 Mapper</p>
*
* @author Hunwoo Park
*
*/
@Mapper
public interface MailDAO {

	public int insert(MailVO param);

}
