package com.erns.coching.message.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
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
import com.erns.coching.common.type.NotificationType;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.login.domain.LoginUserDTO;
import com.erns.coching.message.domain.MessageSearchDTO;
import com.erns.coching.message.domain.MessageSetDTO;
import com.erns.coching.message.domain.MessageVO;
import com.erns.coching.message.domain.vg.ValidMessage0011;
import com.erns.coching.message.domain.vg.ValidMessage0012;
import com.erns.coching.message.domain.vg.ValidMessage0020;
import com.erns.coching.message.domain.vg.ValidMessage0041;
import com.erns.coching.message.service.MessageService;
import com.erns.coching.notification.service.NotificationService;

import lombok.extern.slf4j.Slf4j;

/**
 * 쪽지 API
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

		@Autowired
	private NotificationService notificationService;
	
	/**
	 * 쪽지 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MESSAGE_LIST)
	@PostMapping("/list.api")
	public ApiResult getMessageList(
            HttpServletRequest request,
            @RequestBody @Validated(ValidMessage0011.class) MessageSearchDTO param,
            Errors errors) {
		
		ApiResult axRet = new ApiResult();
    if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		LoginUserDTO loginUser = getLoginedUserObject();
		param.setUserMembSeq(loginUser.getSeq());
		    
		//API Call
		PageInfo pi = new PageInfo(0, param);
		int totalItem = messageService.getListCount(param);
		List<Map<String, Object>> list = new ArrayList<>();
		if(totalItem > 0) {
			list = messageService.getList(param); //쪽지 목록			
		}
    pi.setCurrentPage(totalItem, param.getPage());

		axRet.setSc(param)
			.setPageInfo(pi)
			.setList(list)
			.set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * 쪽지 상세
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MESSAGE_GET)
	@PostMapping("/get.api")
	public ApiResult getMessageDetail(
            HttpServletRequest request,
            @RequestBody @Validated(ValidMessage0012.class) MessageSearchDTO param,
            Errors errors) {
		log.debug("param: {}", param);
		
		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		LoginUserDTO loginUser = getLoginedUserObject();
		param.setUserMembSeq(loginUser.getSeq());
    
		//API Call
		Map<String, Object> data = messageService.load(param); //쪽지 상세
		if(null != data && loginUser.getSeq() == (int)data.get("receiver")) {
			MessageVO updateStateVo = MessageVO.builder()
					.messageSeq((int)data.get("messageSeq"))
					.state(MessageVO.STATE_READ)
					.build();
			messageService.updateState(updateStateVo); //읽음 처리
		}

		axRet.setSc(param)
			.setResultData(data)
			.set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * 쪽지 보내기
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MESSAGE_SEND)
	@PostMapping("/send.api")
	public ApiResult sendMessage(
            HttpServletRequest request,
            @RequestBody @Validated(ValidMessage0020.class) MessageSetDTO param,
            Errors errors) {
		log.debug("param: {}", param);
		
		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		LoginUserDTO loginUser = getLoginedUserObject();
        
		try{
			//쪽지 등록			
			param.setSender(loginUser.getSeq());
			MessageVO vo = MessageVO.AddMessageBuilder()
					.fromDto(param)
					.build();
			
			int ret = messageService.insert(vo); //쪽지 보내기
			if(ret > 0) {
				
				{ //알림 등록
					String title = loginUser.getUserName();
					String detail = vo.getContent();
					NotificationType notiType = NotificationType.MESSAGE;
					notificationService.insertNoti(notiType,
						vo.getMessageSeq(), vo.getReceiver(), title, detail, vo.getSender());
				}

				axRet.setResultData(ret)
					.set(ApiResultError.NO_ERROR, getLocale());
			}
		}catch(Exception e) {
			e.printStackTrace();
			axRet.set(ApiResultError.ERROR_DEFAULT, getLocale());
		}
		
		return axRet;
	}

	/**
	 * 쪽지 삭제
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MESSAGE_DEL)
	@PostMapping("/del.api")
	public ApiResult deleteMessage(
						HttpServletRequest request,
						@RequestBody @Validated(ValidMessage0041.class) MessageSearchDTO param,
						Errors errors) {
		log.debug("param: {}", param);

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		LoginUserDTO loginUser = getLoginedUserObject();

		try {
			param.setUserMembSeq(loginUser.getSeq());
			
			int ret = messageService.updateUserDelYn(param);
			if(ret > 0) {
				axRet.setResultData(ret)
					.set(ApiResultError.NO_ERROR, getLocale());
			}
						
		}catch(Exception e) {
			e.printStackTrace();
			axRet.set(ApiResultError.ERROR_DEFAULT, getLocale());
		}

		return axRet;
	}
}
