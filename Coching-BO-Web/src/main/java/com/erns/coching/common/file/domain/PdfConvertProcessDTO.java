package com.erns.coching.common.file.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import com.erns.coching.common.file.domain.vg.ValidDocProcess1012;
import com.erns.coching.common.file.domain.vg.ValidDocProcess1019;
import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdfConvertProcessDTO extends AbstractReqDTO implements IReqDto {

  @NotBlank(groups = {
			ValidDocProcess1012.class
      , ValidDocProcess1019.class
		}, message = "srcFilePath 누락")
  private String srcFilePath;

  @NotBlank(groups = {
			ValidDocProcess1012.class
      , ValidDocProcess1019.class
		}, message = "destFilePath 누락")
  private String destFilePath;

  @NotEmpty(groups = {
	//		ValidDocProcess1012.class
		}, message = "watermarkText 누락")
  private String watermarkText;
}
