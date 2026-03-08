package com.erns.es.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.taglibs.standard.extra.spath.RelativePath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

import com.erns.es.doc.service.GcsService;
import com.erns.es.doc.service.GoogleVisionApiService;
import com.erns.es.doc.service.TextExtractorService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class TextExtractorServiceTest {

    @Autowired
    private TextExtractorService textExtractorService;
    
    @Autowired
	private GoogleVisionApiService googleVisionApiService;
    
    @Autowired
	private GcsService gcsService;
    
    @Autowired
	private ResourceLoader resourceLoader;

    //@Test
    public void testExtractText() {
    	String[] inputFiles = new String[] {
//				"es_test/image/docx\\231117_NICE4B2B 개인정보이용및수집동의.docx"
//				, "es_test/image/xlsx\\80007859_[주식회사 헥토헬스케어]드시모네 프리미엄 플러스.xlsx"
//				, "es_test/image/pptx\\오토빌IOS_QA_240610.pptx"
//				, "es_test/image/doc\\231117_NICE4B2B 이용약관.doc"
//				, "es_test/image/xls\\자동차안전연구원_수정요청사항정리_221118.xls"
//				, "es_test/image/ppt\\오토빌IOS_QA_240610.ppt"
//				
//				,"es_test/image/pdf\\1. COSMAX 신규 C.I 가이드(24.06.10).pdf"
//				,"es_test/image/hwp\\0. 보안적합성검증-장비현황표.hwp"
//				,"es_test/image/[붙임3] [서식1-3] 비밀유지계약서.hwp"
//				,"es_test/image/화면 캡처 2022-09-26 101429.png"
    			//,
    			"es_test/image/00_logo.jpg"
		};
    	
        
        try {
        	String currentDir = System.getProperty("user.dir");
        	Path runningPath = Paths.get(currentDir);
        	Path resRootPath = runningPath.resolve("src/main/resources/test");
        	log.debug("runningPath:{}", runningPath);
        	log.debug("resRootPath:{}", resRootPath);
        	
        	for(String filePath : inputFiles) {
        		Path targetFile =  resRootPath.resolve(filePath);
        		log.debug("targetFile:{}", targetFile);
                String extractedText = textExtractorService.extractText(targetFile);
                log.debug("extractedText:\\n{}", extractedText);
        	}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //@Test
    public void testExtractTextInPdfUsingGoogleVision() {
		
    	String[] inputFiles = new String[] {
				//"es_test/pdf/80007321_[연세대학교 연세생활건강]07.pdf"
    			"es_test/pdf/20241113_2Page.pdf"
		};
		
		Path refOutputFolder = Paths.get("bin/testOutput");
		
		try {
        	String currentDir = System.getProperty("user.dir");
        	Path runningPath = Paths.get(currentDir);
        	Path outputFolder = runningPath.resolve(refOutputFolder);
        	Path resRootPath = runningPath.resolve("src/main/resources/test");
        	log.debug("runningPath:{}", runningPath);
        	log.debug("resRootPath:{}", resRootPath);
        	log.debug("outputFolder:{}", outputFolder);
        	
        	for(String filePath : inputFiles) {
        		Path sourceFile = resRootPath.resolve(filePath);
        		log.debug("sourceFile:{}", sourceFile);
        		
        		try (FileInputStream inputStream = new FileInputStream(sourceFile.toFile());){
        			googleVisionApiService.extractFromPdf(inputStream);        			
        		}
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }	
	}
    
    @Test
    public void testExtractTextInPdfUsingGoogleVision2() {
		
    	//제지서
//    	String gcsSrcPath = "gs://erns/80007321_[연세대학교 연세생활건강]07.pdf";
//    	String gcsDescPath = "gs://erns/80007321_[연세대학교 연세생활건강]07.json";
    	
    	String[] inputFiles = new String[] {
				"test/es_test/pdf/20241113_2Page.pdf",
				"test/es_test/pdf/80007321_[연세대학교 연세생활건강]07.pdf"
		};		
		final String GCS_BUCKET = "erns";
		
		try {
        	for(String filePath : inputFiles) {
        		Path fPath = Paths.get(filePath);
        		String fileClassPath = String.format("classpath:/%s", fPath);
        		String gscObjecName = fPath.getFileName().toString();
        		
        		log.debug("sourceFile:{}", fileClassPath);
        		
        		//파일 업로드
        		try (InputStream inputStream = resourceLoader.getResource(fileClassPath).getInputStream()){
        			gcsService.uploadFile(GCS_BUCKET, gscObjecName, inputStream);        			
        		}
        		
        		String gcsSrcPath = String.format("gs://%s/%s", GCS_BUCKET, gscObjecName);
        		String gcsDescPath = String.format("gs://%s/%s.json", GCS_BUCKET, StringUtils.stripFilenameExtension(gscObjecName));
        		
        		//구글 Vision 처리
        		try {
        			String extratText = googleVisionApiService.extractFromPdfAsync(gcsSrcPath, gcsDescPath);
        			log.debug(extratText);
        			
                } catch (Exception e) {
                    e.printStackTrace();
                }
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }
    			
	}
}
