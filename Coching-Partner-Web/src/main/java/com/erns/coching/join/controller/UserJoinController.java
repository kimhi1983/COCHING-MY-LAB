package com.erns.coching.join.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erns.coching.common.aop.ApiLogging;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.join.domain.UserJoinDTO;
import com.erns.coching.join.domain.vg.ValidGroupUserJoin0001;
import com.erns.coching.join.domain.vg.ValidGroupUserJoin0002;
import com.erns.coching.join.domain.vg.ValidGroupUserJoin0003;
import com.erns.coching.join.service.UserJoinService;
import com.erns.coching.login.controller.BaseLoginController;
import com.erns.coching.login.domain.LoginUserDTO;
import com.erns.coching.member.domain.MemberVO;
import com.erns.coching.member.service.MemberService;
import com.nimbusds.jose.JOSEException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/join")
public class UserJoinController extends BaseLoginController{

	@Autowired
	private UserJoinService userJoinService;

	@Autowired
	private MemberService memberService;

	@Value("${member.rejoin.limit:0}")
	private int rejoinLimitDate;


    /**
     * <p>회원가입</p>
     * @param request
     * @param joinDto
     * @param errors 파라미터 바인딩 오류 내역
     * @return
     */
	@ApiLogging(logType = UserLogType.JOIN_PARTNER)
	@PostMapping("/userJoin.api")
	public ApiResult userJoin(
			HttpServletRequest request,
			@RequestBody @Validated(ValidGroupUserJoin0001.class) UserJoinDTO joinDto,
			Errors errors) throws JOSEException {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		LoginUserDTO loginInfo = getLoginedUserObject();
		joinDto.setRgtr(loginInfo.getSeq());
		axRet = userJoinService.addUser(joinDto);

		return axRet;
	}

	/**
     * <p>회원정보 수정</p>
     * @param request
     * @param param
     * @param errors 파라미터 바인딩 오류 내역
     * @return
     */
	@ApiLogging(logType = UserLogType.MEMBER_MY_INFO_SET)
	@PostMapping("/userUpdate.api")
	public ApiResult userUpdate(
			HttpServletRequest request,
			@RequestBody @Validated(ValidGroupUserJoin0002.class) UserJoinDTO joinDto,
			Errors errors) throws JOSEException {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		LoginUserDTO loginInfo = getLoginedUserObject();
		joinDto.setChnr(loginInfo.getSeq());
		axRet = memberService.userUpdate(joinDto);

		return axRet;
	}

	@ApiLogging(logType = UserLogType.JOIN_CHK_DUPLICATE)
	@PostMapping("/chkDuplicate.api")
	public ApiResult chkDuplicate(
			HttpServletRequest request,
			@RequestBody @Validated(ValidGroupUserJoin0003.class) UserJoinDTO paramDto,
			Errors errors) throws JOSEException{
		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		MemberVO memberVO = MemberVO
				.MemberDuplicateBuilder()
				.membId(paramDto.getMembId())
				.email(paramDto.getEmail())
				.nickname(paramDto.getNickname())
				.build();
		Map<String, Object> map = memberService.chkDuplicate(memberVO);
		Map<String, Object> data = new HashMap<>();
		if(null != map) {
			data.put("isDuplicate", true);
		}else{
			data.put("isDuplicate", false);
		}

		return axRet.set(ApiResultError.NO_ERROR, getLocale()).setResultData(data);
	}

}
