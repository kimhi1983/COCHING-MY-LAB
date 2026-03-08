package com.erns.coching.raw.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.erns.coching.common.model.ApiResult;
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
import org.springframework.web.multipart.MultipartFile;

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

	public int insert(RawMasterVO param);
	public int update(RawMasterVO param);
	public int updateUseYn(RawMasterVO param);
	public int updateDelYn(RawMasterVO param);
	public int updateRequestCnt(RawDetailVO param);

	public int delete(long rawSeq);
	
	//원료 자료 관련
	public RawDetailVO loadDetail(RawDetailSearchDTO param);
	
	// 성분 구분 타입 코드
	public List<Map<String, Object>> getCmTypeList();

	// 원료 요청내역 관련
	public List<Map<String, Object>> getRequestList(RawRequestSearchDTO param);
	public int getRequestListCount(RawRequestSearchDTO param);
	public Map<String, Object> loadRequest(RawRequestSearchDTO param);
	public List<Map<String, Object>> getRequestTypeList(RawRequestSearchDTO param);
	public List<Map<String, Object>> getRequestReplyList(RawRequestSearchDTO param);
	public int insertRequest(RawRequestVO param);
	public int insertRequestType(RawRequestTypeVO param);
	public ApiResult addRequest(RawRequestSetDTO setDTO);
	public int updateStatus(RawRequestSetDTO setDTO);
	public int insertRequestReply(RawRequestReplyVO param);
	public int addRequestReply(RawRequestReplySetDTO setDTO, List<MultipartFile> uploadFiles) throws IOException;
	public int updateReplyDelYn(RawRequestReplySetDTO setDTO);
}
