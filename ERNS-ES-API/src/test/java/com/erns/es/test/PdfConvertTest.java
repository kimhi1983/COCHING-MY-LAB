package com.erns.es.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import com.erns.es.doc.DocToPdfConfig;
import com.erns.es.doc.service.GoogleVisionApiService;
import com.erns.es.doc.service.LibrePdfConvertService;
import com.erns.es.doc.service.PoiPdfConvertService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class PdfConvertTest {
	
	@SuppressWarnings("unused")
	@Autowired
	private PoiPdfConvertService poiPdfConvertService;
	
	@Autowired
	private LibrePdfConvertService librePdfConvertService;
	
	@Autowired
	private GoogleVisionApiService googleVisionApiService;
	
	
	@Test
    public void testToPDF() {
		String[] inputFiles = new String[] {
				"es_test/doc/231117_NICE4B2B 이용약관.doc",
				"es_test/docx/231117_NICE4B2B 구매기업_매출채권양수도 동의.docx",
				"es_test/ppt/230109_Viva_MAGENTA.ppt",
    			"es_test/pptx/230109_Viva_MAGENTA.pptx",
    			"es_test/xls/자동차안전연구원_수정요청사항정리_221118.xls",
    			"es_test/xlsx/80007321_[연세대학교 연세생활건강]07.xlsx",
    			"es_test/hwp/[붙임3] [서식1-3] 비밀유지계약서.hwp",
    			"es_test/hwpx/multipara.hwpx"
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
        		
        		String orgFileName = sourceFile.getFileName().toString();
        		String fileName = StringUtils.stripFilenameExtension(orgFileName);
        		//String fileExt = StringUtils.getFilenameExtension(orgFileName);        		
        		Path outputFile = outputFolder.resolve(String.format("%s.pdf", fileName));
        		
        		librePdfConvertService.mkdirs(outputFolder);
        		
        		DocToPdfConfig dtpCfg = DocToPdfConfig.builder()
        				//.watermarkText("(주) 이알앤에스")
        				.sourceFilePath(sourceFile)
        				.destinationFilePath(outputFile)
        				.build();
        		
        		//poiPdfConvertService.convertToPdf(dtpCfg);
        		librePdfConvertService.convertToPdf(dtpCfg);
        		
                log.debug("outputFile:{}", outputFile);
                
        	}
        } catch (IOException e) {
            e.printStackTrace();
        }		
	}   
    
}
