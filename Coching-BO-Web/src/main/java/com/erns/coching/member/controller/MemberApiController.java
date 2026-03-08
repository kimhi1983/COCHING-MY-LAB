package com.erns.coching.member.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erns.coching.common.Const;
import com.erns.coching.common.aop.ApiLogging;
import com.erns.coching.common.controller.AbstractApiController;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.model.PageInfo;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.SiteType;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.common.type.UserRole;
import com.erns.coching.config.JwtConfig;
import com.erns.coching.login.domain.AclTokenVO;
import com.erns.coching.login.domain.LoginUserDTO;
import com.erns.coching.login.service.AclTokenService;
import com.erns.coching.login.service.LoginService;
import com.erns.coching.member.domain.MemberMultipleSetDTO;
import com.erns.coching.member.domain.MemberSearchDTO;
import com.erns.coching.member.domain.MemberSetDTO;
import com.erns.coching.member.domain.MemberVO;
import com.erns.coching.member.domain.vg.ValidMember0001;
import com.erns.coching.member.domain.vg.ValidMember0002;
import com.erns.coching.member.domain.vg.ValidMember0003;
import com.erns.coching.member.domain.vg.ValidMember0004;
import com.erns.coching.member.domain.vg.ValidMember0032;
import com.erns.coching.member.domain.vg.ValidSetMember0001;
import com.erns.coching.member.domain.vg.ValidSetMember0002;
import com.erns.coching.member.service.MemberService;
import com.erns.coching.member.service.MemberWithdrawInfoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * <p>회원정보 API Controller</p>
 *
 * @author Hunwoo Park
 *
 */
@RestController
@RequestMapping("/api/member")
@PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
@Slf4j
public class MemberApiController extends AbstractApiController {

	@Autowired
	private MemberService memberService;

	@Value("#{'${server.user.base.url:https://www.coching.co.kr}'.replaceAll('\\s+', '').split(',')}")
	private String USER_SERVER_BASE_URL = "";

	@Value("#{'${server.ptn.base.url:https://www.cochingprt.co.kr}'.replaceAll('\\s+', '').split(',')}")
	private String PARTNER_SERVER_BASE_URL = "";

	@Autowired
	protected JwtConfig jwtConfig;

	@Autowired
	protected LoginService loginService;

	@Autowired
	protected AclTokenService aclTokenService;

	@Autowired
	private MemberWithdrawInfoService memberWithdrawInfoService;

	/**
	 * 사용자회원 계정 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MEMBER_USER_LIST)
	@PostMapping("/list.api")
	public ApiResult getMemberUserList(
            HttpServletRequest request,
            @RequestBody @Validated(ValidMember0001.class) MemberSearchDTO param,
            Errors errors) {

		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		PageInfo pi = new PageInfo(0, param);
		int totalItem = memberService.getListCount(param);
		pi.setCurrentPage(totalItem, param.getPage());

        List<Map<String, Object>> memberList = memberService.getList(param);
		axRet.setSc(param).setPageInfo(pi).setList(memberList).set(ApiResultError.NO_ERROR);

        return axRet;
	}

	/**
	 * 사용자회원 계정 상세
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MEMBER_USER_GET)
	@PostMapping("/get.api")
	public ApiResult getMemberUser(
			HttpServletRequest request,
			@RequestBody @Validated(ValidMember0002.class) MemberSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		Map<String, Object> member = memberService.load(param);
		axRet.setResultData(member).set(ApiResultError.NO_ERROR);

		return axRet;
	}

	/**
	 * <p>회원정보 수정</p>
	 * @param request
	 * @param setDTO
	 * @param errors 파라미터 바인딩 오류 내역
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MEMBER_USER_UDT)
	@PostMapping("/updateMember.api")
	public ApiResult updateMember(
			HttpServletRequest request,
			@RequestBody @Validated(ValidSetMember0001.class) MemberSetDTO setDTO,
			Errors errors) throws JOSEException {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		LoginUserDTO loginInfo = getLoginedUserObject();
		setDTO.setRgtr(loginInfo.getSeq());
		setDTO.setChnr(loginInfo.getSeq());
		if(memberService.updateMember(setDTO) <= 0){
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}
		axRet.set(ApiResultError.NO_ERROR);

		return axRet;
	}

	/**
	 * <p>비밀번호 초기화</p>
	 * @param request
	 * @param paramDto
	 * @param errors 파라미터 바인딩 오류 내역
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MEMBER_PW_INIT)
	@RequestMapping("/initPasswd.api")
	public ApiResult initPasswd(
			HttpServletRequest request,
			@RequestBody @Validated(ValidSetMember0002.class) MemberSetDTO paramDto,
			Errors errors) {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		if(memberService.initPasswd(paramDto) <= 0){
			//초기화 실패
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		axRet.set(ApiResultError.NO_ERROR);

		return axRet;
	}

	/**
	 * APP 사용자회원 계정 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 * @throws JsonProcessingException
	 * @throws JOSEException
	 */
	@ApiLogging
	@PostMapping("/emulator/{membtype}.api")
	public ApiResult getEmulatorApp(
            HttpServletRequest request,
            @PathVariable String membtype,
            @RequestBody @Validated(ValidMember0003.class) MemberSearchDTO param,
            Errors errors) throws JsonProcessingException, JOSEException {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		if(!("user".equals(membtype) || "ptn".equals(membtype))) {
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		//API Call
		MemberVO uInfo = memberService.loadMemb(param);
		if(uInfo == null) {
			return axRet.set(ApiResultError.LOGIN_ERR_NOT_FOUND_USER);
		}

		log.info("사용자 에뮬레이터 로그인,{}", uInfo);
		LoginUserDTO luObj = new LoginUserDTO(uInfo);
		List<String> userRoles = membtype.equals("user") ? Arrays.asList(UserRole.COCHING_USER.getValue()) : Arrays.asList(UserRole.COCHING_PARTNER.getValue());
		Map<String, Object> retLogin = doLogin(luObj, userRoles, membtype.equals("user") ?  SiteType.USER_WEB : SiteType.PARTNER_WEB);

		String snsLoginRet = mapper.writeValueAsString(retLogin);

		Map<String, Object> retData = new HashMap<String, Object>();
		retData.put("snsAction", "login");
		retData.put("snsLoginRet", snsLoginRet);
		retData.put("targetUrl", membtype.equals("user") ? USER_SERVER_BASE_URL : PARTNER_SERVER_BASE_URL);

	    return axRet.set(ApiResultError.NO_ERROR).setResultData(retData);
	}

	/**
	 * 로그인 처리
	 * @param uInfo
	 * @return
	 * @throws JOSEException
	 */
	@ApiLogging
	protected Map<String, Object> doLogin(LoginUserDTO uInfo, List<String> roleList, SiteType site) throws JOSEException {
		AclTokenVO jwtAccessToken = jwtConfig.createAccessToken(uInfo, roleList, site);
		AclTokenVO jwtRefreshToken = jwtConfig.createRefreshToken(uInfo, roleList, false, site);
        log.debug("new make jwtAccessToken:{}", jwtAccessToken.getToken());
        log.debug("new make jwtRefreshToken:{}", jwtRefreshToken.getToken());

        {	//DB refresh token 저장
        	aclTokenService.addRefreshToken(jwtRefreshToken);

        	//DB access token 저장
        	aclTokenService.addAccessToken(jwtAccessToken);
        }

        try {
        	loginService.setLogin(uInfo);
        	addUserLog(uInfo, UserLogType.LOGIN, String.format("emulator:%s", site.getCode()));
        }catch (Exception e) {
        	log.error(" Write Log Error! At user login.", e);
			e.printStackTrace();
		}

        Map<String, Object> retLogin = new HashMap<String,Object>();
        retLogin.put(Const.AUTH_TOKEN_ACCESS, jwtAccessToken.getToken());
        retLogin.put(Const.AUTH_TOKEN_REFRESH, jwtRefreshToken.getToken());
        retLogin.put("pwdChangeDate", uInfo.getPwdChangeDate());
        retLogin.put("pwdInitYn", uInfo.getPwdInitYn());
        retLogin.put("userData", getProfile(uInfo));
        retLogin.put("userRoles", roleList);

        return retLogin;
	}

	@ApiLogging
	protected Map<String, Object> getProfile(LoginUserDTO uInfo){
		Map<String, Object> profile = new HashMap<String,Object>();
		profile.put("userSeq", uInfo.getSeq());
		profile.put("ptnSeq", uInfo.getPtnSeq());
		profile.put("id", uInfo.getUserId());
		profile.put("userType", uInfo.getUserType());
		profile.put("userName", uInfo.getUserName());
		profile.put("email", uInfo.getEmail());
		profile.put("nickname", uInfo.getNickname());
		//개인정보 사항 추가 금지(ex:phone)

		return profile;
	}

	@ApiLogging
	@PostMapping("/chkDuplicate.api")
	public ApiResult chkDuplicate(
			HttpServletRequest request,
			@RequestBody @Validated(ValidMember0004.class) MemberSearchDTO paramDto,
			Errors errors) throws JOSEException {
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

		return axRet.set(ApiResultError.NO_ERROR).setResultData(data);
	}

	/**
	 * <p>사용여부 변경</p>
	 * @param request
	 * @param paramDto
	 * @param errors 파라미터 바인딩 오류 내역
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MEMBER_USEYN_SET)
	@RequestMapping("/update/useYn.api")
	public ApiResult updateUseYn(
			HttpServletRequest request,
			@RequestBody @Validated(ValidMember0002.class) MemberSetDTO paramDto,
			Errors errors) {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		LoginUserDTO loginInfo = getLoginedUserObject();
		paramDto.setChnr(loginInfo.getSeq());
		memberService.updateMemberUseYn(paramDto);

		axRet.setSc(paramDto).set(ApiResultError.NO_ERROR);

		return axRet;
	}

	/**
	 * 사용자 상태 설정
	 *
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MEMBER_USER_UDT_STATE)
	@PostMapping("/setState.api")
	public ApiResult setPopupState(
			HttpServletRequest request,
			@RequestBody @Validated(ValidMember0032.class) MemberMultipleSetDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { // 파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			LoginUserDTO loginUser = getLoginedUserObject();

			List<MemberVO> setList = new ArrayList<>();
			for (MemberSetDTO setDto : param.getList()) {
				if (setDto.getMembSeq() <= 0){
					continue;
				}

				MemberVO member = MemberVO.builder()
						.membSeq(setDto.getMembSeq())
						.useYn(setDto.getUseYn())
						.chnr(loginUser.getSeq())
						.build();

				setList.add(member);
			}

			int ret = memberService.updateState(setList);
			axRet.setResultData(ret)
				.set(ApiResultError.NO_ERROR);

		} catch (Exception e) {
			log.error("error", e);
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}

}
