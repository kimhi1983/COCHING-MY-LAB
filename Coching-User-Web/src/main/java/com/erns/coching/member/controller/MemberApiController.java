package com.erns.coching.member.controller;

import java.util.ArrayList;
import java.util.Date;
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
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.common.util.CryptoAESUtil;
import com.erns.coching.common.util.DateUtil;
import com.erns.coching.join.domain.UserJoinDTO;
import com.erns.coching.join.domain.vg.ValidGroupUserJoin0002;
import com.erns.coching.join.domain.vg.ValidGroupUserJoin0004;
import com.erns.coching.join.service.UserJoinService;
import com.erns.coching.login.domain.LoginUserDTO;
import com.erns.coching.member.domain.MemberSearchDTO;
import com.erns.coching.member.domain.MemberSetDTO;
import com.erns.coching.member.domain.MemberWithdrawDTO;
import com.erns.coching.member.domain.MemberWithdrawInfoVO;
import com.erns.coching.member.domain.vg.ValidMember0003;
import com.erns.coching.member.domain.vg.ValidMember0004;
import com.erns.coching.member.domain.vg.ValidMemberList0001;
import com.erns.coching.member.domain.vg.ValidWithdraw0001;
import com.erns.coching.member.domain.vg.ValidWithdraw0002;
import com.erns.coching.member.service.MemberService;
import com.nimbusds.jose.JOSEException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

/**
 * 회원 정보 API
 * @author hw.park@erns.co.kr
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/member")
@PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_COCHING_USER')")
public class MemberApiController extends AbstractApiController {

	public static final String FILE_INPUT_NAME = "profile_files";
	@Autowired
	private MemberService memberService;

	@Autowired
	private UserJoinService userJoinService;

	@Autowired
	private FileRepository fileRepo;

	/**
	 * <p>사용자 정보 로드</p>
	 *
	 * @param request
	 * @param param
	 * @param errors  파라미터 바인딩 오류 내역
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MEMBER_MY_INFO)
	@RequestMapping("/myInfo.api")
	public ApiResult getMyInfo(
			HttpServletRequest request,
			@RequestBody MemberSearchDTO paramDto,
			Errors errors) {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		LoginUserDTO loginInfo = getLoginedUserObject();
		Map<String, Object> myInfo = memberService.loadMyInfo(loginInfo.getSeq());
		axRet.setResultData(myInfo).set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * <p>회원정보 수정</p>
	 * @param request
	 * @param joinDto
	 * @param errors 파라미터 바인딩 오류 내역
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MEMBER_MY_INFO_SET)
	@PostMapping("/updateMemb.api")
	public ApiResult updateMemb(
			HttpServletRequest request,
			@RequestBody @Validated(ValidGroupUserJoin0002.class) UserJoinDTO joinDto,
			Errors errors) throws JOSEException {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		LoginUserDTO loginInfo = getLoginedUserObject();
		joinDto.setMembSeq(loginInfo.getSeq());

		axRet = userJoinService.updateMemb(joinDto);

		return axRet;
	}

	/**
	 * <p>파트너사 정보 수정</p>
	 * @param request
	 * @param joinDto
	 * @param errors 파라미터 바인딩 오류 내역
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MEMBER_PARTNER_INFO_SET)
	@PostMapping("/updatePartner.api")
	public ApiResult updatePartner(
			HttpServletRequest request,
			@RequestBody @Validated(ValidGroupUserJoin0004.class) UserJoinDTO joinDto,
			Errors errors) throws JOSEException {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		LoginUserDTO loginInfo = getLoginedUserObject();
		joinDto.setMembSeq(loginInfo.getSeq());
		axRet = userJoinService.updatePartner(joinDto);

		return axRet;
	}


	/**
	 * <p>사용자 리스트</p>
	 *
	 * @param request
	 * @param param
	 * @param errors  파라미터 바인딩 오류 내역
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

		PageInfo pi = new PageInfo(0, paramDto);

		List<Map<String, Object>> list = null;
		int totalItem = memberService.getListCount(paramDto);
		if (totalItem <= 0) {
			list = new ArrayList<Map<String, Object>>();
		} else {
			list = memberService.getList(paramDto);
			pi.setCurrentPage(totalItem, paramDto.getPage());
		}

		axRet.setSc(paramDto).setPageInfo(pi).setList(list).set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * <p>사용자 기업 정보 조회</p>
	 *
	 * @param request
	 * @param param
	 * @param errors  파라미터 바인딩 오류 내역
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MEMBER_COMPANY_INFO)
	@RequestMapping("/loadContractMemb.api")
	public ApiResult loadContractMemb(
			HttpServletRequest request,
			@RequestBody @Validated(ValidMember0004.class) MemberSearchDTO paramDto,
			Errors errors) {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

//		List<Map<String, Object>> list = memberService.loadContractMemb(paramDto);
//
//		axRet.setSc(paramDto).setList(list).set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * <p>계정 삭제</p>
	 *
	 * @param request
	 * @param param
	 * @param errors  파라미터 바인딩 오류 내역
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MEMBER_DELETE)
	@RequestMapping("/delete.api")
	public ApiResult delete(
			HttpServletRequest request,
			@RequestBody @Validated(ValidMember0003.class) MemberSetDTO paramDto,
			Errors errors) {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		memberService.delete(paramDto.getMembSeq());

		axRet.setSc(paramDto).set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * 사용자 탈회(탈퇴) 조회
	 *
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

		try {    //탈회 검증용 토큰 생성
			String strDate = DateUtil.SDF_DATEDN.format(new Date());
			String enKey = String.format("Coching%s", strDate);
			String cLeaveToken = String.format("%d%s", loginInfo.getSeq(), loginInfo.getEmail());
			String eLeaveToken = CryptoAESUtil.encrypt(enKey, cLeaveToken);

			log.debug("cLeaveToken:{}", cLeaveToken);
			log.debug("eLeaveToken:{}", eLeaveToken);
			paramDto.setWithdrawToken(eLeaveToken);
		} catch (Exception e) {
			log.error("탈회 토큰 생성 오류", e);
			return axRet;
		}

		return axRet.set(ApiResultError.NO_ERROR, getLocale()).setSc(paramDto);
	}


	/**
	 * 사용자 탈회(탈퇴)
	 *
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

		try {    //탈회 검증용 토큰 생성
			String strDate = DateUtil.SDF_DATEDN.format(new Date());
			String enKey = String.format("Coching%s", strDate);
			String cLeaveToken = String.format("%d%s", loginInfo.getSeq(), loginInfo.getEmail());
			String eLeaveToken = CryptoAESUtil.encrypt(enKey, cLeaveToken);

			if (!paramDto.getWithdrawToken().equals(eLeaveToken)) {
				return axRet.set(ApiResultError.WITHDRAW_ERR_INVALID_TOKEN, getLocale());
			}
		} catch (Exception e) {
			log.error("탈회 토큰 생성 오류", e);
			return axRet;
		}

		MemberWithdrawInfoVO mwiVo = MemberWithdrawInfoVO.MemberWidthdrawBuilder()
				.fromDto(paramDto)
				.rgtr(loginInfo)
				.build();


		//API Call
		int retVal = memberService.withdraw(mwiVo);
		if (retVal > 0) {
			axRet.set(ApiResultError.NO_ERROR, getLocale());
		}

		return axRet;
	}

	/**
	 * <p>비밀번호 변경일 초기화</p>
	 *
	 * @param request
	 * @param param
	 * @param errors  파라미터 바인딩 오류 내역
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
	 * 원료 파트너 썸네일 이미지 등록
	 * @param request
	 * @param paramDto
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MEMBER_PROFILE_SET)
	@PostMapping("/setProfileFile.api")
	public ApiResult addThumbFile(
			HttpServletRequest request,
			@Validated() MemberSetDTO paramDto,
			Errors errors) {

		log.debug("param : {}", paramDto);

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}
		LoginUserDTO loginInfo = getLoginedUserObject();
		paramDto.setMembSeq(loginInfo.getSeq());
		List<Map<String, Object>> list = null;
		try{
			// 파일 등록
			fileRepo.printFilesInfo(request);
			List<MultipartFile> getUploadFiles = null;
			try {
				getUploadFiles = fileRepo.getUploadFiles(request, FILE_INPUT_NAME);
				log.debug("Files:{}", getUploadFiles.size());
			} catch (Exception e) {
				e.printStackTrace();
				return axRet.set(ApiResultError.ATTACH_ERROR, getLocale());
			}

			list = memberService.procAttachFiles(paramDto, getUploadFiles, paramDto.getStrDelFileIds(), true);

		}catch (Exception e){
			e.printStackTrace();
			return axRet.set(ApiResultError.ERROR_DEFAULT, getLocale());
		}

		axRet.setList(list).set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}
}
