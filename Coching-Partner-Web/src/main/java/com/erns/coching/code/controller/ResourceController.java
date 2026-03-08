package com.erns.coching.code.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.erns.coching.common.aop.ApiLogging;
import com.erns.coching.common.controller.AbstractApiController;
import com.erns.coching.common.type.UserLogType;
import com.nimbusds.jose.JOSEException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/api/code")
@PreAuthorize("permitAll")
public class ResourceController extends AbstractApiController {
	
	@Autowired
	protected ResourceLoader resourceLoader;
	
	@Value("${system.file.uploadpath}")
	private String uploadPath;
	
	public static final String I18N_FLODER_NAME = "i18n";
	public static final String DATA_FLODER_NAME = "data";
	
	public static final String INGREDIENT_NAMES_FILENAME = "ingdNams.json";
	
	/**
	 * 성분사전
	 * @param request
	 * @param response
	 * @param param
	 * @param errors
	 * @return
	 * @throws JOSEException
	 * @throws IOException 
	 */
	@ApiLogging(logType = UserLogType.SEARCH_INGREDIENTS)
	@GetMapping(value = "/static/ingredient/names.api", produces = MediaType.APPLICATION_JSON_VALUE)	
    public ResponseEntity<InputStreamResource> getIngredientNames(
    		HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		// 캐시 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        //n시간 캐시
        int cacheHour = 12;
        headers.setCacheControl(CacheControl.maxAge(cacheHour, TimeUnit.HOURS).getHeaderValue());
		
		Path rootPath = Paths.get(uploadPath, DATA_FLODER_NAME);
		Path localePath = rootPath.resolve(INGREDIENT_NAMES_FILENAME);
		{	//파일 체크후 문제 없으면 리턴
			File jsonFile = localePath.toFile();
			if(jsonFile.exists()){
				Resource resFile = new FileSystemResource(jsonFile);				
				return ResponseEntity.ok().headers(headers).body(new InputStreamResource(resFile.getInputStream()));				
			}
			log.debug("Cannot found file:{}", jsonFile);
		}
		
		String filePath = String.format("classpath:/api/%s/%s", DATA_FLODER_NAME, INGREDIENT_NAMES_FILENAME);
		log.info("load file:{}", filePath);
		Resource res = resourceLoader.getResource(filePath);
		
		return ResponseEntity.ok().headers(headers).body(new InputStreamResource(res.getInputStream()));
    }
	
	/**
	 * 다국어 코드
	 * @param request
	 * @param response
	 * @param param
	 * @param errors
	 * @return
	 * @throws JOSEException
	 * @throws IOException 
	 */
	@ApiLogging(logType = UserLogType.MLTLN_GET)
	@GetMapping(value = "/i18n/{locale}.api", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> get18nCodeList(
    		HttpServletRequest request, HttpServletResponse response,
    		@PathVariable String locale) throws IOException {
		
		// 캐시 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        //n시간 캐시
        int cacheHour = 12;
        headers.setCacheControl(CacheControl.maxAge(cacheHour, TimeUnit.HOURS).getHeaderValue());
		
		Path rootPath = Paths.get(uploadPath, I18N_FLODER_NAME);
		Path localePath = rootPath.resolve(String.format("%s.json", locale));
		{	//파일 체크후 문제 없으면 리턴
			File jsonFile = localePath.toFile();
			if(jsonFile.exists()){
				Resource resFile = new FileSystemResource(jsonFile);				
				return ResponseEntity.ok().headers(headers).body(new InputStreamResource(resFile.getInputStream()));				
			}
			log.debug("Cannot found file:{}", jsonFile);
		}
		
		String filePath = String.format("classpath:/api/%s/%s.json", I18N_FLODER_NAME, locale);
		log.info("load file:{}", filePath);
		Resource res = resourceLoader.getResource(filePath);
		
		return ResponseEntity.ok().headers(headers).body(new InputStreamResource(res.getInputStream()));
    }

}
