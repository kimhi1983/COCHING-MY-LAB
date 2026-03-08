package com.erns.es.login.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.erns.es.login.domain.AclTokenSearchDTO;
import com.erns.es.login.domain.AclTokenVO;

/**
 *
 * <p>Access Token Mapper</p>
 *
 * @author Hunwoo Park
 *
 */
@Mapper
public interface AccessTokenDAO {

	public AclTokenVO load(String token);
	public int insert(AclTokenVO param);
	public int delete(String token);

	public List<AclTokenVO> selectList(AclTokenSearchDTO param);

	public int deleteExpiredTokens();

}
