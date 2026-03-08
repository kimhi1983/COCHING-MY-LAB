package com.erns.es.doc.service;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.erns.es.doc.DocToPdfConfig;
import com.erns.es.doc.type.SupportDocType;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * <p>MS Office 문서를 PDF 로 변환한다.
 * Note: PPT, PPTX 등은 큰 문제가 없지만,
 * Xls 변환은 변환된 문서가 원본가 크게 차이가 있어 사용에 적합하지 않음
 * </p> 
 *
 * @author Hunwoo Park 
 *
 */
@Slf4j
@Service
public class PoiPdfConvertService extends AbstractPdfConvertService{
	
	@PostConstruct
	public void startup() throws IOException
	{
		super.startup();
		log.info("PoiPdfConvertService is started");
		
		converterMap.put(SupportDocType.PPTX, wrapFunction((config) -> convertPptxToPdfUsingPoi(config)));
	}
	
	@Override
	public boolean convertToPdf(DocToPdfConfig convertConfig) throws IOException {
		
		//필수값 검사
		convertConfig.validate();
		
		Function<DocToPdfConfig, Boolean> converter = converterMap.get(convertConfig.getSourceFormat());
		if (converter == null) {
            throw new IllegalArgumentException("Unsupported source format: " + convertConfig.getSourceFormat());
        }
		
		return converter.apply(convertConfig);
	}
	
	// PPTX to PDF Conversion
	public Boolean convertPptxToPdfUsingPoi(DocToPdfConfig convertConfig) {		
		
        try (
            InputStream inputStream = convertConfig.getSourceInputStream();
            XMLSlideShow ppt = new XMLSlideShow(inputStream);
        ) {
            Dimension pageSize = ppt.getPageSize();
            PDDocument pdfDoc = new PDDocument();

            double scale = 2.0;
            int imageWidth = (int) (pageSize.width * scale);
            int imageHeight = (int) (pageSize.height * scale);

            for (XSLFSlide slide : ppt.getSlides()) {
                BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = img.createGraphics();

                // 고품질 렌더링 설정
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

                // 슬라이드 크기 조정 및 렌더링
                graphics.scale(scale, scale);
                slide.draw(graphics);

                // 워터마크 추가
                if(!StringUtils.hasText(convertConfig.getWatermarkText())) {
                    addWatermarkToImage(graphics, imageWidth, imageHeight, convertConfig.getWatermarkText());
                }

                graphics.dispose();

                // 이미지 파일로 임시 저장
                File imageFile = new File("slide.png");
                ImageIO.write(img, "png", imageFile);

                // PDF에 추가
                PDPage page = new PDPage(new PDRectangle((float) pageSize.width, (float) pageSize.height));
                pdfDoc.addPage(page);
                PDImageXObject pdImage = PDImageXObject.createFromFile("slide.png", pdfDoc);
                PDPageContentStream contentStream = new PDPageContentStream(pdfDoc, page);

                // 이미지 크기를 원래 PDF 페이지 크기에 맞춤
                contentStream.drawImage(pdImage, 0, 0, (float) pageSize.width, (float) pageSize.height);
                contentStream.close();

                imageFile.delete(); // 임시 파일 삭제
            }
            pdfDoc.save(convertConfig.getDestinationOutputStream());
            pdfDoc.close();
            
            return true;
        }catch(IOException e) {
        	log.error("convert error", e);        	
        }
        
        return false;
    }
	
		
	//워터마크를 추가한다.
	private void addWatermarkToImage(Graphics2D graphics, int width, int height, String watermarkText) {
	    // 한글을 지원하는 폰트 설정
	    Font font = new Font("Malgun Gothic", Font.BOLD, 50); // "Malgun Gothic"을 사용
	    if (!font.getFamily().equals("Malgun Gothic")) {
	        // 시스템에 폰트가 없는 경우 기본 폰트 설정
	        font = new Font("SansSerif", Font.BOLD, 50);
	    }		
	    graphics.setFont(font);
	    graphics.setColor(new Color(200, 200, 200, 100)); // 투명도를 위한 RGBA 색상
	    graphics.rotate(Math.toRadians(-30)); // -30도 회전

	    // 워터마크 텍스트 길이를 기준으로 가로 간격 설정
	    FontMetrics metrics = graphics.getFontMetrics(font);
	    int textWidth = metrics.stringWidth(watermarkText);
	    int textHeight = metrics.getHeight();
	    int xOffset = textWidth + 50; // 텍스트 너비 + 추가 간격
	    int yOffset = textHeight + 50; // 텍스트 높이 + 추가 간격

	    int x = -width / 2;
	    int y = 0;

	    // 워터마크 반복적으로 그리기
	    while (y < height * 1.5) {
	        while (x < width * 1.5) {
	            graphics.drawString(watermarkText, x, y);
	            x += xOffset; // 텍스트 길이에 따라 가로 간격 조정
	        }
	        x = -width / 2;
	        y += yOffset; // 텍스트 높이에 따라 세로 간격 조정
	    }

	    // 회전 해제 (다른 그래픽 작업에 영향 방지)
	    graphics.rotate(Math.toRadians(+30));
	}	
}
