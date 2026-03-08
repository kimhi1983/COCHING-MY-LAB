package com.erns.coching.search.domain;

import javax.validation.constraints.NotBlank;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.search.domain.vg.ValidSuggetion0011;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SuggestionSearchDTO extends BaseSearchDTO implements IReqDto {

  //T_INGD_DIC
  private boolean searchIngd = true;

  private int limitIngd = 10;

  private boolean searchIngdNamesKo = false;

  private boolean searchIngdNamesEn = false;

  //T_ES_RAW
  private boolean searchRaw = true;

  private int limitRaw = 10;

  private boolean searchRawProdCompany = false;

  private boolean searchRawProdCountryName = false;

  //PROD_BRAND
  private boolean searchProdBrand = true;

  private int limitProdBrand = 10;

  //T_PT_NAME
  private boolean searchPtName = true;

  private int limitPtName = 10;

  @NotBlank(groups = {
			ValidSuggetion0011.class
		}, message = "keyword 누락")
  private String keyword;

}
