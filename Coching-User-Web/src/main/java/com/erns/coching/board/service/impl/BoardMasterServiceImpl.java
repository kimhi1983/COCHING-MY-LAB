package com.erns.coching.board.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.board.domain.BoardSearchDTO;
import com.erns.coching.board.mapper.BoardMasterDAO;
import com.erns.coching.board.service.BoardMasterService;

/**
 * 
 * <p>게시판 관리 Service</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Service
@Transactional
public class BoardMasterServiceImpl implements BoardMasterService{

	@Autowired
	private BoardMasterDAO dao;

	@Override
	public List<Map<String, Object>> getList(BoardSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(BoardSearchDTO param) {
		return dao.getListCount(param);
	}
	
	@Override
	public Map<String, Object> load(BoardSearchDTO param) {
		return dao.load(param);
	}


}
