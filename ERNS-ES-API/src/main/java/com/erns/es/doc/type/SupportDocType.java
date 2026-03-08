package com.erns.es.doc.type;

import org.springframework.http.MediaType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum SupportDocType {

	DOC(	"doc"	, "docFile"	, "워드97", 
			MediaType.parseMediaType("application/msword")),
    XLS(	"xls"	, "docFile"	, "엑셀97",
    		MediaType.parseMediaType("application/vnd.ms-excel")),
    PPT(	"ppt"	, "docFile"	, "파워포인트97",
    		MediaType.parseMediaType("application/vnd.ms-powerpoint")),
    DOCX(	"docx"	, "docFile"	, "워드",
    		MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document")),
    XLSX(	"xlsx"	, "docFile"	, "엑셀",
    		MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
    PPTX(	"pptx"	, "docFile"	, "파워포인트",
    		MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.presentationml.presentation")),
	
	PDF(	"pdf"	, "docFile"	, "PDF",
			MediaType.parseMediaType("application/pdf")),
	HWP(	"hwp"	, "docFile"	, "한글",
			MediaType.parseMediaType("application/x-hwp")),
	HWPX(	"hwpx"	, "docFile"	,"한글X",
			MediaType.parseMediaType("application/x-hwpml")),
	
	PNG(	"png"	, "image"	, "png 이미지파일",
			MediaType.parseMediaType("image/png")),
	JPG(	"jpg"	, "image"	, "jpg 이미지파일",
			MediaType.parseMediaType("image/jpeg"))
	;

	private String ext;
	private String type;
	private String name;
	private MediaType mediaType;
	
	public static SupportDocType findByFileExtension(String ext) {
		for(SupportDocType e : SupportDocType.values()) {
			if(e.getExt().equalsIgnoreCase(ext)) return e;
		}
		return null;
	}	
}
