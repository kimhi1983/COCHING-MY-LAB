package com.erns.coching.board.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.board.domain.BoardSearchDTO;
import com.erns.coching.board.domain.BoardVO;

/**
 *
 * <p>게시물 DAO Mapper</p>
 *
 * @author Hunwoo Park
 *
 */
@Mapper
public interface BoardDAO {

	public List<Map<String, Object>> getList(BoardSearchDTO param);
	public int getListCount(BoardSearchDTO param);
	public BoardVO load(int boardSeq);
	public Map<String, Object> loadDetail(BoardSearchDTO param);
	public List<Map<String, Object>> getCmt(BoardSearchDTO param);
	
	public int insert(BoardVO param);
	public int update(BoardVO param);
	public int updateDelYn(BoardVO param);
	public int updateViewCnt(BoardVO param);
	
	public int delete(long boardSeq);
	
}
