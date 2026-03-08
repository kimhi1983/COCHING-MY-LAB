package com.erns.coching.popup.domain;

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
 * <p>팝업관리 VO
 * T_POPUP_INF</p>
 *
 * @author Hunwoo Park
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class PopupVO {

	private long popupSeq;
	private String popupMstCd;

	private String popupName;
	private String fromDate;
	private String toDate;
	private String popupDesc;
	private String closeOpt;
	private String popupUrl;
	private int popupWidth;
	private int popupHeight;
	private int clicks;
	private int sortOrd;
	private String useYn;
	private String delYn;
	private long rgtr;
	private Date rgtDttm;
	private long chnr;
	private Date chngDttm;

	protected void setFromPopupSetDTO(PopupSetDTO fromDto) {
		this.popupSeq = fromDto.getPopupSeq();
	    this.popupMstCd = fromDto.getPopupMstCd();

	    this.popupName = fromDto.getPopupName();
	    this.fromDate = fromDto.getFromDate();
	    this.toDate = fromDto.getToDate();
	    this.popupDesc = fromDto.getPopupDesc();
	    this.closeOpt = fromDto.getCloseOpt();
	    this.popupUrl = fromDto.getPopupUrl();
	    this.popupWidth = fromDto.getPopupWidth();
	    this.popupHeight = fromDto.getPopupHeight();
	    this.useYn = fromDto.getUseYn();
	}

	/**
	 * 팝업 등록
	 * @param fromDto
	 * @param addUser
	 */
	@Builder(builderClassName = "AddPopupBuilder", builderMethodName = "AddPopupBuilder")
	public PopupVO(PopupSetDTO fromDto, LoginUserDTO addUser) {
	    Assert.notNull(fromDto, "fromDto must not be null");
	    Assert.notNull(addUser, "addUser must not be null");

	    this.setFromPopupSetDTO(fromDto);
	    this.popupSeq = 0L;
	    this.delYn = "N";

	    this.rgtr = addUser.getSeq();
	    this.chnr = addUser.getSeq();
	}

	/**
	 * 팝업 설정
	 * @param popupSeq
	 * @param fromDto
	 * @param updateUser
	 */
	@Builder(builderClassName = "SetPopupBuilder", builderMethodName = "SetPopupBuilder")
	public PopupVO(long popupSeq, PopupSetDTO fromDto, LoginUserDTO updateUser) {
	    Assert.notNull(fromDto, "fromDto must not be null");
	    Assert.notNull(updateUser, "updateUser must not be null");

	    this.setFromPopupSetDTO(fromDto);
	    this.popupSeq = popupSeq;

	    this.chnr = updateUser.getSeq();
	}

	/**
	 * 팝업 삭제
	 * @param popupMstCd
	 * @param popupSeq
	 * @param delYn
	 * @param updateUser
	 */
	@Builder(builderClassName = "UpdateDelYnBuilder", builderMethodName = "UpdateDelYnBuilder")
	public PopupVO(String popupMstCd, long popupSeq, String delYn, LoginUserDTO updateUser) {
		Assert.notNull(popupMstCd, "popupMstCd must not be null");
	    Assert.notNull(delYn, "delYn must not be null");
	    Assert.notNull(updateUser, "updateUser must not be null");

	    this.popupMstCd = popupMstCd;
	    this.popupSeq = popupSeq;
	    this.delYn = delYn;

	    this.chnr = updateUser.getSeq();
	}
}
