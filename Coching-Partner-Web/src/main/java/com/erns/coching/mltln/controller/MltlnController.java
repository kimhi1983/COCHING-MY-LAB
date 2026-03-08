package com.erns.coching.mltln.controller;

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

import lombok.extern.slf4j.Slf4j;

/**
*
* <p>Mltln API Controller</p>
*
* @author Hunwoo Park
*
*/
@Slf4j
@Controller
@RequestMapping("/api/mltln")
@PreAuthorize("permitAll")
public class MltlnController extends AbstractApiController {

	@Autowired
	protected ResourceLoader resourceLoader;

	@Value("${system.file.uploadpath}")
	private String uploadPath;

	/**
	 * 다국어 Json
	 * @param request
	 * @param response
	 * @param locale
	 * @return
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
        int cacheHour = 4;
        headers.setCacheControl(CacheControl.maxAge(cacheHour, TimeUnit.HOURS).getHeaderValue());

		Path rootPath = Paths.get(uploadPath, "i18n");
		Path localePath = rootPath.resolve(String.format("%s.json", locale));
		{	//파일 체크후 문제 없으면 리턴
			File jsonFile = localePath.toFile();
			if(jsonFile.exists()){
				Resource resFile = new FileSystemResource(jsonFile);
				return ResponseEntity.ok().headers(headers).body(new InputStreamResource(resFile.getInputStream()));
	        }
			log.debug("Cannot found file:{}", jsonFile);
		}

		String filePath = String.format("classpath:/api/i18n/%s.json", locale);
		log.info("load file:{}", filePath);
		Resource res = resourceLoader.getResource(filePath);
		
		return ResponseEntity.ok().headers(headers).body(new InputStreamResource(res.getInputStream()));
    }

}
