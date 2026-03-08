package com.erns.coching.common.util;

import org.springframework.util.StringUtils;

import com.erns.coching.common.model.ApiResult;

/**
 *
 * <p>로그메시지 출력 유틸</p>
 *
 * @author Hunwoo Park
 *
 */
public class LogUtil {

	/**
	 * API 전송결과 로그메시지 포멧팅
	 *
	 * @param apiRet API 전송결과
	 * @return 포멧팅된 로그 메시지
	 */
	public static final String getLogMsg(ApiResult apiRet) {
		return String.format("%s : %s", apiRet.getResultCode(), apiRet.getResultMessage());
	}

	/**
	 * 로그아이디 포멧팅
	 * @param methodName 호출메소드명
	 * @param prefix 접두사
	 * @param suffix 접미사
	 * @return '접두사_호출메소드_접미사' 형태로 로그아이디 반환
	 */
	public static final String getLogId(String methodName, String prefix, String suffix) {
		String retVal = methodName;

		if(StringUtils.hasText(prefix)) {
			retVal = String.format("%s_%s", prefix, retVal);
		}

		if(StringUtils.hasText(suffix)) {
			retVal = String.format("%s_%s", retVal, suffix);
		}

		return retVal;
	}

}
