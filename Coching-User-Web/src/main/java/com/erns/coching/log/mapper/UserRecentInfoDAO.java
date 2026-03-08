package com.erns.coching.log.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.log.domain.UserRecentInfoSearchDTO;
import com.erns.coching.log.domain.UserRecentInfoVO;

/**
 *
 * <p>사용자 최근 정보 Mapper</p>
 *
 * @author Kyungmin Lee
 *
 */
@Mapper
public interface UserRecentInfoDAO {

  public UserRecentInfoVO load(UserRecentInfoVO param);
  public List<UserRecentInfoVO> getListVo(UserRecentInfoSearchDTO param);
  public List<Map<String, Object>> getList(UserRecentInfoSearchDTO param);
  public int getListCount(UserRecentInfoSearchDTO param);

  public int insert(UserRecentInfoVO param);
  
  public int delete(UserRecentInfoVO param);
  
}