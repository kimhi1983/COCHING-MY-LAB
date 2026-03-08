package com.erns.coching.banner.service.impl;

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

import com.erns.coching.banner.domain.BannerAdSearchDTO;
import com.erns.coching.banner.domain.BannerAdVO;
import com.erns.coching.banner.mapper.BannerAdDAO;
import com.erns.coching.banner.service.BannerAdService;
import com.erns.coching.common.Const;
import com.erns.coching.common.file.domain.FileSearchDTO;
import com.erns.coching.common.file.domain.FileSetDTO;
import com.erns.coching.common.file.repository.FileRepository;
import com.erns.coching.common.file.service.FileService;

/**
 * 
 * <p>배너 광고 Service</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Service
@Transactional
public class BannerAdServiceImpl implements BannerAdService {
	
	@Autowired 
	private BannerAdDAO dao;
	
	@Autowired
	private FileRepository fileRepo;

	@Autowired
	private FileService fileService;

	@Override
	public List<Map<String, Object>> getList(BannerAdSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(BannerAdSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public Map<String, Object> load(BannerAdSearchDTO param) {
		return dao.load(param);
	}

	@Override
	public int insert(BannerAdVO param) {
		return dao.insert(param);
	}

	@Override
	public int update(BannerAdVO param) {
		return dao.update(param);
	}

	@Override
	public Map<String, Object> insert(BannerAdVO bannerVO, List<MultipartFile> newFiles)
			throws IllegalStateException, IOException {
		if(dao.insert(bannerVO) <= 0) {
			return null;
		}

		long bannerSeq = bannerVO.getBannerSeq();
		procAttachFiles(bannerSeq, bannerVO.getChnr(), newFiles, null);

		BannerAdSearchDTO bannerAdScDTO = new BannerAdSearchDTO();
		bannerAdScDTO.setBannerSeq(bannerSeq);

		Map<String, Object> retMap = dao.load(bannerAdScDTO);
		return retMap;
	}

	@Override
	public Map<String, Object> update(BannerAdVO bannerVO, List<MultipartFile> addFiles, String strDelFileIds)
			throws IllegalStateException, IOException {
		if(dao.update(bannerVO) <= 0) {
			return null;
		}

		long bannerSeq = bannerVO.getBannerSeq();
		procAttachFiles(bannerSeq, bannerVO.getChnr(), addFiles, strDelFileIds);

		BannerAdSearchDTO bannerAdScDTO = new BannerAdSearchDTO();
		bannerAdScDTO.setBannerSeq(bannerSeq);

		Map<String, Object> retMap = dao.load(bannerAdScDTO);
		return retMap;
	}

	@Override
	public List<Map<String, Object>> getFiles(BannerAdSearchDTO param) {
		String fileTypeCd = getFileTypeCode();
		return getFiles(fileTypeCd, param.getBannerSeq());
	}

	@Override
	public List<Map<String, Object>> getFiles(String fileTypeCd, long bannerSeq) {
		FileSearchDTO fileScDTO = new FileSearchDTO();
		fileScDTO.setRefCode(fileTypeCd);
		fileScDTO.setRefSeq(bannerSeq);
		fileScDTO.setDelYn("N");
		return fileService.getList(fileScDTO);
	}

	@Override
	public int updateDispYn(Collection<BannerAdVO> list) {
		int retCnt = 0;
		
		for(BannerAdVO param : list) {
			retCnt =+ dao.updateDispYn(param);
		}
		
		return retCnt; 
	}

	@Override
	public int updateUseYn(Collection<BannerAdVO> list) {
		int retCnt = 0;
		
		for(BannerAdVO param : list) {
			retCnt =+ dao.updateUseYn(param);
		}
		
		return retCnt; 
	}
	
	@Override
	public int updateDelYn(BannerAdVO param) {
		return dao.updateDelYn(param);
	}

	@Override
	public int updateDelYn(Collection<BannerAdVO> list) {
		int retCnt = 0;
		
		for(BannerAdVO param : list) {
			retCnt =+ dao.updateDelYn(param);
		}
		
		return retCnt;
	}

	@Override
	public int updateOrder(Collection<BannerAdVO> list) {
		int retCnt = 0;
		
		for(BannerAdVO param : list) {
			retCnt =+ dao.updateOrder(param);
		}
		
		return retCnt;
	}
	
	@Override
	public int updateState(Collection<BannerAdVO> list) {
		int retCnt = 0;

		for(BannerAdVO param : list){
			retCnt += dao.updateState(param);
		}

		return retCnt;
	}

	@Override
	public int updateClicks(BannerAdSearchDTO param) {
		return dao.updateClicks(param);
	}

	@Override
	public int delete(BannerAdSearchDTO param) {
		return dao.delete(param);
	}
	
	@Override
	public int delete(Collection<BannerAdSearchDTO> seqs) {
		int retCnt = 0;

		for(BannerAdSearchDTO seq : seqs){
			retCnt += delete(seq);
		}

		return retCnt;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected final String getFileTypeCode() {
		return String.format("FT_%S", "BANNER_AD");
	}	
	
	/**
	* 첨부파일 처리
	* @param bannerSeq
	* @param regId
	* @param uploadFiles
	* @param strDelFileIds
	* @throws IllegalStateException
	* @throws IOException
	*/
	protected void procAttachFiles(long bannerSeq, long regId
			, List<MultipartFile> uploadFiles, String strDelFileIds) throws IllegalStateException, IOException {
		String fileTypeCd = getFileTypeCode();

		// 파일 추출 및 저장
		List<FileSetDTO> newFiles = new ArrayList<>();
		for(MultipartFile mFile : uploadFiles) {
			FileSetDTO fDto = fileRepo.storeFile(mFile, fileTypeCd);
			fDto.setRefSeq(bannerSeq);
			fDto.setRgtr(regId);
			newFiles.add(fDto);
		}

		String[] delFileIds = !StringUtils.hasText(strDelFileIds) ? new String[0] : strDelFileIds.split(",");
		List<FileSetDTO> delFiles = new ArrayList<>();
	
		if(delFileIds.length > 0) {
			List<Map<String, Object>> delList = fileRepo.getList(fileTypeCd, bannerSeq, delFileIds);
			for(Map<String, Object> delFile : delList) {
	
				FileSetDTO item = new FileSetDTO();
				item.setFileId(delFile.get("fileId").toString());
				item.setChnr(regId);
	
				delFiles.add(item);
			}
		}

		//파일 추가 삭제
		fileRepo.setFiles(newFiles, delFiles, Const.DELETE_PHYSICAL_FILE);

		//List<Map<String, Object>> attachFiles = fileRepo.getList(fileTypeCd, param.getBannerSeq());
		//param.setFiles(attachFiles);
	}

}
