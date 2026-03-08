package com.erns.coching.raw.domain;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;

import com.erns.coching.raw.domain.vg.ValidSetRaw0001;
import com.erns.coching.raw.domain.vg.ValidSetRaw0002;
import com.erns.coching.raw.domain.vg.ValidSetRaw0003;
import com.erns.coching.raw.domain.vg.ValidSetRaw0004;
import com.erns.coching.raw.domain.vg.ValidSetRaw0006;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
*
* <p>원료 마스터 설정 DTO</p>
*
* @author Kyungmin Lee
*
*/
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RawMasterSetDTO extends AbstractReqDTO implements IReqDto {

	@NotNull(groups = {ValidSetRaw0002.class,
			ValidSetRaw0003.class,
			ValidSetRaw0004.class},
			message = "rawSeq 누락")
	private long rawSeq;			//원료 SEQ
	@NotNull(groups = {ValidSetRaw0001.class,
			ValidSetRaw0002.class,
			ValidSetRaw0006.class},
			message = "ptnSeq 누락")
	private long ptnSeq;			//원료사 SEQ
	@NotNull(groups = {ValidSetRaw0001.class,
			ValidSetRaw0002.class},
			message = "rawName 누락")
	private String rawName;			//원료명
	private String prodCompany;		//제조사
	private String prodCountry;		//제조국
	private String supplier;		//공급사
	private String weight;			//무게
	private String thumbnailId;
	@NotNull(groups = {ValidSetRaw0003.class},
			message = "useYn 누락")
	private String useYn;			//사용여부
	@NotNull(groups = {ValidSetRaw0004.class},
			message = "delYn 누락")
	private String delYn;			//삭제여부
	private String permitYn; 		//관리자 허용여부
	private long rgtr;

	private List<RawManagerSetDTO> managerList;
	private List<RawElmSetDTO> elmList;
	private List<RawTypeSetDTO> typeList;
	private List<RawDocSetDTO> docList;

	private String fileType;

}
