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
* <p>첨부파일 VO
* T_FILE_INF</p>
*
* @author Hunwoo Park
*
*/
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class FileVO {

	protected String fileId;

	protected String refCode;
	protected long refSeq;
	protected int sortOrd;
	protected String fileName;
	protected String fileExt;
	protected String fileNameDest;
	protected String filePath;
	protected long fileSize;
	protected String delYn;

	protected long rgtr;
	protected Date rgtDttm;
	protected long chnr;
	protected Date chngDttm;

}
