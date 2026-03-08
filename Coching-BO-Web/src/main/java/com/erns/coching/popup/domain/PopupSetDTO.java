package com.erns.coching.popup.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.popup.domain.vg.ValidPopup0020;
import com.erns.coching.popup.domain.vg.ValidPopup0030;
import com.erns.coching.popup.domain.vg.ValidPopup0031;
import com.erns.coching.popup.domain.vg.ValidPopup0032;
import com.erns.coching.popup.domain.vg.ValidPopup0040;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * <p>팝업 설정 DTO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class PopupSetDTO extends AbstractReqDTO implements IReqDto{
	
	@NotEmpty(groups = {
	        ValidPopup0020.class, 
	        ValidPopup0030.class, 
					ValidPopup0031.class,
					ValidPopup0032.class,
	        ValidPopup0040.class,
	    }, message = "popupMstCd 누락")
	private String popupMstCd;
	
	@NotNull(groups = {
			ValidPopup0030.class,
			ValidPopup0031.class,
			ValidPopup0032.class,
			ValidPopup0040.class,
        }, message = "popupSeq 누락")
	@Min(groups = {
			ValidPopup0030.class,
			ValidPopup0031.class,
			ValidPopup0032.class,
			ValidPopup0040.class,
		}, value = 1,message = "popupSeq 누락")
	private Long popupSeq;
	
	@NotEmpty(groups = {
			ValidPopup0020.class, 
			ValidPopup0030.class
	    }, message = "popupName 누락")
	private String popupName;
	
	@Pattern(groups = {
			ValidPopup0020.class,
			ValidPopup0030.class,
		},regexp = "^(19|20)\\d{2}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[0-1])$"
		, message = "fromDate 형식오류.")	
	private String fromDate;
	
	@Pattern(groups = {
			ValidPopup0020.class,
			ValidPopup0030.class,
		},regexp = "^(19|20)\\d{2}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[0-1])$"
		, message = "toDate 형식오류.")	
	private String toDate;
	
	private String popupDesc;
	
	@NotEmpty(groups = {
			ValidPopup0020.class,
			ValidPopup0030.class,
	   	}, message = "closeOpt 누락")
	@Pattern(groups = {
		   ValidPopup0020.class, 
		   ValidPopup0030.class
   		}, message = "closeOpt 형식오류(1|9)", regexp = "1|9")	
	private String closeOpt;
	
	private String popupUrl;
	
	private int popupWidth;
	private int popupHeight;
	private int clicks;
	
	@Min(groups = {
			ValidPopup0031.class
		}, value = 1, message = "sortOrd 누락")
	private int sortOrd;
	
	@NotEmpty(groups = {
			ValidPopup0020.class,
			ValidPopup0030.class,
			ValidPopup0032.class,
		}, message = "useYn 누락")
	@Pattern(groups = {
			ValidPopup0020.class, 
			ValidPopup0030.class,
			ValidPopup0032.class,
		}, message = "useYn 형식오류(Y|N)", regexp = "Y|N")		
	private String useYn;
   
	@NotEmpty(groups = {
			ValidPopup0032.class,
			ValidPopup0040.class,
		}, message = "delYn 누락")	
	@Pattern(groups = {
			ValidPopup0032.class,
			ValidPopup0040.class,
		}, message = "delYn 형식오류(Y|N)", regexp = "Y|N")
	private String delYn;
}
