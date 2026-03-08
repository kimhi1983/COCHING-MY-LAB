package com.erns.coching.favorite.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.favorite.domain.FavoriteSearchDTO;
import com.erns.coching.favorite.domain.FavoriteVO;
import com.erns.coching.favorite.mapper.FavoriteDAO;
import com.erns.coching.favorite.service.FavoriteService;

/**
 *
 * <p>찜 목록 Service</p>
 *
 * @author Kyungmin Lee
 *
 */
@Transactional
@Service
public class FavoriteServiceImpl implements FavoriteService {

	@Autowired
	private FavoriteDAO dao;

	@Override
	public List<Map<String, Object>> getList(FavoriteSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(FavoriteSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public int insert(FavoriteVO param) {
		return dao.insert(param);
	}

	@Override
	public int delete(FavoriteVO param) {
		return dao.delete(param);
	}


}
