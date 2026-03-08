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
	public int toggle(FavoriteVO param) {
		FavoriteVO vo = dao.load(param);
		if(vo == null) {
			dao.insert(param);
			return 1;
		} else {
			dao.delete(vo);
			return 0;
		}
	}

	@Override
	public int deleteForUser(FavoriteSearchDTO param) {
		List<FavoriteVO> delList = dao.getListVo(param);

		int delCnt = 0;
		long actMembSeq = param.getMembSeq();

		for (FavoriteVO vo : delList) {
			if (actMembSeq == vo.getMembSeq()) {
				delCnt += dao.delete(vo);				
			}
		}

		return delCnt;
	}


}
