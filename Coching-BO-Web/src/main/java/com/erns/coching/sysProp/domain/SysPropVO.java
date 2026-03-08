package com.erns.coching.sysProp.domain;

import java.util.Date;

import org.springframework.util.Assert;

import com.erns.coching.login.domain.LoginUserDTO;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <p>시스템설정 SysPropVO</p>
 * 
 * @author 	jsjeong@erns.co.kr
 *
 */
@Getter	
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class SysPropVO {
										 
	private String sysKey;
	private String sysValue;
	
	private long rgtr;                  //RGTR      
	private Date rgtDttm;               //RGT_DTTM          
	private long chnr;                  //CHNR     
	private Date chngDttm;              //CHNG_DTTM

	protected void setFromSysPropSetDTO(SysPropSetDTO fromDto) {
		this.sysKey = fromDto.getSysKey();
	    this.sysValue = fromDto.getSysValue();
	}
	
	@Builder(builderClassName = "setSysPropBuilder", builderMethodName = "setSysPropBuilder") 
	public SysPropVO( SysPropSetDTO fromDto, LoginUserDTO updateUser) {
	    Assert.notNull(fromDto, "fromDto must not be null");
	    Assert.notNull(updateUser, "updateUser must not be null");
	    
	    this.setFromSysPropSetDTO(fromDto);
	    
		this.rgtr = updateUser.getSeq();
		this.chnr = updateUser.getSeq();
	}
}
