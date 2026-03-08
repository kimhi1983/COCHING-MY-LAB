package com.erns.coching.favorite.service;

import java.util.List;
import java.util.Map;

import com.erns.coching.favorite.domain.FavoriteSearchDTO;
import com.erns.coching.favorite.domain.FavoriteVO;

/**
 *
 * <p>찜 목록 Service</p>
 *
 * @author Kyungmin Lee
 *
 */
public interface FavoriteService {

	public List<Map<String, Object>> getList(FavoriteSearchDTO param);
	public int getListCount(FavoriteSearchDTO param);

	public int toggle(FavoriteVO param);
	
	public int deleteForUser(FavoriteSearchDTO param);
	
}
