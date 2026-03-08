package com.erns.coching.join.service;

import com.erns.coching.join.domain.UserJoinDTO;

/**
 *
 * <p>계정생성 Service</p>
 *
 * @author Kyunmin Lee
 *
 */
public interface UserJoinService {

	public boolean addUser(UserJoinDTO param);
}
