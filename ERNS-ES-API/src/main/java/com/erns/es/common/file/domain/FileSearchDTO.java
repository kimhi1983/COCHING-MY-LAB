package com.erns.es.common.file.domain;

import com.erns.es.common.model.BaseSearchDTO;
import com.erns.es.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * <p>파일 검색 DTO</p>
 *
 * @author Hunwoo Park
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class FileSearchDTO extends BaseSearchDTO implements IReqDto{

	private String refCode; // 파일그룹 코드
	private long refSeq; // 대상 글 번호

	private String fileId;
	private String[] fileIds;

	private String fileExt;
	private String[] fileExts;

	private String delYn;

}
