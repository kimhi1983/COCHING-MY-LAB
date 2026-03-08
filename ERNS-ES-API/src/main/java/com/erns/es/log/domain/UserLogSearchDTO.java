package com.erns.es.log.domain;

import com.erns.es.common.model.BaseSearchDTO;
import com.erns.es.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
/**
 * <p>사용자로그 검색</p>
 * @author heejinyu@erns.co.kr
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserLogSearchDTO extends BaseSearchDTO implements IReqDto {
	
	private String logDateRL; //YYYY-MM-DD hh24:mi:ss sss

	private long userSeq; 
	private String logType;
	private String logTypeRL;	
	private String siteType;
	
	private String userIdL;
	private String userIpL;
	

}
