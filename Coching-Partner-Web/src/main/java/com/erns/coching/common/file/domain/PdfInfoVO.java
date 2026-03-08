package com.erns.coching.common.file.domain;

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
*
* <p>PDF 정보 VO
* t_pdf_info</p>
*
* @author Hunwoo Park
*
*/
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class PdfInfoVO {

	protected String fileId;

	protected String fileName;
	protected String fileExt;
	protected String fileNameDest;
	protected String filePath;
	protected long fileSize;
  protected Date rgtDttm;

  protected long pageCount;
  protected String pageImgPath;  
  protected String content;	
	protected Date contentDttm;

}
