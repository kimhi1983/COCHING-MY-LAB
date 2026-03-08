package com.erns.coching.member.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.common.util.CryptoAESUtil;
import com.erns.coching.common.util.DateUtil;
import com.erns.coching.login.domain.LoginUserDTO;
import com.erns.coching.member.domain.MemberSearchDTO;
import com.erns.coching.member.domain.MemberSetDTO;
import com.erns.coching.member.domain.MemberWithdrawDTO;
import com.erns.coching.member.domain.MemberWithdrawInfoVO;
import com.erns.coching.member.domain.vg.ValidMember0002;
import com.erns.coching.member.domain.vg.ValidMember0003;
import com.erns.coching.member.domain.vg.ValidMember0004;
import com.erns.coching.member.domain.vg.ValidMember0005;
import com.erns.coching.member.domain.vg.ValidMemberList0001;
import com.erns.coching.member.domain.vg.ValidWithdraw0001;
import com.erns.coching.member.domain.vg.ValidWithdraw0002;
import com.erns.coching.member.service.MemberService;

import lombok.extern.slf4j.Slf4j;

/**
 * 회원 정보 API
 * @author hw.park@erns.co.kr
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/member")
//@PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_COCHING_PARTNER')")
public class MemberApiController extends AbstractApiController {

	public static final String FILE_INPUT_NAME = "membSign_files";
	@Autowired
	private MemberService memberService;

	@Autowired
	private FileRepository fileRepo;

	/**
	 * <p>사용자 정보 로드</p>
	 * @param request
	 * @param param
	 * @param errors 파라미터 바인딩 오류 내역
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MEMBER_MY_INFO)
	@RequestMapping("/membInfo.api")
	public ApiResult getMemInfo(
			HttpServletRequest request,
			@RequestBody MemberSearchDTO paramDto,
			Errors errors) {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		Map<String, Object> myInfo = memberService.loadMembInfo(paramDto.getMembSeq());
		axRet.setResultData(myInfo).set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * <p>사용자 리스트</p>
	 * @param request
	 * @param param
	 * @param errors 파라미터 바인딩 오류 내역
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MEMBER_PARTNER_USER_LIST)
	@RequestMapping("/list.api")
	public ApiResult getList(
			HttpServletRequest request,
			@RequestBody @Validated(ValidMemberList0001.class) MemberSearchDTO paramDto,
			Errors errors) {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		List<Map<String, Object>> list = null;
		int totalItem = memberService.getListCount(paramDto);
		if(totalItem <= 0) {
			list = new ArrayList<Map<String, Object>>();
		}else {
			list = memberService.getList(paramDto);
		}

		axRet.setSc(paramDto).setList(list).set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * 사용자회원 계정 상세
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MEMBER_MY_INFO)
	@PostMapping("/get.api")
	public ApiResult getMemberUser(
			HttpServletRequest request,
			@RequestBody @Validated(ValidMember0002.class) MemberSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		Map<String, Object> member = memberService.loadMembInfo(param.getMembSeq());
		axRet.setResultData(member).set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}


	/**
	 * <p>사용여부 변경</p>
	 * @param request
	 * @param param
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
		memberService.updateUseYn(paramDto);

		axRet.setSc(paramDto).set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * 사용자 탈회(탈퇴) 조회
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MEMBER_WITHDRAW_INFO)
	@PostMapping("/withdrawInfo.api")
	public ApiResult withdrawInfo(
            HttpServletRequest request,
            @RequestBody @Validated(ValidWithdraw0001.class) MemberWithdrawDTO paramDto,
            Errors errors) {

        ApiResult axRet = new ApiResult();
        if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

        LoginUserDTO loginInfo = getLoginedUserObject();

        //paramDto.setMembSeq(loginInfo.getSeq());
        //paramDto.setMembId(loginInfo.getUserId());

        try{	//탈회 검증용 토큰 생성
        	String strDate = DateUtil.SDF_DATEDN.format(new Date());
            String enKey = String.format("Coching%s", strDate);
            String cLeaveToken = String.format("%d%s", loginInfo.getSeq(), loginInfo.getEmail());
            String eLeaveToken = CryptoAESUtil.encrypt(enKey, cLeaveToken);

            log.debug("cLeaveToken:{}", cLeaveToken);
    		log.debug("eLeaveToken:{}", eLeaveToken);
    		paramDto.setWithdrawToken(eLeaveToken);
        }catch (Exception e) {
        	log.error("탈회 토큰 생성 오류", e);
        	return axRet;
		}

		return axRet.set(ApiResultError.NO_ERROR, getLocale()).setSc(paramDto);
	}


	/**
	 * 사용자 탈회(탈퇴)
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MEMBER_WITHDRAW)
	@PostMapping("/withdraw.api")
	public ApiResult withdrawMember(
            HttpServletRequest request,
            @RequestBody @Validated(ValidWithdraw0002.class) MemberWithdrawDTO paramDto,
            Errors errors) {

        ApiResult axRet = new ApiResult();
        if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

        LoginUserDTO loginInfo = getLoginedUserObject();
        paramDto.setMembSeq(loginInfo.getSeq());
        paramDto.setEmail(loginInfo.getEmail());

        try{	//탈회 검증용 토큰 생성
        	String strDate = DateUtil.SDF_DATEDN.format(new Date());
            String enKey = String.format("Coching%s", strDate);
            String cLeaveToken = String.format("%d%s", loginInfo.getSeq(), loginInfo.getEmail());
            String eLeaveToken = CryptoAESUtil.encrypt(enKey, cLeaveToken);

            if(!paramDto.getWithdrawToken().equals(eLeaveToken)) {
            	return axRet.set(ApiResultError.WITHDRAW_ERR_INVALID_TOKEN, getLocale());
            }
        }catch (Exception e) {
        	log.error("탈회 토큰 생성 오류", e);
        	return axRet;
		}

        MemberWithdrawInfoVO mwiVo = MemberWithdrawInfoVO.MemberWidthdrawBuilder()
        	.fromDto(paramDto)
        	.rgtr(loginInfo)
        	.build();


        //API Call
        int retVal = memberService.withdraw(mwiVo);
        if(retVal > 0) {
        	axRet.set(ApiResultError.NO_ERROR, getLocale());
        }

        return axRet;
	}

	/**
	 * <p>비밀번호 변경일 초기화</p>
	 * @param request
	 * @param param
	 * @param errors 파라미터 바인딩 오류 내역
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MEMBER_PW_CHNG_DTTM_RESET)
	@RequestMapping("/update/pwChngDttm.api")
	public ApiResult updatePwChngDttm(
			HttpServletRequest request,
			@RequestBody @Validated(ValidMember0003.class) MemberSetDTO paramDto,
			Errors errors) {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		memberService.updatePwChngDttm(paramDto);

		axRet.setSc(paramDto).set(ApiResultError.NO_ERROR, getLocale());

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
			@RequestBody @Validated(ValidMember0004.class) MemberSetDTO paramDto,
			Errors errors) {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		LoginUserDTO userDTO = getLoginedUserObject();
		paramDto.setChnr(userDTO.getSeq());
		if(memberService.initPasswd(paramDto) <= 0){
			//초기화 실패
			return axRet.set(ApiResultError.ERROR_DEFAULT, getLocale());
		}

		axRet.set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * <p>비밀번호 초기화</p>
	 * @param request
	 * @param paramDto
	 * @param errors 파라미터 바인딩 오류 내역
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MEMBER_PW_CHANGE)
	@RequestMapping("/changePasswd.api")
	public ApiResult changePasswd(
			HttpServletRequest request,
			@RequestBody @Validated(ValidMember0005.class) MemberSetDTO paramDto,
			Errors errors) {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		if(memberService.changePasswd(paramDto)<= 0){
			//변경 실패
			return axRet.set(ApiResultError.ERROR_DEFAULT, getLocale());
		}

		axRet.set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}
}
