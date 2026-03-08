package com.erns.coching.common.file.domain;

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
public class ResultPdfAllProcessDTO {

  private ResultConvertToPdfDTO convertToPdf;

  private ResultExtractTextDTO extractText;
  
  private ResultPdfToImagesDTO pdfToImages;
}
