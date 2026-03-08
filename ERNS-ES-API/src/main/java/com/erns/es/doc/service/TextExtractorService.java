package com.erns.es.doc.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.sl.extractor.SlideShowExtractor;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erns.es.doc.ThrowingBiFunction;
import com.erns.es.doc.type.SupportDocType;

import kr.dogfoot.hwplib.object.HWPFile;
import kr.dogfoot.hwplib.reader.HWPReader;
import kr.dogfoot.hwplib.tool.textextractor.TextExtractMethod;
import kr.dogfoot.hwplib.tool.textextractor.TextExtractOption;
import kr.dogfoot.hwpxlib.object.HWPXFile;
import kr.dogfoot.hwpxlib.reader.HWPXReader;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * <p>
 * 문서에서 텍스트를 추출한다.
 * Note
 * POI 사용 : DOC, DOCX, PPT, PPTX, XLS, XLSX
 * hwplib, hwpxlib 사용 : HWP, HWPX
 * Google Vision API : 이미지
 * </p>
 *
 * @author Hunwoo Park
 *
 */
@Slf4j
@Service
public class TextExtractorService {
	
	@Autowired
	private GoogleVisionApiService googleVisionApiService;
	
	//확장자별로 텍스트 추출방식을 저장함
	private Map<String, BiFunction<File, FileInputStream, String>> extractorMap = new HashMap<>();	
	
	public TextExtractorService() {
		//확장자 별 처리함수 등록		
		extractorMap.put(SupportDocType.DOC.getExt(), wrapFunction((file, fis) -> extractTextFromDoc(fis)));
	    extractorMap.put(SupportDocType.XLS.getExt(), wrapFunction((file, fis) -> extractTextFromXls(fis)));
	    extractorMap.put(SupportDocType.PPT.getExt(), wrapFunction((file, fis) -> extractTextFromPpt(fis)));
	    
	    extractorMap.put(SupportDocType.DOCX.getExt(), wrapFunction((file, fis) -> extractTextFromDocx(fis)));
	    extractorMap.put(SupportDocType.XLSX.getExt(), wrapFunction((file, fis) -> extractTextFromXlsx(fis)));
	    extractorMap.put(SupportDocType.PPTX.getExt(), wrapFunction((file, fis) -> extractTextFromPptx(fis)));
	    
	    extractorMap.put(SupportDocType.PDF.getExt(), wrapFunction((file, fis) -> extractTextFromPdf(fis)));
	    extractorMap.put(SupportDocType.HWP.getExt(), wrapFunction((file, fis) -> extractTextFromHwp(fis)));
	    extractorMap.put(SupportDocType.HWPX.getExt(), wrapFunction((file, fis) -> extractTextFromHwpx(file)));
	    
	    for(SupportDocType imageSdt : SupportDocType.values()) {
	    	if("image".equalsIgnoreCase(imageSdt.getType())) {
	    		extractorMap.put(imageSdt.getExt(), wrapFunction((file, fis) -> extractTextFromImage(fis)));
	    	}
	    }
	}
	
	private <T, U> BiFunction<T, U, String> wrapFunction(ThrowingBiFunction<T, U, String> function) {
        return (t, u) -> {
            try {
                return function.apply(t, u);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        };
    }
	
	/**
	 * 각종 문서에서 텍스트를 추출한다.
	 * 지원되는 포멧은 com.erns.es.doc.type.SupportDocType 에 정의
	 * @param sourceFile
	 * @return
	 * @throws IOException
	 */
	public String extractText(Path sourceFile) throws IOException {
		// Check if the path is a valid file
		if (!Files.isRegularFile(sourceFile)) {
			throw new IllegalArgumentException("The provided path is not a valid file: " + sourceFile);
		}
		String fileName = sourceFile.getFileName().toString().toLowerCase(Locale.ROOT);

		for (SupportDocType docType : SupportDocType.values()) {
			if (fileName.endsWith("." + docType.getExt())) {
				BiFunction<File, FileInputStream, String> extractor = extractorMap.get(docType.getExt());
				if (extractor == null) {
		            throw new IllegalArgumentException("Unsupported file format: " + docType.getExt());
		        }
				
				File file = sourceFile.toFile();
	            try (FileInputStream fis = new FileInputStream(file)) {
	                return extractor.apply(file, fis); // Pass both File and FileInputStream
	            }
			}
		}

		throw new IllegalArgumentException("Unsupported file format: " + fileName);

	}

	/**
	 * doc 에서 텍스트를 추출한다 
	 * [테스트필요]
	 * 
	 * @param sourceFile
	 * @return
	 * @throws IOException
	 */
	public String extractTextFromDoc(FileInputStream sourceFile) throws IOException {
	    try (HWPFDocument doc = new HWPFDocument(sourceFile);
	         WordExtractor extractor = new WordExtractor(doc)) {
	        return extractor.getText();
	    }
	}
	
	/**
	 * docx 에서 텍스트를 추출한다 
	 * [테스트완료]
	 * 
	 * @param sourceFile
	 * @return
	 * @throws IOException
	 */
	public String extractTextFromDocx(FileInputStream sourceFile) throws IOException {
	    try (XWPFDocument docx = new XWPFDocument(sourceFile);
	         XWPFWordExtractor extractor = new XWPFWordExtractor(docx)) {
	        return extractor.getText();
	    }
	}

	/**
	 * xls 파일에서 텍스트를 추출한다
	 * @param sourceFile
	 * @return
	 * @throws IOException
	 */
	public String extractTextFromXls(FileInputStream sourceFile) throws IOException {
		try (HSSFWorkbook wb = new HSSFWorkbook(sourceFile);
			ExcelExtractor ee = new ExcelExtractor(wb)) {
			
			return ee.getText();
		}
	}
	
	/**
	 * xlsx 파일에서 텍스트를 추출한다.
	 * @param sourceFile
	 * @return
	 * @throws IOException
	 */
	public String extractTextFromXlsx(FileInputStream sourceFile) throws IOException {
		try (XSSFWorkbook wb = new XSSFWorkbook(sourceFile);
			XSSFExcelExtractor extractor = new XSSFExcelExtractor(wb)) {
			extractor.setFormulasNotResults(true);
			extractor.setIncludeSheetNames(false);
			
			return extractor.getText();
		}
	}

	/**
	 * ppt 파일에서 텍스트를 추출한다.
	 * @param sourceFile
	 * @return
	 * @throws IOException
	 */
	public String extractTextFromPpt(FileInputStream sourceFile) throws IOException {
	    try (HSLFSlideShow ppt = new HSLFSlideShow(sourceFile);
	         SlideShowExtractor<HSLFShape, HSLFTextParagraph> extractor = new SlideShowExtractor<>(ppt)) {
	        extractor.setSlidesByDefault(true);
	        extractor.setMasterByDefault(true);
	        return extractor.getText();
	    }
	}

	/**
	 * pptx 파일에서 텍스트를 추출한다.
	 * @param sourceFile
	 * @return
	 * @throws IOException
	 */
	public String extractTextFromPptx(FileInputStream sourceFile) throws IOException {
	    try (XMLSlideShow pptx = new XMLSlideShow(sourceFile);
	         SlideShowExtractor<XSLFShape, XSLFTextParagraph> extractor = new SlideShowExtractor<>(pptx)) {
	        extractor.setSlidesByDefault(true);
	        extractor.setMasterByDefault(true);
	        return extractor.getText();
	    }
	}
	
	
	/**
	 * PDF 파일로 부터 텍스트를 추출한다.
	 * @param sourceFile
	 * @return
	 * @throws IOException
	 */
	public String extractTextFromPdf(FileInputStream sourceFile) throws IOException {
		
		try (PDDocument document = PDDocument.load(sourceFile)) {
            if (!document.isEncrypted()) {
                PDFTextStripper pdfStripper = new PDFTextStripper();
                return pdfStripper.getText(document);    
            } else {
                log.warn("The PDF is encrypted and cannot be processed.");
            }
        }		
		
		return "";
	}
	
	/**
	 * 한글파일 hwp 에서 텍스트를 추출한다.
	 * @param sourceFile
	 * @return
	 * @throws IOException
	 */
	public String extractTextFromHwp(FileInputStream sourceFile) throws IOException {

		try {
			// HWP 파일 읽기
			HWPFile hwpFile = HWPReader.fromInputStream(sourceFile);
			if(hwpFile == null) {
				log.warn("Failed to read the HWP file.");
				return "";
			}
			
			TextExtractOption option = new TextExtractOption();
	        option.setMethod(TextExtractMethod.InsertControlTextBetweenParagraphText);
	        option.setWithControlChar(false);
	        option.setAppendEndingLF(true);

		    String hwpText = kr.dogfoot.hwplib.tool.textextractor.TextExtractor.extract(hwpFile, option);
		    
			return hwpText;
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}
	
	/**
	 * 한글파일 hwpx 에서 텍스트를 추출한다.
	 * @param sourceFile
	 * @return
	 * @throws IOException
	 */
	public String extractTextFromHwpx(File sourceFile) throws IOException {

		try {
			// HWP 파일 읽기
			HWPXFile hwpxFile = HWPXReader.fromFile(sourceFile);
			if(hwpxFile == null) {
				log.warn("Failed to read the HWPX file.");
				return "";
			}
			
			String hwpText = kr.dogfoot.hwpxlib.tool.textextractor.TextExtractor.extract(hwpxFile
					,kr.dogfoot.hwpxlib.tool.textextractor.TextExtractMethod.AppendControlTextAfterParagraphText,
	                true,
	                new kr.dogfoot.hwpxlib.tool.textextractor.TextMarks()
	                        .lineBreakAnd("\n")
	                        .paraSeparatorAnd("\n")
					);
		    
			return hwpText;
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}
	
	/**
	 * Google Vision API 로 이미지에서 텍스트를 추출한다.
	 * @param sourceFile
	 * @return
	 * @throws IOException
	 */
	public String extractTextFromImage(FileInputStream sourceFile) throws IOException{
		
		String eText = googleVisionApiService.extractFromImage(sourceFile);
		return eText;
	}
	
	
	public String extractTextFromPdfOcr(FileInputStream sourceFile) throws IOException{
		return "";
	}
	
	
	public static void main(String[] args) {
		
		
		String[] inputFiles = new String[] {
//				"D:\\work\\es_test\\docx\\231117_NICE4B2B 개인정보이용및수집동의.docx"
//				, "D:\\work\\es_test\\xlsx\\80007859_[주식회사 헥토헬스케어]드시모네 프리미엄 플러스.xlsx"
//				, "D:\\work\\es_test\\pptx\\오토빌IOS_QA_240610.pptx"
//				, "D:\\work\\es_test\\doc\\231117_NICE4B2B 이용약관.doc"
//				, "D:\\work\\es_test\\xls\\자동차안전연구원_수정요청사항정리_221118.xls"
//				, "D:\\work\\es_test\\ppt\\오토빌IOS_QA_240610.ppt"
//				
//				,"D:\\work\\es_test\\pdf\\1. COSMAX 신규 C.I 가이드(24.06.10).pdf"
//				,"D:\\work\\es_test\\hwp\\0. 보안적합성검증-장비현황표.hwp"
//				,
				"D:\\work\\es_test\\hwp\\[붙임3] [서식1-3] 비밀유지계약서.hwp"
//				,"D:\\work\\es_test\\image\\화면 캡처 2022-09-26 101429.png" //이미지
				
				,"D:\\work\\es_test\\hwpx\\multipara.hwpx"
		};
        try {
        	TextExtractorService tes = new TextExtractorService();
        	for(String filePath : inputFiles) {
        		Path targetFile = Paths.get(filePath);
        		log.debug("targetFile:{}", targetFile);
                String extractedText = tes.extractText(targetFile);
                log.debug("extractedText:\\n{}", extractedText);
        	}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
