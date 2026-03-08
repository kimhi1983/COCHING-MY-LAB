package com.erns.es.search.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
public class EsHitDTO {
  private Double score;
  private Object source;
  private Object highlight;
}
