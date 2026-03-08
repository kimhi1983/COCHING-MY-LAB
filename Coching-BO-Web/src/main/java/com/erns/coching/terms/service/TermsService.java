package com.erns.coching.terms.service;

import java.util.List;
import java.util.Map;

import com.erns.coching.terms.domain.TermsSearchDTO;
import com.erns.coching.terms.domain.TermsVO;

/**
 *
 * <p>약관정보 Service</p>
 *
 * @author Hunwoo Park
 *
 */
public interface TermsService {

	public List<Map<String, Object>> getList(TermsSearchDTO param);
	public int getListCount(TermsSearchDTO param);

	public Map<String, Object>  load(TermsSearchDTO param);
	public int insert(TermsVO param);
	public int update(TermsVO param);
	public int updateUseYn(TermsVO param);

	public List<Map<String, Object>> getVersionList(TermsSearchDTO param);

//	public int newVersion(TermsVO newTerms, TermsVO oldTerms);
}
