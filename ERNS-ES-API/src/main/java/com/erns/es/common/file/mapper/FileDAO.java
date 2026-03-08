package com.erns.es.common.file.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.es.common.file.domain.FileSearchDTO;
import com.erns.es.common.file.domain.FileVO;

/**
 *
 * <p>첨부파일 Mapper</p>
 *
 * @author Hunwoo Park
 *
 */
@Mapper
public interface FileDAO {

	public List<Map<String, Object>> getList(FileSearchDTO param);
	public int getListCount(FileSearchDTO param);
	public List<Map<String, Object>> selectByRefId(FileSearchDTO param);

	public Map<String, Object> load(FileSearchDTO param);

	public int insert(FileVO param);
	public int update(FileVO param);
	public int delete(String fileId);

	public int updateRefSeq(FileVO param);
	public int updateOrder(FileVO param);
	public int updateDelYn(FileVO param);
	public int updateDelYnByRefId(FileVO param);
	public int deleteByRefId(FileVO param);
}
