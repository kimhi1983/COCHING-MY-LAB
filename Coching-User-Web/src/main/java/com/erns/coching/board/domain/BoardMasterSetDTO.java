package com.erns.coching.board.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * <p>게시판마스터 DTO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class BoardMasterSetDTO extends AbstractReqDTO implements IReqDto{

	@NotBlank(groups = {
        
    }, message = "게시판마스터아이디 누락")
	private String boardMstId; 
	
	@NotBlank(groups = {
		
    }, message = "게시판명 누락")
	private String boardName;
	
	@NotBlank(groups = {
		
    }, message = "게시판형태 누락")
	private String boardType;
	
    @Pattern(groups = {},
	message = "사용여부 누락",
	regexp = "Y|N")		
	private String useYn = "Y"; 
	    
	private String boardDesc;    
	    
}
