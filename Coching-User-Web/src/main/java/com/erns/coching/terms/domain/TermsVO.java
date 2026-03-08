package com.erns.coching.terms.domain;

import java.sql.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <p>약관 VO </p>
 * @author heejinyu@erns.co.kr
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TermsVO {

	protected long membSeq;
	protected String termsCd;
	protected String agreeYn;

	protected long rgtr;
	protected Date rgtDttm;
}
