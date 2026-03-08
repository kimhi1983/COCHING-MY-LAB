package com.erns.coching.partner.service;

import java.util.List;
import java.util.Map;

import com.erns.coching.partner.domain.PartnerSearchDTO;
import com.erns.coching.partner.domain.PartnerVO;

/**
 *
 * <p>파트너사 기본정보 Service</p>
 *
 * @author Kyungmin Lee
 *
 */
public interface PartnerService {

	public List<Map<String, Object>> getList(PartnerSearchDTO param);
	public int getListCount(PartnerSearchDTO param);

	public List<Map<String, Object>> getMembList(PartnerSearchDTO param);
	public int getMembListCount(PartnerSearchDTO param);

	public List<Map<String, Object>> getPtnNmList(PartnerSearchDTO param);
	public int getPtnNmListCount(PartnerSearchDTO param);

	public Map<String, Object> load(PartnerSearchDTO param);

	public int insert(PartnerVO param);
	public int update(PartnerVO param);
	public int updateDelYn(PartnerVO param);
	public int delete(long ptnSeq);

}
