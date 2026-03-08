package com.erns.coching.raw.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.erns.coching.raw.domain.RawDetailSearchDTO;
import com.erns.coching.raw.domain.RawDetailVO;
import com.erns.coching.raw.domain.RawManagerSearchDTO;
import com.erns.coching.raw.domain.RawMasterSearchDTO;
import com.erns.coching.raw.domain.RawMasterVO;

/**
 *
 * <p>원료 Service</p>
 *
 * @author Kyungmin Lee
 *
 */
public interface RawService {

	public List<Map<String, Object>> getList(RawMasterSearchDTO param);
	public int getListCount(RawMasterSearchDTO param);

	public Map<String, Object> load(RawMasterSearchDTO param);
	
	public int updateState(Collection<RawMasterVO> list);
	// 원료 성분 관련
	public List<Map<String, Object>> getElmList(RawMasterSearchDTO param);

	// 원료 서류 관련
	public List<Map<String, Object>> getDocList(RawMasterSearchDTO param);

	// 성분 구분정보 관련
	public List<Map<String, Object>> getTypeList(RawMasterSearchDTO param);

	//원료 담당자 관련
	public List<Map<String, Object>> getManagerList(RawManagerSearchDTO param);
	public Map<String, Object> getManager(RawManagerSearchDTO param);

	//원료 자료 관련
	public Map<String, Object> getDetail(RawDetailSearchDTO param);
	public RawDetailVO loadDetail(RawDetailSearchDTO param);

	//휴지통
	public List<Map<String, Object>> getGarbageList(RawMasterSearchDTO param);
	public int getGarbageListCount(RawMasterSearchDTO param);

	// 성분 구분 타입 코드
	public List<Map<String, Object>> getCmTypeList();


}
