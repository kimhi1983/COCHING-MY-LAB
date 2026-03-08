package com.erns.es.common.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

public class ImageHelper {
	public static final Set<String> SURPORT_FORMAT = new HashSet<>();
	
	static {
		SURPORT_FORMAT.add("png");
		SURPORT_FORMAT.add("gif");
		SURPORT_FORMAT.add("jpeg");
		SURPORT_FORMAT.add("jpg");
		SURPORT_FORMAT.add("bmp");
	}
	
	public static BufferedImage makeFillThumbnail(InputStream file
			, int thumbWidth
    		, int thumbHeight
    		, String backgroundColor
    		, String borderdColor) throws IOException {
        BufferedImage originImage = ImageIO.read(file);

        return makeFillThumbnail(originImage, thumbWidth, thumbHeight, backgroundColor, borderdColor);
    }

    public static BufferedImage makeFillThumbnail(File file
    		, int thumbWidth
    		, int thumbHeight
    		, String backgroundColor
    		, String borderdColor) throws IOException{
        BufferedImage originImage = ImageIO.read(file);

        return makeFillThumbnail(originImage, thumbWidth, thumbHeight, backgroundColor, borderdColor);
    }
    
    public static BufferedImage makeRatioThumbnail(InputStream file
			, int thumbWidth
    		, int thumbHeight    		
    		, String borderdColor) throws IOException {
        BufferedImage originImage = ImageIO.read(file);

        return makeRatioThumbnail(originImage, thumbWidth, thumbHeight, borderdColor);
    }

    public static BufferedImage makeRatioThumbnail(File file
    		, int thumbWidth
    		, int thumbHeight
    		, String borderdColor) throws IOException{
        BufferedImage originImage = ImageIO.read(file);

        return makeRatioThumbnail(originImage, thumbWidth, thumbHeight, borderdColor);
    }
    
    public static BufferedImage makeFillThumbnail(BufferedImage originImage
    		, int thumbWidth
    		, int thumbHeight
    		, String backgroundColor
    		, String borderdColor)
    {
    	BufferedImage bufferedThumbnailImage;
        int sourceWidth = originImage.getWidth();
        int sourceHeight = originImage.getHeight();
        int locationX = 0;
        int locationY = 0;
        double ratio = 1;        

        if(sourceWidth > sourceHeight){
            ratio = thumbWidth / (double)originImage.getWidth();
            sourceWidth = (int)(originImage.getWidth() * ratio);
            sourceHeight = (int)(originImage.getHeight() * ratio);
            locationY = (thumbHeight - sourceHeight) / 2;
        }else{
            ratio = thumbHeight / (double)originImage.getHeight();
            sourceWidth = (int)(originImage.getWidth() * ratio);
            sourceHeight = (int)(originImage.getHeight() * ratio);
            locationX = (thumbWidth - sourceWidth) / 2;
        }

        bufferedThumbnailImage = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphic = bufferedThumbnailImage.createGraphics();
        
        if(backgroundColor == null || backgroundColor.isEmpty()) {
        	graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphic.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            graphic.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            graphic.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        }else{
        	//Set Background Color
        	Color bgColor = Color.WHITE; 
        	if(backgroundColor != null) {
        		bgColor = Color.decode(backgroundColor);
        	}
            graphic.setColor(bgColor);
            graphic.fillRect(0, 0, thumbWidth, thumbHeight);
        }
        
        
        graphic.drawImage(originImage, locationX, locationY, sourceWidth, sourceHeight, null);
        
        //Draw Border
        if(borderdColor != null && !borderdColor.isEmpty()) {
        	graphic.setColor(Color.decode(borderdColor));
        	graphic.drawRect(0, 0, --thumbWidth, --thumbHeight);
        }		
        graphic.dispose();

        return bufferedThumbnailImage;
    }
    
    public static BufferedImage makeRatioThumbnail(BufferedImage originImage
    		, int thumbMaxWidth
    		, int thumbMaxHeight
    		, String borderdColor)
    {
    	BufferedImage bufferedThumbnailImage;
        int sourceWidth = originImage.getWidth();
        int sourceHeight = originImage.getHeight();
        int newWidth = Math.min(thumbMaxWidth, sourceWidth);
		int newHeight = Math.min(thumbMaxHeight, sourceHeight);
		
		
		double rateOrg = ((double) sourceWidth) / ((double) sourceHeight);
		if (rateOrg >= 1) {
			if (sourceWidth < newWidth)
				newWidth = sourceWidth;

			newHeight = (int) (((double) newWidth) / rateOrg);
		} else {
			if (sourceHeight < newHeight)
				newHeight = sourceHeight;

			newWidth = (int) (((double) newHeight) * rateOrg);
		}
		
		bufferedThumbnailImage = new BufferedImage(newWidth, newHeight, BufferedImage.TRANSLUCENT);
		Graphics2D graphic = bufferedThumbnailImage.createGraphics();
		graphic.drawImage(originImage, 0, 0, newWidth, newHeight, null);
		
		//Draw Border
        if(borderdColor != null) {
        	graphic.setColor(Color.decode(borderdColor));
        	graphic.drawRect(0, 0, --newWidth, --newHeight);
        }		
        graphic.dispose();

        return bufferedThumbnailImage;
    }
        
    
    /**
	 * Test Main method
	 * 
	 * @param args
	 */
    public static void main(String[] args) {
		String source = "C:\\Users\\zuch\\Pictures\\코코 제리\\IMG_20190815_211806.jpg";		
		String output = "C:\\Users\\zuch\\Pictures\\코코 제리\\thumb\\test";
		String outputExt = "png";
		int outWidth = 500;
		int outHeight = 500;
		//String outBgColor = "#ffffff";
		String outBgColor = null;
		String outBdColor = "#cccccc";

		try {
			BufferedImage bufferImage1 = makeFillThumbnail(new File(source),outWidth,outHeight,outBgColor,outBdColor);			
			File outputfile = new File(output+"_1."+outputExt);
			ImageIO.write(bufferImage1, outputExt, outputfile);
			
			BufferedImage bufferImage2 = makeRatioThumbnail(new File(source),outWidth,outHeight,outBdColor);			
			outputfile = new File(output+"_2."+outputExt);
			ImageIO.write(bufferImage2, outputExt, outputfile);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
