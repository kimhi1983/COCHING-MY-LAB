package com.erns.coching.member.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.member.domain.vg.ValidAdminMember0001;
import com.erns.coching.member.domain.vg.ValidAdminMember0002;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * <p>관리자 정보 검색 DTO</p>
 *
 * @author Hunwoo Park
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminMemberSearchDTO extends BaseSearchDTO implements IReqDto {

	@NotNull(groups = {ValidAdminMember0002.class},
			message = "사용자seq 누락")
	@Min(value = 1,
			groups = {ValidAdminMember0002.class},
			message = "사용자seq 누락")
	private Long userSeq;

	private long[] userSeqs;

	@NotNull(groups = {ValidAdminMember0001.class},
			message = "사용자ID 누락")
	private String userId;

	private String userIdL;

	private String userNameL;

	private String phoneL;

	private String emailL;

	private String useYn;
}
