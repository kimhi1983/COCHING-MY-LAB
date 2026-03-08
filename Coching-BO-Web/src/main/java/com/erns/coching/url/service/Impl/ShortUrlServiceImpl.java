package com.erns.coching.url.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.url.domain.ShortUrlVO;
import com.erns.coching.url.mapper.ShortUrlDAO;
import com.erns.coching.url.service.ShortUrlService;

@Transactional
@Service
public class ShortUrlServiceImpl implements ShortUrlService {

	@Autowired
	private ShortUrlDAO dao;

	@Override
	public ShortUrlVO load(String id) {
		return dao.load(id);
	}

	@Override
	public int insert(ShortUrlVO param) {
		return dao.insert(param);
	}

	@Override
	public int updateHits(String id) {
		return dao.updateHits(id);
	}
}
