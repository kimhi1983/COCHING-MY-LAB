package com.erns.coching.search.domain;

import javax.validation.constraints.Min;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.search.domain.vg.ValidHwBrand0011;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class HwBrandSearchDTO extends BaseSearchDTO implements IReqDto {

  @Min(groups = {
    ValidHwBrand0011.class,
  }, 
    value = 1,
    message = "seq 누락")
  private long seq;			//SEQ

  private String nameL;		//검색어

}
