package com.erns.coching.terms.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.terms.domain.TermsSearchDTO;

/**
*
* <p>Terms Mapper</p>
*
* @author Kyunmin Lee
*
*/
@Mapper
public interface TermsDAO {

	public List<Map<String, Object>> getList(TermsSearchDTO param);
	public int getListCount(TermsSearchDTO param);
	
	public Map<String, Object> load(TermsSearchDTO param);
	
	public List<Map<String, Object>> getVersionList(TermsSearchDTO param);
}
