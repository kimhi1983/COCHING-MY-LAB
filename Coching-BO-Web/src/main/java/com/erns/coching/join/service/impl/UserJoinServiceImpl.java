package com.erns.coching.join.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.join.domain.UserJoinDTO;
import com.erns.coching.join.service.UserJoinService;
import com.erns.coching.partner.service.PartnerService;

/**
 *
 * <p>계정생성 Service</p>
 *
 * @author Kyunmin Lee
 *
 */
@Service
@Transactional
public class UserJoinServiceImpl implements UserJoinService {

	@Autowired
	private PartnerService partnerService;

	@Override
	public boolean addUser(UserJoinDTO joinData) {

		return false;
	}

}
