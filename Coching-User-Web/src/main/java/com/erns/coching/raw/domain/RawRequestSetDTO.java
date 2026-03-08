package com.erns.coching.raw.domain;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.raw.domain.vg.ValidSetRawRequest0001;
import com.erns.coching.raw.domain.vg.ValidSetRawRequest0002;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
*
* <p>원료 요청 설정 DTO</p>
*
* @author Kyungmin Lee
*
*/
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RawRequestSetDTO extends AbstractReqDTO implements IReqDto {


	@NotNull(groups = {ValidSetRawRequest0002.class},
			message = "rawRequestSeq 누락")
	private long rawRequestSeq;			//원료 성분 SEQ
	@NotNull(groups = {ValidSetRawRequest0001.class},
			message = "rawDetailSeq 누락")
	private long rawDetailSeq;			//원료 자료 SEQ
	@NotNull(groups = {ValidSetRawRequest0001.class},
			message = "rawName 누락")
	private String rawName;			//원료 이름
	@NotNull(groups = {ValidSetRawRequest0001.class},
			message = "ptnSeq 누락")
	private long ptnSeq;			//원료사 SEQ
	@NotNull(groups = {ValidSetRawRequest0001.class},
			message = "ptnName 누락")
	private String ptnName;			//원료사 이름
	private String membName;
	@NotNull(groups = {ValidSetRawRequest0001.class},
			message = "phone 누락")
	private String phone;			//전화번호
	@NotNull(groups = {ValidSetRawRequest0001.class},
			message = "email 누락")
	@Email(message = "이메일 형식 오류")
	private String email;			//이메일
	private String address;			//주소
	private String reqDetail;		//요청사항
	private String messageType;		//전송 타입
	@NotNull(groups = {ValidSetRawRequest0002.class},
			message = "status 누락")
	private String status;			//상태

	@NotNull(groups = {ValidSetRawRequest0001.class},
			message = "rawList 누락")
	private List<RawDetailSetDTO> rawList; //요청 원료 리스트

	@NotNull(groups = {ValidSetRawRequest0001.class},
			message = "typeList 누락")
	private List<RawRequestTypeSetDTO> typeList; //요청 자료 리스트

	private long rgtr;
	private long chnr;

}
