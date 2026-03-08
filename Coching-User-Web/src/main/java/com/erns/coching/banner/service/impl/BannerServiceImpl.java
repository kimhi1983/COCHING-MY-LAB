package com.erns.coching.banner.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.banner.domain.BannerSearchDTO;
import com.erns.coching.banner.mapper.BannerDAO;
import com.erns.coching.banner.service.BannerService;
import com.erns.coching.common.file.domain.FileSearchDTO;
import com.erns.coching.common.file.repository.FileRepository;
import com.erns.coching.common.file.service.FileService;

/**
 *
 * <p>배너정보 Service</p>
 *
 * @author Kyungmin Lee
 *
 */
@Service
@Transactional
public class BannerServiceImpl implements BannerService {

	@Autowired
	private BannerDAO dao;

	@Autowired
	private FileRepository fileRepo;

	@Autowired
	private FileService fileService;

	@Override
	public List<Map<String, Object>> getList(BannerSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(BannerSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public Map<String, Object> load(BannerSearchDTO param) {
		return dao.load(param);
	}

	@Override
	public int updateClicks(BannerSearchDTO param) {
		return dao.updateClicks(param);
	}

	@Override
	public List<Map<String, Object>> getFiles(BannerSearchDTO param) {
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

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected final String getFileTypeCode() {
		return String.format("FT_%S", "BANNER");
	}	
}
