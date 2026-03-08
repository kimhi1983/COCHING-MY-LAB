package com.erns.coching.raw.domain;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * <p>원료 담당자 설정 DTO</p>
 *
 * @author Kyungmin Lee
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RawManagerSetDTO {

	protected long rawSeq;			//원료 SEQ
	protected long membSeq;			//멤버 SEQ

	protected long ptnSeq;
	protected long newManagerSeq;
	@NotEmpty
	protected List<RawDetailSetDTO> checkedlist;
	private long rgtr;
}
