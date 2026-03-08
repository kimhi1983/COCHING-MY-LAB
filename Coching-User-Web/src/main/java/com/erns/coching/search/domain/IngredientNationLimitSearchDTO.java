package com.erns.coching.search.domain;

import javax.validation.constraints.NotEmpty;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.search.domain.vg.ValidIngredientNationLimit0011;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class IngredientNationLimitSearchDTO extends BaseSearchDTO implements IReqDto{

  @NotEmpty(groups = {
    ValidIngredientNationLimit0011.class
    }, message = "ingdId 누락")
  private String ingdId; // 성분 아이디 (t_ingd_dic 기준)
}
