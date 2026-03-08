package com.erns.coching.log.domain;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.log.domain.vg.ValidUserRecentInfo0011;
import com.erns.coching.log.domain.vg.ValidUserRecentInfo0040;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>사용자 최근 정보 검색 DTO</p>
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserRecentInfoSearchDTO extends BaseSearchDTO implements IReqDto {

  private long membSeq;   //사용자 SEQ

  @NotBlank(groups = {
	  		ValidUserRecentInfo0011.class
    	}, message = "참조코드 누락")
  @Pattern(groups = {
        ValidUserRecentInfo0011.class
    	}, message = "참조코드 형식오류", regexp = "raw|prod")    
  private String refCode; //참조 CD
  
  private String refSeq1; //참조 SEQ1

   @NotNull(groups = {
        ValidUserRecentInfo0040.class,        },
        message = "refSeq1s 누락")
  private String[] refSeq1s; //참조 SEQ1

  private long refSeq2;   //참조 SEQ2  
  private long refSeq3;   //참조 SEQ3
  private Date rgtDttm;		//등록일


  
}
