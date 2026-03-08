package com.erns.coching.join.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.join.domain.UserJoinDTO;

/**
 * 
 * <p>계정생성 Mapper</p> 
 *
 * @author Kyunmin Lee
 *
 */
@Mapper
public interface UserJoinDAO {
	
	public boolean addUser(UserJoinDTO param);
}
