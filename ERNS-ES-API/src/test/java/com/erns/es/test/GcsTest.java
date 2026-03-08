package com.erns.es.test;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import com.erns.es.doc.service.GcsService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class GcsTest {
	
	@Autowired
	private GcsService gcsService;
	

	//@Test
    public void testUplodFile() {
		String[] inputFiles = new String[] {
				"es_test/doc/231117_NICE4B2B 이용약관.doc"
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
        		
        		gcsService.uploadFile("erns", orgFileName, sourceFile.toString());
                
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }		
	}
	
	@Test
    public void testDeleteFile() {
		try {
			
			String objectName = "231117_NICE4B2B 이용약관.doc";
					
			gcsService.deleteFile("erns", objectName);
        	
        } catch (Exception e) {
            e.printStackTrace();
        }		
	}
}
