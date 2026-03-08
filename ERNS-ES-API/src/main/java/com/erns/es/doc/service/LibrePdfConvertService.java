package com.erns.es.doc.service;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.LocalConverter;
import org.jodconverter.local.office.LocalOfficeManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.erns.es.doc.DocToPdfConfig;
import com.erns.es.doc.type.SupportDocType;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * <p>다양한 문서를 PDF 로 변환한다.
 * jodconverter + LibreOffice 조합으로 변환
 * NOTE: 구동하는 서버에 LibreOffice 가 설치되어 있어야함
 * 테스트 완료항목 :
 * DOC, DOCX, PPT, PPTX, XLS, XLSX, HWP, HWPX
 * </p> 
 *
 * @author Hunwoo Park 
 *
 */
@Slf4j
@Service
public class LibrePdfConvertService extends AbstractPdfConvertService {
	
	private OfficeManager officeManager;
	
	@Value("${converter.libre-office.launch:false}")
	private boolean LIBRE_OFFICE_LAUNCH;	
	
	@Value("${converter.libre-office.home:C:\\Program Files\\LibreOffice}")
	private String LIBRE_OFFICE_HOME;
	
	@Value("${converter.libre-office.temp.dir:D:\\temp}")
	private String LIBRE_OFFICE_TEMP_DIR;
	
	@PostConstruct
	public void startup() throws IOException
	{
		if(!LIBRE_OFFICE_LAUNCH) {
			log.info("LibreOffice is not launched");
			return;
		}

		log.info("LibrePdfConvertService is started");
		super.startup();		
		
		//필요하다면 임시 디렉토리 생성		
		mkdirs(Paths.get(LIBRE_OFFICE_TEMP_DIR));
		
		
		// 오피스 매니저 초기화 및 풀링 설정
		log.info("OfficeManager starting...");
		LocalOfficeManager.Builder officeBilder = LocalOfficeManager.builder();
        if(StringUtils.hasText(LIBRE_OFFICE_HOME)) {
        	//officeBilder.install() // LibreOffice 설치가 필요한 경우 사용
        	log.info("Set officeHome to: {}", LIBRE_OFFICE_HOME);
        	// LibreOffice 경로를 Windows 환경에 맞게 설정합니다.
        	officeBilder.officeHome(LIBRE_OFFICE_HOME);
        }
        
        officeManager = officeBilder
        	.taskExecutionTimeout(240000L) // 선택사항: 작업 실행 타임아웃 설정
            .taskQueueTimeout(60000L) // 선택사항: 작업 대기 타임아웃 설정
            .maxTasksPerProcess(20) // 선택사항: 프로세스당 최대 작업 수 설정            
            .build();
        
        try {
            // 오피스 매니저 시작
            officeManager.start();
            log.info("OfficeManager started");
        } catch (Exception e) {
            throw new RuntimeException("오피스 매니저 시작 실패", e);
        }
		
        
        {	//지원하는 파일 포멧 정리
        	converterMap.put(SupportDocType.DOC, wrapFunction((config) -> convertDocToPdfUsingJodConverter(config)));
    		converterMap.put(SupportDocType.DOCX, wrapFunction((config) -> convertDocToPdfUsingJodConverter(config)));
    		converterMap.put(SupportDocType.PPT, wrapFunction((config) -> convertDocToPdfUsingJodConverter(config)));
    		converterMap.put(SupportDocType.PPTX, wrapFunction((config) -> convertDocToPdfUsingJodConverter(config)));
    		converterMap.put(SupportDocType.XLS, wrapFunction((config) -> convertDocToPdfUsingJodConverter(config)));
    		converterMap.put(SupportDocType.XLSX, wrapFunction((config) -> convertDocToPdfUsingJodConverter(config)));
    		
    		//HWP, HWPX 는 jodconverter 로 사용이 불가
    		converterMap.put(SupportDocType.HWP, wrapFunction((config) -> convertDocToPdfUsingProcessBuilder(config)));
    		converterMap.put(SupportDocType.HWPX, wrapFunction((config) -> convertDocToPdfUsingProcessBuilder(config)));	
        }
		
	}
	
	@PreDestroy
    public void destroy() {
        // 오피스 매니저 종료
        try {
            if (officeManager != null) {
                officeManager.stop();
                log.info("OfficeManager stopped");
            }
        } catch (Exception e) {
            log.error("오피스 매니저 종료 에러", e);
        }
    }
	
	@Override
	public boolean convertToPdf(DocToPdfConfig convertConfig) throws IOException {
		//필수값 검사
		convertConfig.validate();		
		log.debug("convertOption:{}",convertConfig);
		
		Function<DocToPdfConfig, Boolean> converter = converterMap.get(convertConfig.getSourceFormat());
		if (converter == null) {
            throw new IllegalArgumentException("Unsupported source format: " + convertConfig.getSourceFormat());
        }
		
		return converter.apply(convertConfig);
	}
	
	
	// XLSX to PDF Conversion
	private Boolean convertDocToPdfUsingJodConverter(DocToPdfConfig convertConfig) throws IOException {
	    // Create a unique temporary file for intermediate PDF storage
	    Path tempFile = Files.createTempFile("pdfconverter_" + UUID.randomUUID(), ".tmp");
	    
        try (InputStream input = convertConfig.getSourceInputStream();
	         OutputStream tempOutputStream = Files.newOutputStream(tempFile);
	         OutputStream finalOutput = convertConfig.getDestinationOutputStream()) {

			// Perform conversion
			DocumentConverter converter = LocalConverter.make(officeManager);
			converter
				.convert(input)
				.to(tempOutputStream).as(DefaultDocumentFormatRegistry.PDF) //임시 PDF 로 생성
				.execute();

			if (StringUtils.hasText(convertConfig.getWatermarkText())) {
				// 워터마크 추가
				try (InputStream tempInputStream = Files.newInputStream(tempFile)) {
					addWatermarkToPdf(tempInputStream, finalOutput, convertConfig.getWatermarkText());
				}

			}else {				
				// 결과물로 전송
				try (FileInputStream tempInputStream = new FileInputStream(tempFile.toFile());
                     FileChannel fileChannel = tempInputStream.getChannel();
                     WritableByteChannel outputChannel = Channels.newChannel(finalOutput)) {

                    fileChannel.transferTo(0, fileChannel.size(), outputChannel);
                }
			}


			return true; 
		} catch (OfficeException e) {
			log.error("PDF Converting Error", e);
			
		} finally {
			// Ensure the temporary file is deleted
			Files.deleteIfExists(tempFile);
		}

        return false;               
	}
	
	// XLSX to PDF Conversion
	private Boolean convertDocToPdfUsingProcessBuilder(DocToPdfConfig convertConfig) throws IOException {
	    
	    try {
	    	Path libreOfficePath = Paths.get(LIBRE_OFFICE_HOME, "program", "soffice.exe");
	    	String cliCommand = libreOfficePath.toString();
	    	
	        // Prepare the command to use LibreOffice CLI for conversion
	        ProcessBuilder processBuilder = new ProcessBuilder(
	        	cliCommand, // Adjust this path to your LibreOffice installation
	            "--headless",
	            "--convert-to", "pdf:writer_pdf_Export",
	            convertConfig.getSourceFilePath().toString(),
	            "--outdir", LIBRE_OFFICE_TEMP_DIR
	        );

	        processBuilder.inheritIO(); // This will direct the output/error streams to the current process
	        Process process = processBuilder.start();
	        int exitCode = process.waitFor(); // Wait for the process to finish
	        if (exitCode != 0) {
	            log.error("LibreOffice conversion process failed with exit code: " + exitCode);
	            return false;
	        }
	        
	        // 입력 파일 이름에서 확장자를 제거하고 ".pdf"를 추가하여 변환된 파일 이름 생성
	        String inputFileName = convertConfig.getSourceFilePath().getFileName().toString();
	        String convertOutputFileName = inputFileName.replaceFirst("\\.[^.]+$", "") + ".pdf"; // 기존 확장자 제거 후 ".pdf" 추가

	        // 변환된 파일의 전체 경로 생성
	        Path tempPutputFilePath = Paths.get(LIBRE_OFFICE_TEMP_DIR, convertOutputFileName);

	        // Watermark processing if needed
	        try (OutputStream finalOutput = convertConfig.getDestinationOutputStream()) {
	            if (StringUtils.hasText(convertConfig.getWatermarkText())) {
	            	// 워터마크 추가
	                try (InputStream tempInputStream = Files.newInputStream(tempPutputFilePath)) {
	                    addWatermarkToPdf(tempInputStream, finalOutput, convertConfig.getWatermarkText());
	                }
	            }else {
	            	
	            	// 결과물로 전송
	                try (FileInputStream tempInputStream = new FileInputStream(tempPutputFilePath.toFile());
	                     FileChannel fileChannel = tempInputStream.getChannel();
	                     WritableByteChannel outputChannel = Channels.newChannel(finalOutput)) {

	                    fileChannel.transferTo(0, fileChannel.size(), outputChannel);
	                }
	            	
	            }
	            
	            // Ensure the temporary file is deleted
				Files.deleteIfExists(tempPutputFilePath);
	        }
	        return true;

	    } catch (Exception e) {
	        log.error("PDF Conversion Error using ProcessBuilder", e);
	        return false;
	    } finally {
	    
	    }
               
	}
	
	private void addWatermarkToPdf(InputStream inputPdf, OutputStream output, String watermarkText) throws IOException {
	    try (PDDocument document = PDDocument.load(inputPdf)) {
	        // 외부 폰트 파일 사용	    	
	    	PDType0Font font = PDType0Font.load(document, resourceLoader.getResource(watermarkFontPath).getInputStream());

	        for (PDPage page : document.getPages()) {
	            // 페이지 크기를 가져옵니다.
	            float pageWidth = page.getMediaBox().getWidth();
	            float pageHeight = page.getMediaBox().getHeight();

	            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
	                contentStream.setNonStrokingColor(new Color(200, 200, 200, 100)); // 반투명 회색
	                contentStream.setFont(font, 50); // 외부 폰트 설정

	                // 워터마크 각도 설정 (기본 -30도 회전)
	                double rotationAngle = Math.toRadians(-30);

	                // 워터마크 텍스트 길이를 기준으로 가로 간격 설정
	                int textWidth = (int) (font.getStringWidth(watermarkText) / 1000 * 50); // 폰트 크기에 따라 텍스트 너비 계산
	                int textHeight = 50; // 폰트 크기와 일치
	                int xOffset = textWidth + 50; // 텍스트 너비 + 추가 간격
	                int yOffset = textHeight + 50; // 텍스트 높이 + 추가 간격

	                // 반복적으로 워터마크 그리기
	                for (float y = -pageHeight; y < pageHeight * 1.5; y += yOffset) {
	                    for (float x = -pageWidth; x < pageWidth * 1.5; x += xOffset) {
	                        // 텍스트 회전 및 위치 설정
	                        contentStream.saveGraphicsState();
	                        contentStream.beginText();
	                        contentStream.setTextMatrix((float) Math.cos(rotationAngle), (float) Math.sin(rotationAngle),
	                                (float) -Math.sin(rotationAngle), (float) Math.cos(rotationAngle), x, y);
	                        contentStream.showText(watermarkText);
	                        contentStream.endText();
	                        contentStream.restoreGraphicsState();
	                    }
	                }
	            }
	        }
	        document.save(output);
	        
	        log.debug("Watermark added successfully: {}", watermarkText);
	    }
	}


	

}
