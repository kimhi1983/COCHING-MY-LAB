package com.erns.es.common.aop;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.erns.es.common.model.ApiResult;
import com.erns.es.common.model.IReqDto;
import com.erns.es.common.type.ApiResultError;
import com.erns.es.log.domain.ApiProcLogVO;
import com.erns.es.log.service.ApiProcLogService;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * <p>Log AOP</p>
 *
 * @author Hunwoo Park
 *
 */
@Component
@Aspect
@Slf4j
public class ApiLoggingAspect {

	@Autowired
	protected ApiProcLogService apiProcLogService;

	protected static final ObjectMapper objectMapper = new ObjectMapper();

    @Before("@annotation(ApiLogging)")
    public void beforeApiLogging(JoinPoint jp) {
		Object[] params = jp.getArgs();

		ApiProcLogVO logVo = generateReqLogProc(params);

		if(null != logVo){
			log.debug("ApiLogVo:{}", logVo);
			addLog(logVo);
		}
    }

	@Around("@annotation(ApiLogging)")
	public Object logPerf(ProceedingJoinPoint pjp) throws Throwable{
		long begin = System.currentTimeMillis();

		Object apiResult = null;
		try {
			apiResult = pjp.proceed(); // 메서드 호출 자체를 감쌈
		}catch(Exception e) {
			log.error("API Error", e);
			apiResult = new ApiResult(ApiResultError.ERROR_CALL_API);
		}

		log.debug("API Execute: {} ms", System.currentTimeMillis() - begin);

		{	//logging
			ApiResult apiRet = (apiResult instanceof ApiResult) ? (ApiResult) apiResult : new ApiResult(ApiResultError.ERROR_CALL_API);
			ApiProcLogVO logVo = generateResLogProc(pjp.getArgs(), apiRet);

			if(null != logVo){
				log.debug("ApiLogVo:{}", logVo);
				addLog(logVo);
			}
		}

		return apiResult;
	}

//	@AfterReturning(value = "@annotation(ApiLogging)", returning = "apiResult")
//	public void afterSuccessLog(JoinPoint jp, ApiResult apiResult) throws RuntimeException {
//	    //logging
//
//	}
//
//	@AfterThrowing(value = "@annotation(ApiLogging)", throwing = "e")
//	public void afterFailLog(JoinPoint jp, Exception e) throws RuntimeException {
//	    //logging
//	    //exception 으로 해당 메서드에서 발생한 예외를 가져올 수 있다.
//		log.error("API Error", e);
//	}

	/**
	 * Request 로그 생성
	 * @param jpArgs
	 * @return
	 */
	private ApiProcLogVO generateReqLogProc(Object[] jpArgs) {
		if(jpArgs == null || jpArgs.length <=0) {
			return null;
		}

		ApiProcLogVO reqLog = generateLogProc(jpArgs, null);
		reqLog.setProcStatus("0"); //요청

		return reqLog;
	}

	/**
	 * Response 로그 생성
	 * @param jpArgs
	 * @param apiRet
	 * @return
	 */
	private ApiProcLogVO generateResLogProc(Object[] jpArgs, ApiResult apiRet) {
		if(jpArgs == null || jpArgs.length <=0) {
			return null;
		}

		ApiProcLogVO reqLog = generateLogProc(jpArgs, apiRet);
		reqLog.setProcStatus("1"); //응답

		return reqLog;
	}


	@SuppressWarnings("rawtypes")
	private ApiProcLogVO generateLogProc(Object[] params, ApiResult apiRet) {
		ApiProcLogVO logVo = null;

		MultiValueMap apiParam = null;
		for(Object p : params) {
			//logger.debug("ApiLoggingAspect: {}", p.getClass().toString());

			if(p instanceof HttpServletRequest) {
				HttpServletRequest req = (HttpServletRequest)p;
				//logger.debug("API URL:{}", req.getRequestURI());
				if(apiRet != null) {
					logVo = ApiProcLogVO.getResLog(req, apiRet);
				}else {
					logVo = ApiProcLogVO.getReqLog(req);
				}

				continue;
			}

			if(p instanceof IReqDto) {
				log.debug("ReqDto:{}", p.toString());
				try {
					apiParam = convert(objectMapper, p);
				}catch(Exception e){
					log.debug(e.getMessage());
					apiParam = null;
				}

				continue;
			}

			if(p instanceof MultiValueMap) {
				log.debug("Param:{}", p.toString());
				if(apiParam != null) {
					apiParam = (MultiValueMap) p;
				}
				continue;
			}
		}

		if(logVo != null && apiParam != null) {
			logVo.appendParam(apiParam);
		}

		return logVo;
	}

	/**
     * 로그 기록
     * @param logVo
     */
	private final void addLog(ApiProcLogVO logVo) {

		if(logVo == null) {
			return;
		}

		try {
			apiProcLogService.addLog(logVo);
		}catch (Exception e) {
    		log.error("AddLogError", e);
		}
	}

	private MultiValueMap<String, String> convert(ObjectMapper objectMapper, Object dto) { // (2)
        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            Map<String, String> map = objectMapper.convertValue(dto, new TypeReference<Map<String, String>>() {}); // (3)
            params.setAll(map); // (4)

            return params;
        } catch (Exception e) {
        	//log.error("Parameter 변환중 오류가 발생했습니다. requestDto={}", dto, e);
            throw new IllegalStateException("Parameter 변환중 오류가 발생했습니다.");
        }
    }

}
