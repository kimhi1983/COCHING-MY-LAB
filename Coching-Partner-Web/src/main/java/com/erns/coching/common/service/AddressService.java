package com.erns.coching.common.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.json.parser.ParseException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AddressService {

	@Value("${address.confmkey}")
	private String confmKey;

	/**주소 검색 결과 가져오기**/
	public Map<String,Object> getSearchResult(String keyword, int currentPage, int countPerPage) throws ParseException, UnsupportedEncodingException, IOException {

		String resultType = "json";

		int cp = currentPage <= 0 ? 1 : currentPage;
		int cpp = countPerPage <= 0 ? 10 : countPerPage;
		String eKeyword = URLEncoder.encode(keyword, "UTF-8");

		String apiUrl =
			UriComponentsBuilder.fromHttpUrl("https://www.juso.go.kr/addrlink/addrLinkApi.do")
			.queryParam("confmKey", confmKey)
			.queryParam("resultType", resultType)
			.queryParam("keyword", eKeyword)			//URLEncoder.encode(keyword,"UTF-8")
			.queryParam("currentPage", cp)
			.queryParam("countPerPage", cpp)
			.build()
			.toUriString();

		log.debug("url:{}", apiUrl);

		ObjectMapper mapper = new ObjectMapper();
		Map<String,Object> result = mapper.readValue(new URL(apiUrl), new TypeReference<Map<String, Object>>() {});
    	log.debug("result:{}", result);

    	return result;
	}
}

