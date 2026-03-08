package com.erns.coching.common.file.domain;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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
public class DocProcessDTO extends AbstractReqDTO implements IReqDto {

  @Valid
  private PdfConvertProcessDTO pdfConvert;

  @Valid
  @NotNull(groups = {
			ValidDocProcess1019.class
		}, message = "docExtractText")
  private DocExtractTextProcessDTO docExtractText;

  @Valid
  @NotNull(groups = {
			ValidDocProcess1019.class
		}, message = "pdfToImage")
  private PdfToImageProcessDTO pdfToImage;
}
