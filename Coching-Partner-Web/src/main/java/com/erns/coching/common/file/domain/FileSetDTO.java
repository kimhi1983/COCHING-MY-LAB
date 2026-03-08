package com.erns.coching.common.file.domain;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * <p>파일 저장용 DTO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class FileSetDTO extends AbstractReqDTO implements IReqDto{


	private String fileId;

	private String refcode;
	private long refSeq;
	private int sortOrd;
	private String fileName;
	private String fileExt;
	private String fileNameDest;
	private String filePath;
	private long fileSize;
	private String delYn;

	private long rgtr;
	private long chnr;

}
