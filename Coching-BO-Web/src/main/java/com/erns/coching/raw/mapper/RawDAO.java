package com.erns.coching.raw.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.raw.domain.RawDetailSearchDTO;
import com.erns.coching.raw.domain.RawDetailVO;
import com.erns.coching.raw.domain.RawManagerSearchDTO;
import com.erns.coching.raw.domain.RawMasterSearchDTO;
import com.erns.coching.raw.domain.RawMasterVO;


/**
 *
 * <p>원료 마스터 정보 Mapper</p>
 *
 * @author Kyungmin Lee
 *
 */
@Mapper
public interface RawDAO {

	public List<Map<String, Object>> getList(RawMasterSearchDTO param);
	public int getListCount(RawMasterSearchDTO param);

	public Map<String, Object> load(RawMasterSearchDTO param);
	
	public int updateState(RawMasterVO param);

	// 원료 성분 관련
	public List<Map<String, Object>> getElmList(RawMasterSearchDTO param);

	// 원료 서류 관련
	public List<Map<String, Object>> getDocList(RawMasterSearchDTO param);

	// 성분 구분정보 관련
	public List<Map<String, Object>> getTypeList(RawMasterSearchDTO param);

	public List<Map<String, Object>> getManagerList(RawManagerSearchDTO param);
	public Map<String, Object> getManager(RawManagerSearchDTO param);

	public Map<String, Object> getDetail(RawDetailSearchDTO param);
	public RawDetailVO loadDetail(RawDetailSearchDTO param);

	public List<Map<String, Object>> getGarbageList(RawMasterSearchDTO param);
	public int getGarbageListCount(RawMasterSearchDTO param);

	//성분 구분 타입 코드
	public List<Map<String, Object>> getCmTypeList();

}
