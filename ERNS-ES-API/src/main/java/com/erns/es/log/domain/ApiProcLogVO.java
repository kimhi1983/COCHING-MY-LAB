package com.erns.es.log.domain;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import com.erns.es.common.Const;
import com.erns.es.common.model.ApiResult;
import com.erns.es.common.type.ApiResultError;
import com.erns.es.common.util.LogUtil;
import com.erns.es.login.domain.LoginUserDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * <p>TB_SC_API_PROC 로그 VO</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
//@Slf4j
public class ApiProcLogVO {

    private long apiLogSeq;
    
    private long membSeq;
	
	private String queryStr;	//[uri]?[queryString]
	
	private String procStatus; 	//요청:0, 응답:1, 에러:E
	
	private String procMsg;
	
	private Date rgtDttm;
	
	private String procType;		
	
	public static ApiProcLogVO getReqLog(HttpServletRequest request) {
		
		ApiProcLogVO retVal = getBase(request);		
		retVal.procStatus = "0";//요청
		
		return retVal;
	}
	
	public static ApiProcLogVO getResLog(HttpServletRequest request, ApiResult apiRet) {
		
		ApiProcLogVO retVal = getBase(request);		
		
		boolean isSuccess = ApiResultError.NO_ERROR.getCode().equals(apiRet.getResultCode());

		//요청:0, 응답:1, 에러:E
		retVal.procStatus = isSuccess ? "1" : "E";
		retVal.procMsg = LogUtil.getLogMsg(apiRet);
		
		return retVal;
	}
	
	private static ApiProcLogVO getBase(HttpServletRequest request) {
		String uri = request.getRequestURI();
		
//		HttpSession session = request.getSession();
		
		//로그인 정보 추출
		LoginUserDTO loginInfo = getLoginedUserObject();
		long userSeq = loginInfo == null ? 0 : loginInfo.getSeq();
		
		
		//요청 파라미터로 query string 생성
		Map<String, String[]> paramMap = request.getParameterMap();
		
		StringBuffer sb = new StringBuffer();
		StringBuffer qnSb = new StringBuffer();
		sb.append("[").append(Const.APP_PREFIX).append("]");
		sb.append(uri+"?");	
		for(String key : paramMap.keySet()) {
			String[] vals = paramMap.get(key);
			String val = vals == null ? "" : String.join(",", vals);
			
			if(!StringUtils.hasText(val)) {
				qnSb.append(key).append("=").append(val).append("&");
				continue;
			}
			
			sb.append(key).append("=").append(val).append("&");
		}
		
		//TODO : Token 정보등 필요시 추가
		
		sb.append(qnSb); //value 없는 부분 맨 뒤로	
		sb.delete(sb.length()-1, sb.length()); //& 제거
		String qStr = sb.toString();
		qStr = qStr.replaceAll("\\s", ""); //화이트 스페이스 제거
		if(qStr.length() > 500) {
			qStr = qStr.substring(0, 500); //최대 500자
		}
		
		//LogVo 생성 후 리턴
		ApiProcLogVO baseVo = ApiProcLogVO.builder()
			.queryStr(qStr)
			.membSeq(userSeq)
			.procType("BAPI_LOG_PROC")
			.build();
		
		return baseVo;
	}
	
	public void appendParam(@SuppressWarnings("rawtypes") MultiValueMap apiParam) {
		if(!StringUtils.hasText(queryStr)) {
			return;
		}
		
		StringBuffer sb = new StringBuffer();
		StringBuffer qnSb = new StringBuffer();
		
		for(Object objKey : apiParam.keySet()) {
			if(!(objKey instanceof String)) {
				continue;
			}
			String key = objKey.toString();
			Object vals = apiParam.get(key);
			String val = "";
			if(vals != null) {
				String strVals = vals.toString();
				strVals = strVals.replaceFirst("^\\[", "");
				strVals = strVals.replaceAll("\\]$", "");
				if("null".equals(strVals)) {
					val = "";
				}else {
					val = strVals.toString();
				}
			}
			
			if(!StringUtils.hasText(val)) {
				qnSb.append(key).append("=").append(val).append("&");
				continue;
			}
			
			sb.append(key).append("=").append(val).append("&");
		}
		
		sb.append(qnSb); //value 없는 부분 맨 뒤로	
		sb.delete(sb.length()-1, sb.length()); //& 제거
		
		if(queryStr.indexOf("?") > 0) {
			queryStr += "&" + sb.toString();
		}else {
			queryStr += "?" + sb.toString();
		}
		queryStr = queryStr.replaceAll("\\s", ""); //화이트 스페이스 제거
		if(queryStr.length() > 500) {
			queryStr = queryStr.substring(0, 500); //최대 500자
		}
		
	}
	
	/**
     * 로그인 정보를 얻는다.
     * @return
     */
	protected static LoginUserDTO getLoginedUserObject() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if(authentication == null) {
			return null;
		}
		
		Object userInfo = authentication.getPrincipal();
		if(userInfo instanceof LoginUserDTO) {
			return (LoginUserDTO)userInfo;
		}
		return null;
    }
	
	public Date getRgtDttm() {
		return rgtDttm == null ? null : new Date(rgtDttm.getTime());
	}
    
	public static String[] asStrings(Object... objArray) {
	    String[] strArray = new String[objArray.length];
	    for (int i = 0; i < objArray.length; i++)
	        strArray[i] = String.valueOf(objArray[i]);
	    return strArray;
	}
}
