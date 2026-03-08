package com.erns.coching.log.domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.erns.coching.common.log.domain.IApiProcLogVO;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * <p>
 * T_API_LOG_H 로그 VO
 * </p>
 *
 * @author Hunwoo Park
 *
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@Slf4j
public class ApiLogVO implements IApiProcLogVO {

	protected static final ObjectMapper objectMapper = new ObjectMapper();
	protected static final SimpleDateFormat LOGDATE_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private Date rgtDttm;

	private String siteType;

	private long membSeq;

	private String logType;

	private String routerNm;

	private String accessIp;

	private String procStatus;

	private String apiUri;

	private String apiParams;

	private String apiRes;

	private Map<String, Object> paramMap;

	private String tempKey;

	@Override
	public void setResData(Map<String, Object> resDataMap) {
		try {
			this.apiRes = objectMapper.writeValueAsString(resDataMap);
		} catch (Exception e) {
			log.error("ResponseData 변환중 오류가 발생했습니다. resDataMap={}", resDataMap, e);
			// throw new IllegalStateException("ResponseData 변환중 오류가 발생했습니다.");
			this.apiRes = "{}";
		}
	}

	@Override
	public void setParamMap(Map<String, Object> paramMap) {
		this.paramMap = paramMap;

		try {
			this.apiParams = objectMapper.writeValueAsString(paramMap);
		} catch (Exception e) {
			log.error("paramMap 변환중 오류가 발생했습니다. paramMap={}", paramMap, e);
			// throw new IllegalStateException("ResponseData 변환중 오류가 발생했습니다.");
			this.apiParams = "{}";
		}
	}
}
