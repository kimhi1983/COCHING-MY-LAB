package com.erns.coching.common.file.domain;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * PDF 정보 검색 DTO
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class PdfInfoSearchDTO extends BaseSearchDTO implements IReqDto{
  
  private String fileId;
	private String[] fileIds;

	private String fileExt;
	private String[] fileExts;
}
