package com.erns.coching.rnd.domain;

import java.util.Date;

import org.springframework.util.Assert;

import com.erns.coching.login.domain.LoginUserDTO;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * <p>연구실 마스터 VO
 * T_LAB_MST</p>
 *
 * @author Hunwoo Park
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class LabMasterVO {

	private long labMstSeq;
	private String title;
	private String prodCateGroup;
	private String prodCateCode;
	private String content;
	private long rgtr;
	private Date rgtDttm;
	private long chnr;
	private Date chngDttm;

	protected void setFromLabMasterSetDTO(LabMasterSetDTO fromDto) {
		this.labMstSeq = fromDto.getLabMstSeq();
		this.title = fromDto.getTitle();
		this.prodCateGroup = fromDto.getProdCateGroup();
		this.prodCateCode = fromDto.getProdCateCode();
		this.content = fromDto.getContent();
	}

	/**
	 * 연구실 마스터 등록
	 * @param title
	 * @param prodCateGroup
	 * @param prodCateCode
	 * @param content
	 * @param addUser
	 */
	@Builder(builderClassName = "AddLabMasterBuilder", builderMethodName = "AddLabMasterBuilder")
	public LabMasterVO(LabMasterSetDTO fromDto, LoginUserDTO addUser) {
	    Assert.notNull(fromDto, "fromDto must not be null");
	    Assert.notNull(addUser, "addUser must not be null");

	    this.setFromLabMasterSetDTO(fromDto);
	    this.labMstSeq = 0L;

	    this.rgtr = addUser.getSeq();
	    this.chnr = addUser.getSeq();
	}

	/**
	 * 연구실 마스터 설정
	 * @param labMstSeq
	 * @param title
	 * @param prodCateGroup
	 * @param prodCateCode
	 * @param content
	 * @param updateUser
	 */
	@Builder(builderClassName = "SetLabMasterBuilder", builderMethodName = "SetLabMasterBuilder")
	public LabMasterVO(long labMstSeq, LabMasterSetDTO fromDto, LoginUserDTO updateUser) {
	    Assert.notNull(fromDto, "fromDto must not be null");
	    Assert.notNull(updateUser, "updateUser must not be null");

	    this.labMstSeq = labMstSeq;
	    this.setFromLabMasterSetDTO(fromDto);

	    this.chnr = updateUser.getSeq();
	}
}
