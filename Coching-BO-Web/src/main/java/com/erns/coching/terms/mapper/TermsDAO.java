package com.erns.coching.terms.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.terms.domain.TermsSearchDTO;
import com.erns.coching.terms.domain.TermsVO;

/**
 *
 * <p>약관정보 Mapper</p>
 *
 * @author Hunwoo Park
 *
 */
@Mapper
public interface TermsDAO {

	public List<Map<String, Object>> getList(TermsSearchDTO param);
	public int getListCount(TermsSearchDTO param);

	public Map<String, Object> load(TermsSearchDTO param);
	public int insert(TermsVO param);
	public int update(TermsVO param);
	public int updateUseYn(TermsVO param);

	public List<Map<String, Object>> getVersionList(TermsSearchDTO param);
}
