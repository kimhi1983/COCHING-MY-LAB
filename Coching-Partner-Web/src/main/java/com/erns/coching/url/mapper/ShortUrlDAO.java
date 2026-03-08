package com.erns.coching.url.mapper;

import com.erns.coching.url.domain.ShortUrlVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShortUrlDAO {

	public ShortUrlVO load(String id);

	public int insert(ShortUrlVO param);

	public int updateHits(String id);
}
