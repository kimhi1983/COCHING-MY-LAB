package com.erns.coching.rnd.domain;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.rnd.domain.vg.ValidAiPrescription0011;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AiPrescriptionDTO extends BaseSearchDTO implements IReqDto {

  private long labMstSeq = 0L;

  @NotEmpty(groups = {
    ValidAiPrescription0011.class,	//AI 처방
  }, message = "title 누락")  
  private String title;

  @NotEmpty(groups = {
    ValidAiPrescription0011.class,	//AI 처방
  }, message = "formulation 누락")  
  private String formulation;

  @NotEmpty(groups = {
    ValidAiPrescription0011.class,	//AI 처방
  }, message = "prodCateGroup 누락")  
  private String prodCateGroup;

  @NotEmpty(groups = {
    ValidAiPrescription0011.class,	//AI 처방
  }, message = "prodCateCode 누락")  
  private String prodCateCode;

  private String direction; // 방향성

  @NotNull(groups = {
    ValidAiPrescription0011.class,	//AI 처방
  }, message = "ingredients 누락") 
  private List<IngredientDTO> ingredients; // AI 처방 옵션 - 모델

  private String aiOptModel = "gpt-5-mini";

  private double aiOptTemperature = 1;  // AI 처방 옵션 - 온도

  private String model_name = "gpt-5-mini";

  private double temperature = 1;  // AI 처방 옵션 - 온도

  private long membSeq;
}
