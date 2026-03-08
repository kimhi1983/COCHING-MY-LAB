package com.erns.coching.url.service;

import com.erns.coching.url.domain.ShortUrlVO;

public interface ShortUrlService {
	
	public ShortUrlVO load(String id);

	public int insert(ShortUrlVO param);
	
	public int updateHits(String id);	
}
