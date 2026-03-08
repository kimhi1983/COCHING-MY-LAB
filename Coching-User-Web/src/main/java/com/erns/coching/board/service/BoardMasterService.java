package com.erns.coching.board.service;

import java.util.List;
import java.util.Map;

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
}
