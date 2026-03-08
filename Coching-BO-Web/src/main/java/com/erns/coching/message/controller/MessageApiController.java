package com.erns.coching.message.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erns.coching.common.aop.ApiLogging;
import com.erns.coching.common.controller.AbstractApiController;
import com.erns.coching.common.file.repository.FileRepository;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.model.PageInfo;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.message.domain.MessageSearchDTO;
import com.erns.coching.message.service.MessageService;

import lombok.extern.slf4j.Slf4j;

/**
 * 쪽지 API
 * 
 * @author hw.park@erns.co.kr
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/message")
@PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_COCHING_USER')")
public class MessageApiController extends AbstractApiController {

	@Autowired
	private MessageService messageService;

	@Autowired
	private FileRepository fileRepo;

	/**
	 * 쪽지 목록
	 * 
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MESSAGE_LIST)
	@PostMapping("/list.api")
	public ApiResult getMessageList(
			HttpServletRequest request,
			@RequestBody MessageSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		// API Call
		PageInfo pi = new PageInfo(0, param);
		int totalItem = messageService.getListCount(param);
		List<Map<String, Object>> list = null;
		if (totalItem <= 0) {
			list = new ArrayList<>();
		} else {
			list = messageService.getList(param);
			pi.setCurrentPage(totalItem, param.getPage());
		}
		pi.setCurrentPage(totalItem, param.getPage());

		axRet.setSc(param)
			.setPageInfo(pi)
			.setList(list)
			.set(ApiResultError.NO_ERROR);

		return axRet;
	}

}
