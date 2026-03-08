package com.erns.coching.search.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.search.domain.CochingEsTvSearchDTO;
import com.erns.coching.search.domain.CochingEsTvVO;

/**
 * 
 * <p>코칭 TV Mapper</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Mapper
public interface CochingEsTvDAO {

  public List<Map<String, Object>> getList(CochingEsTvSearchDTO param);
  public List<CochingEsTvVO> getListVo(CochingEsTvSearchDTO param);  
  public int getListCount(CochingEsTvSearchDTO param);
  public Map<String, Object> load(long rawSeq);

  public int dropTable();
  public int createTable();

  //ES document 개별 관리용
  public List<CochingEsTvVO> getListVoForEsRenewIndexItems(CochingEsTvSearchDTO param);  
  public List<CochingEsTvVO> getListVoForEsRenewDeleteItems(CochingEsTvSearchDTO param);  

}
