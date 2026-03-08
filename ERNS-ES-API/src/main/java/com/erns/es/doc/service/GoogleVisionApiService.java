package com.erns.es.doc.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.vision.v1.AnnotateFileRequest;
import com.google.cloud.vision.v1.AnnotateFileResponse;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.AsyncAnnotateFileRequest;
import com.google.cloud.vision.v1.AsyncBatchAnnotateFilesResponse;
import com.google.cloud.vision.v1.BatchAnnotateFilesResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.GcsDestination;
import com.google.cloud.vision.v1.GcsSource;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import com.google.cloud.vision.v1.InputConfig;
import com.google.cloud.vision.v1.OperationMetadata;
import com.google.cloud.vision.v1.OutputConfig;
import com.google.protobuf.ByteString;
import com.google.protobuf.util.JsonFormat;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GoogleVisionApiService {

	@Value("${google.vision.api.credentials.json.path:classpath:/key/google/ocr/ocr-project-411607-21bc092ff347.json}")
	private String credentialsPath;

	@Autowired
	private ResourceLoader resourceLoader;
	
	private ServiceAccountCredentials credentials;
	
	@PostConstruct
	public void startup() throws IOException
	{
		log.info("GoogleVisionApiService is started");
		
		// 구글 인증은 기본적으로 키 생성을 할 경우 .json 파일로 로컬에 저장이 된다.
		// .json을 통해 인증을 정보를 등록
		credentials = ServiceAccountCredentials
				.fromStream(resourceLoader.getResource(credentialsPath).getInputStream());
		log.info("Loaded Google ServiceAccountCredentials..");
	}
	

	/**
	 * Google Vision API 를 사용하여, 이미지 내의 Text를 감지 추출한다.
	 * @param srcIs
	 * @return
	 * @throws IOException 
	 */
	public String extractFromImage(InputStream srcIs) throws IOException {

		List<AnnotateImageRequest> requests = new ArrayList<>();

		try (InputStream inputStream = srcIs) {
			ByteString imgBytes = ByteString.readFrom(inputStream); // ocr 할 이미지의 ByteString

			Image img = Image.newBuilder().setContent(imgBytes).build();
			Feature feat = Feature.newBuilder().setType(Type.TEXT_DETECTION).build();
			AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img)
					.build();
			requests.add(request);

			try (ImageAnnotatorClient vision = ImageAnnotatorClient.create(
					ImageAnnotatorSettings.newBuilder()
						.setCredentialsProvider(FixedCredentialsProvider.create(credentials))
						.build())
			) {
				// 인증값을 통해 Provider를 빌드한 후 결과를 받는다.
				BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
				List<AnnotateImageResponse> responses = response.getResponsesList();

				StringBuilder stringBuilder = new StringBuilder();
				for (AnnotateImageResponse res : responses) {
					if (res.hasError()) {
						log.error("Error: {}\n", res.getError().getMessage());
						return null;
					}

					stringBuilder.append(res.getTextAnnotationsList().get(0).getDescription());
				}

				return stringBuilder.toString(); // 모든 detected 된 문자열을 append 한 후 리턴
			}
		}
	}
	
	/**
	 * [실패]
	 * @param srcIs
	 * @return
	 * @throws Exception
	 */
	public String extractFromPdf(InputStream srcIs) throws Exception {
		
		try {
			ByteString pdfBytes = ByteString.readFrom(srcIs);

			InputConfig inputConfig = InputConfig.newBuilder().setMimeType("application/pdf").setContent(pdfBytes)
					.build();

			Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();

			AnnotateFileRequest fileRequest = AnnotateFileRequest.newBuilder().addFeatures(feat)
					.setInputConfig(inputConfig).addAllPages(Collections.singletonList(0)) // 첫 번째 페이지를 처리합니다.
					.build();

			List<AnnotateFileRequest> requests = Collections.singletonList(fileRequest);

			try (ImageAnnotatorClient vision = ImageAnnotatorClient.create(ImageAnnotatorSettings.newBuilder()
					.setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build())) {

				BatchAnnotateFilesResponse response = vision.batchAnnotateFiles(requests);
				List<AnnotateFileResponse> fileResponses = response.getResponsesList();

				StringBuilder stringBuilder = new StringBuilder();
				for (AnnotateFileResponse fileRes : fileResponses) {
					for (AnnotateImageResponse res : fileRes.getResponsesList()) {
						if (res.hasError()) {
							System.err.println("Error: " + res.getError().getMessage());
							return null;
						}
						stringBuilder.append(res.getFullTextAnnotation().getText());
					}
				}
				return stringBuilder.toString();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();			
			return null;
		}
	}
	
	
	/**
	 * Google Vision API 를 이용하여 PDF 파일에서 텍스트를 비동기 방식으로 추출한다.
	 * 반드시 GCS 에 파일을 업로드 한 뒤에 호출해야함 
	 * @param gcsSrcUri 소스 파일의 GCS 경로 ex)gs://erns/파일명.pdf
	 * @param gcsDestUri 결과 파일의 GCS 경로 ex)gs://erns/파일명.json
	 * @return 추출된 텍스트
	 */
	public String extractFromPdfAsync(String gcsSrcUri, String gcsDestUri) throws Exception {		
	    // 요청을 보내기 위해 사용할 클라이언트를 초기화합니다. 이 클라이언트는 한 번만 생성하여
	    // 여러 요청에 재사용할 수 있습니다. 모든 요청이 완료된 후에는 클라이언트의 "close" 메서드를 호출하여
	    // 남아 있는 백그라운드 리소스를 안전하게 정리합니다.		
		List<AsyncAnnotateFileRequest> requests = new ArrayList<>();
		try (ImageAnnotatorClient client = ImageAnnotatorClient.create(
					ImageAnnotatorSettings.newBuilder()
						.setCredentialsProvider(FixedCredentialsProvider.create(credentials))
						.build())
			) {	        

	        // 원격 파일에 대한 GCS 소스 경로를 설정합니다.
	        GcsSource gcsSource = GcsSource.newBuilder().setUri(gcsSrcUri).build();

	        // 지정된 MIME(Multipurpose Internet Mail Extensions) 유형으로 구성합니다.
	        InputConfig inputConfig = InputConfig.newBuilder()
	                .setMimeType("application/pdf") // 지원되는 MimeTypes: "application/pdf", "image/tiff"
	                .setGcsSource(gcsSource).build();

	        // 결과를 저장할 GCS 대상 경로를 설정합니다.
	        GcsDestination gcsDestination = GcsDestination.newBuilder()
	        		.setUri(gcsDestUri)
	        		.build();

	        // 출력에 대한 구성과 배치 크기를 설정합니다.
	        // 배치 크기는 각 JSON 출력 파일에 그룹화할 페이지 수를 설정합니다.
	        OutputConfig outputConfig = OutputConfig.newBuilder()
	        		.setBatchSize(10)
	        		.setGcsDestination(gcsDestination)
	                .build();

	        // Vision API에서 필요한 기능을 선택합니다.
	        Feature feature = Feature.newBuilder()
	        		.setType(Feature.Type.DOCUMENT_TEXT_DETECTION)
	        		.build();

	        // OCR 요청을 빌드합니다.
	        AsyncAnnotateFileRequest request = AsyncAnnotateFileRequest.newBuilder()
	        		.addFeatures(feature)
	                .setInputConfig(inputConfig)
	                .setOutputConfig(outputConfig)
	                .build();

	        requests.add(request);

	        // OCR 요청을 수행합니다.
	        OperationFuture<AsyncBatchAnnotateFilesResponse, OperationMetadata> response = client
	                .asyncBatchAnnotateFilesAsync(requests);

	        log.debug("작업이 완료되기를 기다리는 중입니다.");

	        // 작업 완료 대기
            response.get(180, TimeUnit.SECONDS);

            // GCS에서 결과 파일 읽기
            //Storage storage = StorageOptions.getDefaultInstance().getService();
            Storage storage = StorageOptions.newBuilder()
                    .setCredentials(credentials)
                    .build()
                    .getService();
            
            Pattern pattern = Pattern.compile("gs://([^/]+)/(.+)");
            Matcher matcher = pattern.matcher(gcsDestUri);

            if (matcher.find()) {
                String bucketName = matcher.group(1);
                String prefix = matcher.group(2);

                Bucket bucket = storage.get(bucketName);
                com.google.api.gax.paging.Page<Blob> blobs = bucket.list(Storage.BlobListOption.prefix(prefix));

                StringBuilder extractedText = new StringBuilder();

                // 모든 결과 파일을 처리
                for (Blob blob : blobs.iterateAll()) {
                    String jsonContents = new String(blob.getContent());
                    AnnotateFileResponse.Builder builder = AnnotateFileResponse.newBuilder();
                    JsonFormat.parser().merge(jsonContents, builder);
                    AnnotateFileResponse annotateFileResponse = builder.build();

                    // 각 페이지의 응답 처리
                    for (AnnotateImageResponse imageResponse : annotateFileResponse.getResponsesList()) {
                        if (imageResponse.hasError()) {
                            System.err.println("Error: " + imageResponse.getError().getMessage());
                            continue;
                        }
                        extractedText.append(imageResponse.getFullTextAnnotation().getText());
                    }
                }

                return extractedText.toString();
            } else {
                throw new Exception("GCS 경로를 파싱할 수 없습니다.");
            }
	    }
	}
}
