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
* <p>프록시파일 VO
* t_proxy_file</p>
*
* @author Hunwoo Park
*
*/
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class ProxyFileVO {

	protected String fileId;

	protected String refCode;
	protected String orgUrl;
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
