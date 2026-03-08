package com.erns.coching.raw.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.erns.coching.common.model.ApiResult;
import com.erns.coching.raw.domain.RawDetailSearchDTO;
import com.erns.coching.raw.domain.RawDetailSetDTO;
import com.erns.coching.raw.domain.RawDetailVO;
import com.erns.coching.raw.domain.RawDocVO;
import com.erns.coching.raw.domain.RawElmVO;
import com.erns.coching.raw.domain.RawManagerSearchDTO;
import com.erns.coching.raw.domain.RawManagerSetDTO;
import com.erns.coching.raw.domain.RawManagerVO;
import com.erns.coching.raw.domain.RawMasterSearchDTO;
import com.erns.coching.raw.domain.RawMasterSetDTO;
import com.erns.coching.raw.domain.RawMasterVO;
import com.erns.coching.raw.domain.RawTypeVO;
import com.erns.coching.raw.domain.IngdDicSearchDTO;
import com.erns.coching.raw.domain.IngdDicVO;

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
	public ApiResult addRaw(RawMasterSetDTO setDTO);
	public ApiResult uploadRawExcel(RawMasterSetDTO setDTO, MultipartFile uploadFiles);
	public ApiResult updateRaw(RawMasterSetDTO setDTO);
	public int addRawDetail(RawDetailSetDTO paramDto, List<MultipartFile> uploadFiles) throws IOException;
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

	//원료 담당자 관련
	public List<Map<String, Object>> getManagerList(RawManagerSearchDTO param);
	public Map<String, Object> getManager(RawManagerSearchDTO param);
	public int managerInsert(RawManagerVO param);
	public int managerDelete(RawManagerSetDTO setDTO);

	//원료 자료 관련
	public Map<String, Object> getDetail(RawDetailSearchDTO param);
	public RawDetailVO loadDetail(RawDetailSearchDTO param);
	public int detailInsert(RawDetailVO param);
	public int detailDelete(RawDetailVO param);
	public int updateDetailUseYn(RawDetailVO param);
	public int updateDetailDelYn(RawDetailVO param);

	//휴지통
	public List<Map<String, Object>> getGarbageList(RawMasterSearchDTO param);
	public int getGarbageListCount(RawMasterSearchDTO param);

	//담당자 지정 관련
	public int updateRawManager(RawManagerSetDTO param);

	// 성분 구분 타입 코드
	public List<Map<String, Object>> getCmTypeList();

	//성분 사전 관련
	public List<IngdDicVO> getIngdDic(IngdDicSearchDTO param);

	public List<Map<String, Object>> procAttachThumbFiles(RawMasterSetDTO paramDto, List<MultipartFile> uploadFiles, String strDelFileIds, boolean isPreview) throws IllegalStateException, IOException;
	//첨부파일 업로드
	public List<Map<String, Object>> procAttachFiles(RawDetailSetDTO paramDto, List<MultipartFile> uploadFiles, String strDelFileIds, boolean isPreview) throws IllegalStateException, IOException;

	//첨부파일 PDF 처리
	public void addPdfProcess(List<String> fileIdList);

	//검색엔진에 원료 변경사항 반영
	public int syncEs(long[] rawSeqs);
}
