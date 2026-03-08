package com.erns.coching.join.domain;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>약관 동의 DTO</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TermsAgreeDTO extends AbstractReqDTO implements IReqDto {

	private long membSeq; 	 	//MEMB_SEQ

	private String termsCd;		//TERMS_CD

	private String agreeYn;		//ARGEE_YN
}
