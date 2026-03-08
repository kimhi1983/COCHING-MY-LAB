package com.erns.coching.raw.mapper;

import java.util.List;
import java.util.Map;

import com.erns.coching.raw.domain.RawDetailSearchDTO;
import com.erns.coching.raw.domain.RawDetailVO;
import com.erns.coching.raw.domain.RawElmVO;
import com.erns.coching.raw.domain.RawManagerSearchDTO;
import com.erns.coching.raw.domain.RawManagerSetDTO;
import com.erns.coching.raw.domain.RawManagerVO;
import com.erns.coching.raw.domain.RawMasterSetDTO;
import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.raw.domain.RawDocVO;
import com.erns.coching.raw.domain.RawMasterSearchDTO;
import com.erns.coching.raw.domain.RawMasterVO;
import com.erns.coching.raw.domain.RawTypeVO;
import com.erns.coching.raw.domain.IngdDicSearchDTO;
import com.erns.coching.raw.domain.IngdDicVO;


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

	public int insert(RawMasterVO param);
	public int update(RawMasterVO param);
	public int updateUseYn(RawMasterVO param);
	public int updateDelYn(RawMasterVO param);
	public int delete(RawMasterSetDTO setDTO);

	// 원료 성분 관련
	public List<Map<String, Object>> getElmList(RawMasterSearchDTO param);
	public int elmInsert(RawElmVO param);
	public int elmDelete(RawMasterSetDTO setDTO);

	// 원료 서류 관련
	public List<Map<String, Object>> getDocList(RawMasterSearchDTO param);
	public int docInsert(RawDocVO param);
	public int docDelete(RawMasterSetDTO setDTO);

	// 성분 구분정보 관련
	public List<Map<String, Object>> getTypeList(RawMasterSearchDTO param);
	public int typeInsert(RawTypeVO param);
	public int typeDelete(RawMasterSetDTO setDTO);

	public List<Map<String, Object>> getManagerList(RawManagerSearchDTO param);
	public Map<String, Object> getManager(RawManagerSearchDTO param);
	public int managerInsert(RawManagerVO param);
	public int managerDelete(RawManagerSetDTO setDTO);

	public Map<String, Object> getDetail(RawDetailSearchDTO param);
	public RawDetailVO loadDetail(RawDetailSearchDTO param);
	public int detailInsert(RawDetailVO param);
	public int detailDelete(RawDetailVO param);
	public int updateDetailPermitYn(RawDetailVO param);
	public int updateDetailUseYn(RawDetailVO param);
	public int updateDetailDelYn(RawDetailVO param);

	public List<Map<String, Object>> getGarbageList(RawMasterSearchDTO param);
	public int getGarbageListCount(RawMasterSearchDTO param);

	//성분 구분 타입 코드
	public List<Map<String, Object>> getCmTypeList();

	//성분 사전 관련
	public List<IngdDicVO> getIngdDic(IngdDicSearchDTO param);

}
