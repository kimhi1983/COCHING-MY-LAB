package com.erns.coching.raw.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.raw.domain.RawDetailSearchDTO;
import com.erns.coching.raw.domain.RawDetailVO;
import com.erns.coching.raw.domain.RawMasterSearchDTO;
import com.erns.coching.raw.domain.RawMasterVO;
import com.erns.coching.raw.domain.RawRequestReplySetDTO;
import com.erns.coching.raw.domain.RawRequestReplyVO;
import com.erns.coching.raw.domain.RawRequestSearchDTO;
import com.erns.coching.raw.domain.RawRequestSetDTO;
import com.erns.coching.raw.domain.RawRequestTypeVO;
import com.erns.coching.raw.domain.RawRequestVO;



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
	public int updateRequestCnt(RawDetailVO param);

	public int delete(long rawSeq);
	
	public Map<String, Object> getDetail(RawDetailSearchDTO param);
	public RawDetailVO loadDetail(RawDetailSearchDTO param);
	public int updateDetailViewCnt(long rawDetailSeq);

	//성분 구분 타입 코드
	public List<Map<String, Object>> getCmTypeList();

	// 원료 요청내역 관련
	public List<Map<String, Object>> getRequestList(RawRequestSearchDTO param);
	public int getRequestListCount(RawRequestSearchDTO param);
	public Map<String, Object> loadRequest(RawRequestSearchDTO param);
	public List<Map<String, Object>> getRequestTypeList(RawRequestSearchDTO param);
	public List<Map<String, Object>> getRequestReplyList(RawRequestSearchDTO param);
	public int insertRequest(RawRequestVO param);
	public int insertRequestType(RawRequestTypeVO param);
	public int updateStatus(RawRequestSetDTO param);
	public int insertRequestReply(RawRequestReplyVO param);
	public int updateReplyDelYn(RawRequestReplySetDTO param);
}
