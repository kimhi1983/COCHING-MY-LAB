package com.erns.coching.partner.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.partner.domain.PartnerSearchDTO;
import com.erns.coching.partner.domain.PartnerVO;


/**
 *
 * <p>파트너사 정보 Mapper</p>
 *
 * @author Kyungmin Lee
 *
 */
@Mapper
public interface PartnerDAO {

	public List<Map<String, Object>> getList(PartnerSearchDTO param);
	public int getListCount(PartnerSearchDTO param);

	public Map<String, Object> load(PartnerSearchDTO param);
	public long loadNextSeq();

	public int insert(PartnerVO param);
	public int update(PartnerVO param);
	public int updateDelYn(PartnerVO param);
	public int delete(long ptnSeq);

}
