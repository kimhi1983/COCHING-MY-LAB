package com.erns.coching.popup.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.erns.coching.common.Const;
import com.erns.coching.common.file.domain.FileSearchDTO;
import com.erns.coching.common.file.domain.FileSetDTO;
import com.erns.coching.common.file.repository.FileRepository;
import com.erns.coching.common.file.service.FileService;
import com.erns.coching.popup.domain.PopupSearchDTO;
import com.erns.coching.popup.domain.PopupVO;
import com.erns.coching.popup.mapper.PopupDAO;
import com.erns.coching.popup.service.PopupService;

/**
 *
 * <p>팝업 Service</p>
 *
 * @author Hunwoo Park
 *
 */
@Service
@Transactional
public class PopupServiceImpl implements PopupService{

	@Autowired
	private PopupDAO dao;

	@Autowired
	private FileRepository fileRepo;

	@Autowired
	private FileService fileService;

	@Override
	public List<Map<String, Object>> getList(PopupSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(PopupSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public Map<String, Object> load(PopupSearchDTO param) {
		return dao.load(param);
	}

	@Override
	public int insert(PopupVO param) {
		return dao.insert(param);
	}

	@Override
	public int update(PopupVO param) {
		return dao.update(param);
	}

	@Override
	public int updateUseYn(PopupVO param) {
		return dao.updateUseYn(param);
	}

	@Override
	public int updateDelYn(PopupVO param) {
		return dao.updateDelYn(param);
	}

	@Override
	public int updateState(PopupVO param) {
		return dao.updateState(param);
	}

	@Override
	public Map<String, Object> insert(PopupVO popupVO, List<MultipartFile> newFiles)
			throws IllegalStateException, IOException {
		if(dao.insert(popupVO) <= 0) {
			return null;
		}

		long popupSeq = popupVO.getPopupSeq();
		procAttachFiles(popupSeq, popupVO.getChnr(), newFiles, null);

		PopupSearchDTO popupScDTO = new PopupSearchDTO();
		popupScDTO.setPopupSeq(popupSeq);

		Map<String, Object> retMap = dao.load(popupScDTO);
		return retMap;
	}

	@Override
	public Map<String, Object> update(PopupVO popupVO, List<MultipartFile> addFiles,
			String strDelFileIds) throws IllegalStateException, IOException {
		if(dao.update(popupVO) <= 0) {
			return null;
		}

		long popupSeq = popupVO.getPopupSeq();
		procAttachFiles(popupSeq, popupVO.getChnr(), addFiles, strDelFileIds);

		PopupSearchDTO popupScDTO = new PopupSearchDTO();
		popupScDTO.setPopupSeq(popupSeq);

		Map<String, Object> retMap = dao.load(popupScDTO);
		return retMap;
	}

	@Override
	public List<Map<String, Object>> getFiles(PopupSearchDTO param) {
		String fileTypeCd = getFileTypeCode();
		return getFiles(fileTypeCd, param.getPopupSeq());
	}

	@Override
	public List<Map<String, Object>> getFiles(String fileTypeCd, long popupSeq) {
		FileSearchDTO fileScDTO = new FileSearchDTO();
		fileScDTO.setRefCode(fileTypeCd);
		fileScDTO.setRefSeq(popupSeq);
		fileScDTO.setDelYn("N");
		return fileService.getList(fileScDTO);
	}

	@Override
	public int updateState(Collection<PopupVO> list) {
		int retCnt = 0;

		for(PopupVO param : list){
			retCnt += dao.updateState(param);
		}

		return retCnt;
	}

	@Override
	public int updateOrder(Collection<PopupVO> list) {
		int retCnt = 0;

		for(PopupVO param : list){
			retCnt += dao.updateOrder(param);
		}

		return retCnt;
	}

	@Override
	public int delete(long popupSeq) {
		return dao.delete(popupSeq);
	}

	@Override
	public int delete(Collection<Long> seqs) {
		int retCnt = 0;

		for(Long seq : seqs){
			retCnt += delete(seq);
		}

		return retCnt;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected final String getFileTypeCode() {
		return String.format("FT_%S", "POPUP");
	}

	/**
	 * 첨부파일 처리
	 * @param popupSeq
	 * @param regId
	 * @param uploadFiles
	 * @param strDelFileIds
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	protected void procAttachFiles(long popupSeq, long regId, List<MultipartFile> uploadFiles, String strDelFileIds) throws IllegalStateException, IOException {
		String fileTypeCd = getFileTypeCode();

		// 파일 추출 및 저장
		List<FileSetDTO> newFiles = new ArrayList<>();
		for(MultipartFile mFile : uploadFiles) {
			FileSetDTO fDto = fileRepo.storeFile(mFile, fileTypeCd);
			fDto.setRefSeq(popupSeq);
			fDto.setRgtr(regId);
			newFiles.add(fDto);
		}

		String[] delFileIds = !StringUtils.hasText(strDelFileIds) ? new String[0] : strDelFileIds.split(",");
		List<FileSetDTO> delFiles = new ArrayList<>();

		if(delFileIds.length > 0) {
			List<Map<String, Object>> delList = fileRepo.getList(fileTypeCd, popupSeq, delFileIds);
			for(Map<String, Object> delFile : delList) {

				FileSetDTO item = new FileSetDTO();
				item.setFileId(delFile.get("fileId").toString());
				item.setChnr(regId);

				delFiles.add(item);
			}
		}

		//파일 추가 삭제
		fileRepo.setFiles(newFiles, delFiles, Const.DELETE_PHYSICAL_FILE);

		//List<Map<String, Object>> attachFiles = fileRepo.getList(fileTypeCd, param.getPopupSeq());
		//param.setFiles(attachFiles);
	}



}
