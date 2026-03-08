package com.erns.coching.member.domain;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.member.domain.vg.ValidAdminMember0032;
import com.erns.coching.member.domain.vg.ValidMember0032;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * <p>사용자 복수 설정 DTO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class MemberMultipleSetDTO extends AbstractReqDTO implements IReqDto {

	@NotEmpty(groups = {
			ValidMember0032.class,
			ValidAdminMember0032.class
    	}, message = "list 누락")
	private List<MemberSetDTO> list;

}