package com.erns.coching.terms.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.terms.domain.vg.ValidTerms0001;
import com.erns.coching.terms.domain.vg.ValidTerms0002;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * <p>약관정보 설정 DTO</p>
 *
 * @author Hunwoo Park
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TermsSetDTO extends AbstractReqDTO implements IReqDto {

	@NotBlank(groups = {
        ValidTerms0001.class,
        ValidTerms0002.class
    }, message = "약관코드 누락")
	private String termsCd;

	@NotBlank(groups = {
        ValidTerms0002.class
    }, message = "버전 누락")
	private String version;

	@NotBlank(groups = {
        ValidTerms0001.class
    }, message = "신규버전 누락")
	private String newVersion;

	@NotBlank(groups = {
        ValidTerms0001.class
    }, message = "구버전 누락")
	private String oldVersion;

	@NotBlank(groups = {
        ValidTerms0001.class,
        ValidTerms0002.class
    }, message = "약관구분 누락")
	private String termsType;

	@NotBlank(groups = {
        ValidTerms0001.class,
        ValidTerms0002.class
    }, message = "약관내용 누락")
	private String content;


    @Pattern(groups = {
		ValidTerms0001.class,
		ValidTerms0002.class
	},
	message = "사용여부 형식오류(Y/N) ",regexp = "Y|N")
	private String useYn;

    @Pattern(groups = {
		ValidTerms0001.class,
		ValidTerms0002.class
	},
	message = "필수여부 형식오류(Y/N)", regexp = "Y|N")
	private String requiredYn;

    @NotBlank(groups = {
            ValidTerms0001.class,
            ValidTerms0002.class
    }, message = "약관게시시작일 누락")
    private String fromDate;

    @NotBlank(groups = {
            ValidTerms0001.class,
            ValidTerms0002.class
    }, message = "약관게시종료일 누락")
    private String toDate;

    private String remarks;

}
