package com.erns.es.doc.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;

import com.erns.es.doc.DocToPdfConfig;
import com.erns.es.doc.ThrowingFunction;
import com.erns.es.doc.type.SupportDocType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractPdfConvertService {
	
	@Value("${pdf.converter.watermark.font:classpath:/font/NaverNanumSquareNeo/NanumSquareNeo-bRg.ttf}")
	protected String watermarkFontPath;
	
	@Autowired
	protected ResourceLoader resourceLoader;
	
	//확장자별로 변환방식을 설정
	protected Map<SupportDocType, Function<DocToPdfConfig, Boolean>> converterMap = new HashMap<>();
	
	public void startup() throws IOException
	{
		log.info("AbstractPdfConvertService is started");
	}
	
	
	abstract public boolean convertToPdf(DocToPdfConfig convertConfig) throws IOException;
	
	
	protected <T> Function<T, Boolean> wrapFunction(ThrowingFunction<T, Boolean> function) {
		return (t) -> {
			try {
				return function.apply(t);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		};
	}
	
	/**
	 * 경로 만들기
	 * @param path
	 */
	public void mkdirs(Path path) {	
		File temp = path.toFile();
		if(! temp.exists()){
			temp.mkdirs();
		}
	}

}
