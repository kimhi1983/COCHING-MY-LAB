package com.erns.coching.common.util;



import java.util.Map;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * <p>MultiValueMap 컨버터</p>
 *
 * @author Hunwoo Park
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public abstract class MultiValueMapConverter {

	/**
	 * <p>
	 * DTO 모델을 MultiValueMap<String, String> 형태로 반환 한다.<br/>
	 * 주로 RestTemplate에서 파라미터 전달시 사용<br/>
	 * Object -&gt; JSON -&gt; MultiValueMap 순서로 변환<br/>
	 * </p>
	 *
	 * @param objectMapper
	 * @param dto 변환할 Object
	 * @return
	 */
    public static MultiValueMap<String, String> convert(ObjectMapper objectMapper, Object dto) { // (2)
        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            Map<String, String> map = objectMapper.convertValue(dto, new TypeReference<Map<String, String>>() {}); // (3)
            params.setAll(map); // (4)

            return params;
        } catch (Exception e) {
        	log.error("Url Parameter 변환중 오류가 발생했습니다. requestDto={}", dto, e);
            throw new IllegalStateException("Url Parameter 변환중 오류가 발생했습니다.");
        }
    }
}
