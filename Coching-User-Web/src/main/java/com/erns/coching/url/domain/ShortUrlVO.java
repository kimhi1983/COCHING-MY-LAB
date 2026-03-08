package com.erns.coching.url.domain;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.erns.coching.common.Const;
import com.erns.coching.common.type.ShortUrlType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 
 * <p>단축 URL </p> 
 *
 * @author Hunwoo Park 
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class ShortUrlVO {

	protected String urlId; //uuid + 임의값
	protected String url;	//실제 URL
	protected String method;//전달방식 GET/POST
	protected String paramJson;	//파라미터 json
	protected String token;	//검증토큰

	protected long rgtr;	//등록자
	protected Date rgtDttm;	//등록일
	
	protected long hits;	//접근 클릭수
	protected Date lastDttm;//최근사용일 
	
	public String getRequestUrl() {
		return getRequestUrl(this);
	}
	
	public static String getRequestUrl(ShortUrlVO vo) {		
		if(!StringUtils.hasText(vo.token)) {
			return String.format("%s/%s", Const.SHORT_URL_BASE_URI, vo.urlId);
		}
		
		try {
			return String.format("%s/%s?tk=%s", Const.SHORT_URL_BASE_URI, vo.urlId, URLEncoder.encode(vo.token, "UTF_8"));
		} catch (UnsupportedEncodingException e) {
			return String.format("%s/%s?tk=%s", Const.SHORT_URL_BASE_URI, vo.urlId, vo.token);
		}
	}
	
	/**
	 * 주어진 입력값으로 부터 ShortUrl을 생성한다.
	 * @param seq
	 * @param lastAccessIp
	 * @throws JsonProcessingException 
	 */
	@Builder(builderClassName = "ShortUrlParamBuilder", builderMethodName = "ShortUrlParamBuilder")
	public ShortUrlVO(String url
			, Map<String, Object> parameters
			, String method
			, String token 
			, long rgtr) {
	    Assert.notNull(url, "url must not be null");
	    
	    this.urlId = generateId();	    
	    this.url = url;
	    this.method = StringUtils.hasText(method) ? method : "GET";
	    this.token = token;
	    
	    this.paramJson = "{}";
	    if(parameters != null && parameters.size() > 0) {
	    	try {
	    		ObjectMapper objectMapper = new ObjectMapper();
				this.paramJson = objectMapper.writeValueAsString(parameters);
			} catch (JsonProcessingException e) {
				this.paramJson = "{}";
			}
	    }	    
	    this.rgtr = rgtr <= 0 ? 1 : rgtr; //대부분 system 
	}

	/**
	 * 주어진 입력값으로 부터 ShortUrl을 생성한다.
	 * @param seq
	 * @param lastAccessIp
	 * @throws JsonProcessingException 
	 */
	@Builder(builderClassName = "ShortUrlTypeBuilder", builderMethodName = "ShortUrlTypeBuilder")
	public ShortUrlVO(ShortUrlType urlType
			, Map<String, Object> parameters
			, String token 
			, long rgtr) {
	    Assert.notNull(urlType, "urlType must not be null");
	    
	    this.urlId = generateId();
	    this.url = urlType.getTargetUrl();
	    this.method = urlType.getMethod();
	    this.token = token;
	    
	    this.paramJson = "{}";
	    if(parameters != null && parameters.size() > 0) {
	    	try {
	    		ObjectMapper objectMapper = new ObjectMapper();
				this.paramJson = objectMapper.writeValueAsString(parameters);
			} catch (JsonProcessingException e) {
				this.paramJson = "{}";
			}
	    }	    
	    this.rgtr = rgtr <= 0 ? 1 : rgtr; //대부분 system 
	}
	
	protected String generateId() {		
		String uuid = UUID.randomUUID().toString();
		
		//System.out.println("uuid : " + uuid);
		
		String[] splits = uuid.split("-");
		String[] rejoins = new String[splits.length];
		rejoins[0] = splits[4];
		rejoins[1] = splits[2];
		rejoins[2] = splits[3];
		rejoins[3] = splits[1];
		rejoins[4] = splits[0];
		
		String retVal = String.join("", rejoins);		
		return retVal;
	}
	
	public static void main(String[] args) throws Exception {
		ShortUrlVO susi = new ShortUrlVO();
		System.out.println("test id : "+susi.generateId());
		
		
		ShortUrlVO test1 = ShortUrlVO.ShortUrlParamBuilder()
				.url("abc.com")
				.build();
		System.out.println("test1 : "+test1);
		
		Map<String, Object> param2 = new HashMap<>();
		param2.put("userId", "erns");
		param2.put("userSeq", 1);
		ShortUrlVO test2 = ShortUrlVO.ShortUrlParamBuilder()
				.url("http://abc1.com?abc=11&ddd=bacd")
				.parameters(param2)
				.rgtr(22)
				.build();
		System.out.println("test2 : "+test2);
		
		Map<String, Object> param3 = new HashMap<>();
		param3.put("userId", "erns");
		param3.put("userSeq", 1);
		ShortUrlVO test3 = ShortUrlVO.ShortUrlParamBuilder()
				.url("abc3.com")
				.parameters(param3)
				.build();
		System.out.println("test3 : "+test3);
	}
}
