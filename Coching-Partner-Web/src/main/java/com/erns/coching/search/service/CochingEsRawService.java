package com.erns.coching.search.service;

import java.util.List;
import java.util.Map;

import com.erns.coching.common.model.ApiResult;
import com.erns.coching.search.domain.CochingEsRawSearchDTO;
import com.erns.coching.search.domain.CochingEsRawVO;
import com.erns.coching.search.domain.EsManageDTO;

public interface CochingEsRawService {

  public List<Map<String, Object>> getList(CochingEsRawSearchDTO param);
  public List<CochingEsRawVO> getListVo(CochingEsRawSearchDTO param);
  public int getListCount(CochingEsRawSearchDTO param);
  public Map<String, Object> load(long rawSeq);

  public ApiResult indices(EsManageDTO param);
  public int bulkSyncDocToEs(String indexName, long[] rawSeqs);
}
