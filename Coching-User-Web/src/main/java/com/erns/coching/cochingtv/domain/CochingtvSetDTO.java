package com.erns.coching.cochingtv.domain;

import javax.validation.constraints.NotNull;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * <p>코칭TV 설정 DTO</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CochingtvSetDTO extends AbstractReqDTO implements IReqDto{

	@NotNull(groups = {
	}, message = "tvSeq 누락")
	private long tvSeq;

	private String ytUrl;
	
	private long sortOrd;
	
	private String title;
	
	private String hashtag;

	private String views;
	
	private String ytDttm;
	
	private String useYn;
	
	private String delYn;
	
	private String category;
	
}
