package com.erns.coching.rnd.domain;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class IngredientDTO extends BaseSearchDTO implements IReqDto{

  private int id = 0;

  private String repName = "";

  private String repNameEn = "";  

}
