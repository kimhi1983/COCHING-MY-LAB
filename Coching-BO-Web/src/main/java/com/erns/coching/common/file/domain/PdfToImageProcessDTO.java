package com.erns.coching.common.file.domain;

import javax.validation.constraints.NotBlank;

import com.erns.coching.common.file.domain.vg.ValidDocProcess1013;
import com.erns.coching.common.file.domain.vg.ValidDocProcess1019;
import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
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
public class PdfToImageProcessDTO extends AbstractReqDTO implements IReqDto  {

  @NotBlank(groups = {
			ValidDocProcess1013.class
      ,ValidDocProcess1019.class
		}, message = "srcFilePath 누락")
  private String srcFilePath;

  @NotBlank(groups = {
			ValidDocProcess1013.class
      ,ValidDocProcess1019.class
		}, message = "destPath 누락")
  private String destPath;

  @Default
  private int dpi = 300;

}
