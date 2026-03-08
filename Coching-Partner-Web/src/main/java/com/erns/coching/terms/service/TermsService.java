package com.erns.coching.terms.service;

import java.util.List;
import java.util.Map;

import com.erns.coching.terms.domain.TermsSearchDTO;

/**
*
* <p>Terms Service</p>
*
* @author Kyunmin Lee
*
*/
public interface TermsService {

	public List<Map<String, Object>> getList(TermsSearchDTO param);
	public int getListCount(TermsSearchDTO param);	
	public Map<String, Object>  load(TermsSearchDTO param);
	
	public List<Map<String, Object>> getVersionList(TermsSearchDTO param);
}
