package com.erns.coching.rnd.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.rnd.domain.AiLabResSearchDTO;
import com.erns.coching.rnd.domain.AiLabResVO;

/**
 *
 * <p>AI 연구실 결과 Mapper</p>
 *
 * @author Hunwoo Park
 *
 */
@Mapper
public interface AiLabResDAO {

	public List<Map<String, Object>> getList(AiLabResSearchDTO param);
	public int getListCount(AiLabResSearchDTO param);

	public Map<String, Object> load(AiLabResSearchDTO param);

	public int insert(AiLabResVO param);

	public int update(AiLabResVO param);
	
	public int delete(long resSeq);
  public int deleteByLabMstSeq(long labMstSeq);
}

