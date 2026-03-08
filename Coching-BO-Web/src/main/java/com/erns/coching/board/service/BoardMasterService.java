package com.erns.coching.board.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.erns.coching.board.domain.BoardMasterVO;
import com.erns.coching.board.domain.BoardSearchDTO;

/**
 *
 * <p>게시판 관리 Service</p>
 *
 * @author Hunwoo Park
 *
 */
public interface BoardMasterService {

	public List<Map<String, Object>> getList(BoardSearchDTO param);
	public int getListCount(BoardSearchDTO param);

	public Map<String, Object> load(BoardSearchDTO param);
	public int insert(BoardMasterVO param);
	public int update(BoardMasterVO param);
	public int updateUseYn(BoardMasterVO param);
	
	public int updateState(Collection<BoardMasterVO> list);
	
	public int delete(String boardMstId);
}
