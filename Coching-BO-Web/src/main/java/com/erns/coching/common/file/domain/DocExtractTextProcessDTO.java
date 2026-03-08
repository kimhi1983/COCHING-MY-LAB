package com.erns.coching.common.file.domain;

import javax.validation.constraints.NotBlank;

import com.erns.coching.common.file.domain.vg.ValidDocProcess1011;
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
public class DocExtractTextProcessDTO extends AbstractReqDTO implements IReqDto {

  @NotBlank(groups = {
			ValidDocProcess1011.class
      , ValidDocProcess1019.class
		}, message = "srcFilePath 누락")
  private String srcFilePath;

  @Default
  private int method = 0; // 0: textExtractorService, 1: googleVisionApiService

}
