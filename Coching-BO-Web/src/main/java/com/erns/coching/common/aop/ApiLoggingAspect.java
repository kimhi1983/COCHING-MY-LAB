package com.erns.coching.common.aop;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import com.erns.coching.common.log.domain.IApiProcLogVO;
import com.erns.coching.common.log.service.ApiLogService;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.model.IReqDto;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserLogType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * <p>
 * Log AOP
 * </p>
 *
 * @author Hunwoo Park
 *
 */
@Component
@Aspect
@Slf4j
public class ApiLoggingAspect {

	@Autowired
	protected ApiLogService apiProcLogService;

	protected static final ObjectMapper objectMapper = new ObjectMapper();

	// @Before("@annotation(ApiLogging)")
	// public void beforeApiLogging(JoinPoint jp) {
	// Object[] params = jp.getArgs();

	// ApiLogVO logVo = generateReqLogProc(params);

	// if (null != logVo) {
	// log.debug("ApiLogVo:{}", logVo);
	// addLog(logVo);
	// }
	// }

	@Around("@annotation(ApiLogging)")
	public Object aroundApiLogging(ProceedingJoinPoint pjp, ApiLogging ApiLogging) throws Throwable {
		long begin = System.currentTimeMillis();
		String action = ApiLogging.action(); // 어노테이션에서 넘어온 value
		UserLogType logType = ApiLogging.logType();

		// Object[] params = pjp.getArgs();
		Object apiResult = null;
		IApiProcLogVO logVo = null;
		try {
			apiResult = pjp.proceed(); // 메서드 호출 자체를 감쌈
		} catch (Exception e) {
			//

			logVo = generateLogProc(pjp.getArgs(), e);

			log.error("API Error", e);
			apiResult = new ApiResult(ApiResultError.ERROR_CALL_API);
		}
		log.debug("API Execute: {} ms", System.currentTimeMillis() - begin);

		if (null == logVo) {
			if(null == apiResult){
				//Response By-Pass 등은 return type 없이 void 반환 한다.
				//이때는 성공처리로 한다
				logVo = generateResLogProc(pjp.getArgs(), new ApiResult(ApiResultError.NO_ERROR));
			}else{
				// logging
				ApiResult apiRet = (apiResult instanceof ApiResult) 
					? (ApiResult) apiResult
					: new ApiResult(ApiResultError.ERROR_CALL_API);	
					logVo = generateResLogProc(pjp.getArgs(), apiRet);
			}
		}

		logVo.setLogType(logType.getCode());
		addLog(logVo);
		log.debug("ApiLogVo:{}", logVo);

		if ("updateLast".equals(action)) {
			apiProcLogService.addLastLog(logVo);
		}

		return apiResult;
	}

	// @AfterReturning(value = "@annotation(ApiLogging)", returning = "apiResult")
	// public void afterSuccessLog(JoinPoint jp, ApiResult apiResult) throws
	// RuntimeException {
	// //logging
	//
	// }
	//
	// @AfterThrowing(value = "@annotation(ApiLogging)", throwing = "e")
	// public void afterFailLog(JoinPoint jp, Exception e) throws RuntimeException {
	// //logging
	// //exception 으로 해당 메서드에서 발생한 예외를 가져올 수 있다.
	// log.error("API Error", e);
	// }

	/**
	 * Response 로그 생성
	 * 
	 * @param jpArgs
	 * @param apiRet
	 * @return
	 */
	private IApiProcLogVO generateResLogProc(Object[] jpArgs, ApiResult apiRet) {
		if (jpArgs == null || jpArgs.length <= 0) {
			return null;
		}

		IApiProcLogVO reqLog = generateLogProc(jpArgs, apiRet);

		String resultCode = apiRet.getResultCode();
		if (ApiResultError.NO_ERROR.getCode().equals(resultCode)) {
			reqLog.setProcStatus("1"); // 응답
		} else {
			reqLog.setProcStatus("E"); // 오류
			Map<String, Object> errorResData = new HashMap<>();
			errorResData.put("resultCode", resultCode);
			errorResData.put("message", apiRet.getResultMessage());
			reqLog.setResData(errorResData);
		}

		return reqLog;
	}

	private HttpServletRequest getRequest(Object[] pjpArgs) {
		for (Object p : pjpArgs) {
			// logger.debug("ApiLoggingAspect: {}", p.getClass().toString());

			if (p instanceof HttpServletRequest) {
				HttpServletRequest req = (HttpServletRequest) p;
				return req;
			}
		}

		return null;
	}

	/**
	 * 오류 로그 작성
	 * @param pjpArgs
	 * @param e
	 * @return
	 */
	private IApiProcLogVO generateLogProc(Object[] pjpArgs, Exception e) {
		HttpServletRequest req = getRequest(pjpArgs);
		if (null == req) {
			return null;
		}

		IApiProcLogVO logVo = apiProcLogService.generateErrorLog(req, e);
		apiProcLogService.setRequestInfo(req, logVo);

		logVo.setProcStatus("E"); // 오류
		return logVo;
	}

	/**
	 * 실행 후 로그 생성
	 * @param pjpArgs
	 * @param apiRet
	 * @return
	 */
	private IApiProcLogVO generateLogProc(Object[] pjpArgs, ApiResult apiRet) {
		HttpServletRequest req = getRequest(pjpArgs);
		if (null == req) {
			return null;
		}

		IApiProcLogVO logVo = null;
		if (apiRet != null) {
			logVo = apiProcLogService.generateResLog(req, apiRet);
		} else {
			logVo = apiProcLogService.generateReqLog(req);
		}

		//로그 파라미터 처리
		Map<String, Object> apiParam = new HashMap<>();
		for (Object p : pjpArgs) {
			// if(null != null){
			// 	log.debug("ApiLoggingAspect: {}", p.getClass().toString());
			// }

			if (p instanceof HttpServletRequest) {
				continue;
			}
			
			if (p instanceof IReqDto) {
				log.debug("IReqDto:{}", p.toString());
				try {
					Map<String, Object> dtoParam = convert(objectMapper, p);
					apiParam.putAll(dtoParam);
				} catch (Exception e) {
					log.debug(e.getMessage());
				}
				continue;
			}

			if (p instanceof MultiValueMap) {
				log.debug("Param:{}", p.toString());
				try {
					Map<String, Object> mvParam = convert(objectMapper, p);
					apiParam.putAll(mvParam);
				} catch (Exception e) {
					log.debug(e.getMessage());
				}
				continue;
			}
		}

		logVo.setParamMap(apiParam);

		//파라미터 이외의 값 설정
		apiProcLogService.setRequestInfo(req, logVo);

		return logVo;
	}

	/**
	 * 로그 기록
	 * 
	 * @param logVo
	 */
	private final void addLog(IApiProcLogVO logVo) {
		if (logVo == null) {
			return;
		}

		try {
			apiProcLogService.addLog(logVo);
		} catch (Exception e) {
			log.error("AddLogError", e);
		}
	}

	private Map<String, Object> convert(ObjectMapper objectMapper, Object dto) { // (2)
		try {
			Map<String, Object> params = objectMapper.convertValue(dto, new TypeReference<Map<String, Object>>() {
			});
			return params;

		} catch (Exception e) {
			// log.error("Parameter 변환중 오류가 발생했습니다. requestDto={}", dto, e);
			throw new IllegalStateException("Parameter 변환중 오류가 발생했습니다.");
		}
	}

}
