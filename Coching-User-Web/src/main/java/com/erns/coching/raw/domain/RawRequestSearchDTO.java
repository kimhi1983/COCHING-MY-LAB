package com.erns.coching.raw.domain;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>원료 성분 검색 DTO</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RawRequestSearchDTO extends BaseSearchDTO implements IReqDto {

	 private long rawRequestSeq;		//원료 성분 SEQ
	 private long membSeq;				//사용자
	 private long managerSeq;			//담당자
	 private long rawDetailSeq;			//원료 SEQ
	 private String rawName;			//원료 이름
	 private String title;				//타이틀
	 private long ptnSeq;				//파트너 SEQ

}
