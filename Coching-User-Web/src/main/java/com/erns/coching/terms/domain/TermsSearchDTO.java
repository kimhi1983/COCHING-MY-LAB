package com.erns.coching.terms.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.terms.domain.vg.ValidTerms0001;
import com.erns.coching.terms.domain.vg.ValidTerms0002;

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
public class TermsSearchDTO extends BaseSearchDTO  implements IReqDto {
	
	protected String[] termsCds;

	protected String termsCd;
	
	@NotBlank(groups = {ValidTerms0001.class}, message = "locale 누락")
	protected String locale;
	
	@NotEmpty(groups = {ValidTerms0001.class}, message = "vTermsCds 누락")
	protected String[] localeTermsCds;

	@NotBlank(groups = {ValidTerms0002.class}, message = "vTermsCd 누락")	
	protected String localeTermsCd;
	
	@Pattern(groups = {
			ValidTerms0001.class,
			ValidTerms0002.class
	}, message = "useYn 포멧오류(Y|N)", regexp = "Y|N")		
	protected String useYn = "Y";
	
	protected String requiredYn;	
	protected String version;	
	protected String termsType;
	
}
