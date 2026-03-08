  package com.erns.coching.search.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erns.coching.search.domain.HwBrandSearchDTO;
import com.erns.coching.search.domain.HwBrandVO;
import com.erns.coching.search.mapper.HwBrandDAO;
import com.erns.coching.search.service.HwBrandService;

@Service
public class HwBrandServiceImpl implements HwBrandService {

  @Autowired
  private HwBrandDAO hwBrandDao;

  @Override
  public List<Map<String, Object>> getList(HwBrandSearchDTO param) {
    return hwBrandDao.getList(param);
  }

  @Override
  public List<HwBrandVO> getListVo(HwBrandSearchDTO param) {
    return hwBrandDao.getListVo(param);
  }

  @Override
 	public int getListCount(HwBrandSearchDTO param){
    return hwBrandDao.getListCount(param);
  }

  @Override
  public HwBrandVO load(long seq) {
    return hwBrandDao.load(seq);
  }

  @Override
  public int insert(HwBrandVO param) {
    return hwBrandDao.insert(param);
  }

  @Override
  public int insert(Collection<HwBrandVO> param) {
    int ret = 0;

    for(HwBrandVO vo : param) {
      hwBrandDao.insert(vo);
      ret++;
    }

    return ret;
  }

  @Override
  public int delete(long seq) {
    return hwBrandDao.delete(seq);
  }

  @Override
  public int deleteAll() {
    return hwBrandDao.deleteAll();
  }
}
