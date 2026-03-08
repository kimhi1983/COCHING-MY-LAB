package com.erns.coching.search.domain;

import com.erns.coching.common.model.BaseSearchDTO;
import com.erns.coching.common.model.IReqDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CochingEsRawSearchDTO extends BaseSearchDTO implements IReqDto {

  private long rawSeq;

  private long[] rawSeqs;

  private String rawNameL;
}
