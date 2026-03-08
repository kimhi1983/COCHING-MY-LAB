package com.erns.coching.search.service;

import java.util.List;
import java.util.Map;

import com.erns.coching.common.model.ApiResult;
import com.erns.coching.search.domain.CochingEsTvSearchDTO;
import com.erns.coching.search.domain.CochingEsTvVO;
import com.erns.coching.search.domain.EsManageDTO;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface CochingEsTvService {

  public List<Map<String, Object>> getList(CochingEsTvSearchDTO param);
  public List<CochingEsTvVO> getListVo(CochingEsTvSearchDTO param);
  public int getListCount(CochingEsTvSearchDTO param);
  public Map<String, Object> load(long tvSeq);

	public int resetTable();

  public ApiResult indices(EsManageDTO param);

  public boolean resetCochingTvIndex();
  public boolean createEsIndex(String indexName) throws JsonProcessingException;
  public int bulkInsertToEs(String indexName, int chunkSize);
  public int bulkSyncDocToEs(String indexName, long[] tvSeqs);

}
