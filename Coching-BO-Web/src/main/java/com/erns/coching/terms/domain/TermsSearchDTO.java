package com.erns.coching.terms.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.terms.domain.vg.ValidTerms0003;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>약관 검색 DTO </p>
 * @author heejinyu@erns.co.kr
 *
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
public class TermsSearchDTO extends BaseSearchDTO implements IReqDto {

	protected String termsCd;
	protected String version;

	@NotBlank(groups = {
	        ValidTerms0003.class
	    }, message = "약관구분 누락")
	protected String termsType;

	protected String useYn;
	protected String requiredYn;

	@NotEmpty(groups = {
	        ValidTerms0003.class
	    }, message = "약관코드 누락")
	protected String[] termsCds;

	protected String sortColumn;
}
