package com.erns.coching.search.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.search.domain.CochingEsRawSearchDTO;
import com.erns.coching.search.domain.CochingEsRawVO;

/**
 * 
 * <p>코칭 원료 Mapper</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Mapper
public interface CochingEsRawDAO {

  public List<Map<String, Object>> getList(CochingEsRawSearchDTO param);
  public List<CochingEsRawVO> getListVo(CochingEsRawSearchDTO param);  
  public int getListCount(CochingEsRawSearchDTO param);
  public Map<String, Object> load(long rawSeq);

  //ES document 개별 관리용
  public List<CochingEsRawVO> getListVoForEsRenewIndexItems(CochingEsRawSearchDTO param);  
  public List<CochingEsRawVO> getListVoForEsRenewDeleteItems(CochingEsRawSearchDTO param);  

}
