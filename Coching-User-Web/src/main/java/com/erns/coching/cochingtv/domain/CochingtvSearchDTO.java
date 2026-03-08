package com.erns.coching.cochingtv.domain;

import javax.validation.constraints.NotNull;

import com.erns.coching.cochingtv.domain.vg.ValidCochingtv0012;
import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>코칭TV 검색</p>
 *
 * @author hw.park@erns.co.kr
 *
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CochingtvSearchDTO extends BaseSearchDTO implements IReqDto{
    
	@NotNull(groups = {
		ValidCochingtv0012.class
	}, message = "tvSeq 누락")
	private long tvSeq;

	private String ytUrl;
	
	private long sortOrd;
	
	private String titleL;
	
	private String hashtag;

	private String views;
	
	private String ytDttm;
	
	private String useYn;
	
	private String delYn;
	
	private String category;
	
	private String isRandom; //목록에서 랜덤 출력 여부
	private long excludeSeq; //목록에서 제외할 tvSeq (자기 자신 제외 시 활용)

}
