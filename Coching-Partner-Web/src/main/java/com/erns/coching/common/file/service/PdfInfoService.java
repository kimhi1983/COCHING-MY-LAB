package com.erns.coching.common.file.service;

import java.util.List;
import java.util.Map;

import com.erns.coching.common.file.domain.PageImageDTO;
import com.erns.coching.common.file.domain.PdfInfoSearchDTO;
import com.erns.coching.common.file.domain.PdfInfoVO;
import com.erns.coching.common.file.domain.ResultPdfAllProcessDTO;

public interface PdfInfoService {

  public List<Map<String, Object>> getList(PdfInfoSearchDTO param);	
	public int getListCount(PdfInfoSearchDTO param);
	
	public Map<String, Object> load(PdfInfoSearchDTO param);
	public PdfInfoVO loadVo(String fileId);
	
	public int insert(PdfInfoVO param);	
	public int delete(String fileId);

	public List<PageImageDTO> getPageImageList(PdfInfoVO pdfVO) throws Exception;
  public ResultPdfAllProcessDTO pdfAllProcess(String fileId) throws Exception;
	
}
