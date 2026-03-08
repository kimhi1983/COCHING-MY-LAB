package com.erns.coching.code.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.erns.coching.code.domain.vg.ValidGroupCode0001;
import com.erns.coching.code.domain.vg.ValidGroupCode0002;
import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * <p>공통코드 검색 DTO</p>
 *
 * @author Hunwoo Park
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CodeSearchDTO extends BaseSearchDTO implements IReqDto {

    @NotBlank(groups = {
        ValidGroupCode0002.class
    }, message = "grpCode 누락")
    private String grpCode;

    private String code;

    private String etc1;

    private String etc2;

    private String etc3;

    private String etc4;

//    @NotBlank(groups = {
//            ValidGroupCode0002.class
//    }, message = "etc5 누락")
    @Pattern(groups = {ValidGroupCode0002.class},
    		message = "etc5 형식오류",
    		regexp = "(?i)(en|ko)|^\\s*$")
    private String etc5;

    @Pattern(groups = {ValidGroupCode0001.class},
    		message = "사용여부 누락",
    		regexp = "Y|N")
    private String useYn;

}
