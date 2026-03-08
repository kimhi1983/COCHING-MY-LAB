package com.erns.coching.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO Abstract
 * @author Hunwoo Park
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class AbstractReqDTO {

  //호출한 라우더 이름
  protected String routerName;  
}
