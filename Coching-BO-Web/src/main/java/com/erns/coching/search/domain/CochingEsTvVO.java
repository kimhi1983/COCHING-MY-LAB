package com.erns.coching.search.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class CochingEsTvVO {

	@JsonIgnore
	private String job;	//index, delete, update

	private long tvSeq;
	private String ytUrl;
	private long sortOrd;
	private String title;
	private String hashtag;
	private String views;
	private String ytDttm;
	private String useYn;
	private String delYn;
	private long rgtr;
	private String rgtDttm;
	private long chnr;
	private String chngDttm;
	private String content;
}
