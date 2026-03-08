package com.erns.coching.rnd.domain;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import com.erns.coching.common.model.AbstractReqDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.rnd.domain.vg.ValidLabMaster0020;
import com.erns.coching.rnd.domain.vg.ValidLabMaster0030;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class LabMasterSetDTO extends AbstractReqDTO implements IReqDto {
  
  @Min(groups = {
    ValidLabMaster0030.class,
  }, value = 1,message = "labMstSeq 누락")
  private long labMstSeq;

  @NotEmpty(groups = {
      ValidLabMaster0020.class, 
      ValidLabMaster0030.class,
  }, message = "title 누락")
  private String title;

  @NotEmpty(groups = {
      ValidLabMaster0020.class, 
      ValidLabMaster0030.class,
  }, message = "prodCateGroup 누락")
  private String prodCateGroup;

  @NotEmpty(groups = {
      ValidLabMaster0020.class, 
      ValidLabMaster0030.class,
  }, message = "prodCateCode 누락")
  private String prodCateCode;

  @NotEmpty(groups = {
      ValidLabMaster0020.class, 
      ValidLabMaster0030.class,
  }, message = "content 누락")
  private String content;
  
  private long rgtr;
  private long chnr;
  
  private List<IngredientDTO> ingredientList;
}
