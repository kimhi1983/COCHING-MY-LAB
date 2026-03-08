package com.erns.coching.join.service;

import com.erns.coching.common.model.ApiResult;
import com.erns.coching.join.domain.UserJoinDTO;

public interface UserJoinService {

	public ApiResult addUser(UserJoinDTO param);

}
