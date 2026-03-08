package com.erns.coching.common.file.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.common.file.domain.PdfInfoSearchDTO;
import com.erns.coching.common.file.domain.PdfInfoVO;

/**
 * <p>PDF 정보 Mapper</p>
 */
@Mapper
public interface PdfInfoDAO {

  public List<Map<String, Object>> getList(PdfInfoSearchDTO param);	
	public int getListCount(PdfInfoSearchDTO param);
	
	public Map<String, Object> load(PdfInfoSearchDTO param);
	public PdfInfoVO loadVo(String fileId);
	
	public int insert(PdfInfoVO param);	
	public int delete(String fileId);

	public List<String> getBatchConvertList();
}
