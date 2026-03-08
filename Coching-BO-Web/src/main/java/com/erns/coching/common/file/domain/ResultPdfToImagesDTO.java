package com.erns.coching.common.file.domain;

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
public class ResultPdfToImagesDTO {

  @Default
  private boolean success = false;
  private String errorMessage;

  private PdfToImageProcessDTO requestParam;
  private int totalPage;
  private String[] imagesPath;
}
