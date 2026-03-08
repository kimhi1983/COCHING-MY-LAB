package com.erns.coching.mltln.domain;

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <p>다국어 마스터 VO</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class MltlnVO {

	protected String code;
	protected String codeNmKo;
	protected String codeNmEn;
	protected long rgtr;
	protected Date rgtDttm;

}
