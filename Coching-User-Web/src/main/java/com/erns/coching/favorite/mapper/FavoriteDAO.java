package com.erns.coching.favorite.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.favorite.domain.FavoriteSearchDTO;
import com.erns.coching.favorite.domain.FavoriteVO;


/**
 *
 * <p>찜 목록 정보 Mapper</p>
 *
 * @author Kyungmin Lee
 *
 */
@Mapper
public interface FavoriteDAO {

	public FavoriteVO load(FavoriteVO param);
	public List<FavoriteVO> getListVo(FavoriteSearchDTO param);
	public List<Map<String, Object>> getList(FavoriteSearchDTO param);
	public int getListCount(FavoriteSearchDTO param);

	public int insert(FavoriteVO param);
	
	public int delete(FavoriteVO param);
	
}
