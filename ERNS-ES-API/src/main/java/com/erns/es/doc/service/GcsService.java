package com.erns.es.doc.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * <p>GoogleCloudStorage 서비스</p> 
 *
 * @author Hunwoo Park 
 *
 */
@Slf4j
@Service
public class GcsService {
	
	@Value("${google.cloud.storage.project:ocr-project-411607}")
	private String projectId;
	
	@Value("${google.vision.api.credentials.json.path:classpath:/key/google/ocr/ocr-project-411607-21bc092ff347.json}")
	private String credentialsPath;

	@Autowired
	private ResourceLoader resourceLoader;
	
	private ServiceAccountCredentials credentials;
	private Storage storage; // Storage 객체를 재사용하기 위해 클래스 멤버로 선언
	
	@PostConstruct
	public void startup() throws IOException
	{
		log.info("GcsService is started");
		
		// 구글 인증은 기본적으로 키 생성을 할 경우 .json 파일로 로컬에 저장이 된다.
		// .json을 통해 인증을 정보를 등록
		credentials = ServiceAccountCredentials
				.fromStream(resourceLoader.getResource(credentialsPath).getInputStream());
		log.info("Loaded Google ServiceAccountCredentials..");
		
		 // Storage 객체 초기화
        storage = StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();
        log.info("Google Cloud Storage client initialized..");
	}
	
	/**
	 * 파일 업로드
	 * @param bucketName
	 * @param objectName
	 * @param inputStream
	 */
	public void uploadFile(String bucketName, String objectName, InputStream inputStream) {
	    try {
	        // Blob(객체) 정보 설정
	        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName).build();

	        // InputStream을 사용한 GCS 업로드
	        storage.createFrom(blobInfo, inputStream);

	        log.debug("File uploaded to bucket [{}] as {}", bucketName, objectName);            
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * 파일업로드
	 * @param bucketName
	 * @param objectName
	 * @param uploadFilePath
	 */
	public void uploadFile(String bucketName, String objectName, Path uploadFilePath) {
		try {			
            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName).build();

            // GCS에 파일 업로드
            storage.createFrom(blobInfo, uploadFilePath);
            log.debug("File : [{]} uploaded to bucket [{}] as {}", uploadFilePath, bucketName, objectName);            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	/**
	 * 파일업로드
	 * @param bucketName
	 * @param objectName
	 * @param uploadFilePath
	 */
	public void uploadFile(String bucketName, String objectName, String uploadFilePath) {        
		uploadFile(bucketName, objectName, Paths.get(uploadFilePath));
    }
	
	/**
	 * 파일 삭제
	 * @param bucketName
	 * @param objectName
	 */
	public void deleteFile(String bucketName, String objectName) {
	    try {
	        // GCS에서 파일 삭제
	        boolean deleted = storage.delete(bucketName, objectName);

	        if (deleted) {
	            log.debug("File [{}] deleted from bucket [{}]", objectName, bucketName);
	        } else {
	            log.warn("File [{}] not found in bucket [{}]", objectName, bucketName);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * 파일조회
	 * @param bucketName
	 * @param objectName
	 * @return
	 */
	public Blob readFileAsBlob(String bucketName, String objectName) {
	    try {
	        // BlobId 및 Blob 객체 생성
	        BlobId blobId = BlobId.of(bucketName, objectName);
	        Blob blob = storage.get(blobId);

	        if (blob == null) {
	            log.warn("File [{}] not found in bucket [{}]", objectName, bucketName);
	            return null;
	        }

	        return blob;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}

}
