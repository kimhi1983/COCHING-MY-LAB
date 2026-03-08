package com.erns.coching.login.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.login.domain.AclTokenSearchDTO;
import com.erns.coching.login.domain.AclTokenVO;

/**
 * 
 * <p>BlackList Token Mapper</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Mapper
public interface BlacklistTokenDAO {
	
	public AclTokenVO load(String token);
	public int insert(AclTokenVO param);
	public int delete(String token);
	
	public List<AclTokenVO> selectList(AclTokenSearchDTO param);
	
	public int deleteExpiredTokens();
	
	public int insertFromRefresh(String token);
	public int insertFromAccess(String token);
	
	public int insertFromAllRefreshByUserSeq(long userSeq);
	public int insertFromAllAccessByUserSeq(long userSeq);
	
}
