package com.erns.coching.board.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.board.domain.BoardMasterVO;
import com.erns.coching.board.domain.BoardSearchDTO;

/**
 *
 * <p>게시판마스터 DAO Mapper</p>
 *
 * @author Hunwoo Park
 *
 */
@Mapper
public interface BoardMasterDAO {

	public List<Map<String, Object>> getList(BoardSearchDTO param);
	public int getListCount(BoardSearchDTO param);

	public Map<String, Object> load(BoardSearchDTO param);
	public int insert(BoardMasterVO param);
	public int update(BoardMasterVO param);
	public int updateUseYn(BoardMasterVO param);
	public int updateState(BoardMasterVO param);
	public int delete(String boardMstId);
}
