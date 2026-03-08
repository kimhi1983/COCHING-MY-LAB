package com.erns.es.common.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erns.es.common.file.domain.FileSearchDTO;
import com.erns.es.common.file.domain.FileSetDTO;
import com.erns.es.common.file.repository.FileRepository;
import com.erns.es.common.file.service.FileService;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * <p>파일관련 Request 처리 Controller</p> 
 *
 * @author Hunwoo Park 
 *
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController{

	@Autowired
	private FileRepository fileRepo;
	
	@Autowired
	private FileService fileService;
	
	/**
	 * 첨부파일 다운로드
	 * @param req
	 * @param res
	 * @throws IOException
	 */
	@RequestMapping(value = "/download.do")
	public void downloadFile(HttpServletRequest req, HttpServletResponse res) throws IOException
	{
		String newFileName = ServletRequestUtils.getStringParameter(req, "nfn", "").trim();
		int downloadOnly = ServletRequestUtils.getIntParameter(req, "dFlag", 0);
		String fileId = ServletRequestUtils.getStringParameter(req, "fId", "").trim();
		
		if(!StringUtils.hasText(fileId)){
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		
		try{
			FileSearchDTO fileScDTO = new FileSearchDTO();
			fileScDTO.setFileId(fileId);
			
			Map<String, Object> fm = fileService.load(fileScDTO);
			if(fm == null){				
				res.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			FileSetDTO fileDTO = new FileSetDTO();
			fileDTO.setFilePath(fm.get("filePath").toString());
			fileDTO.setFileNameDest(fm.get("fileNameDest").toString());
			
			File downFile = fileRepo.getOrgFile(fileDTO);			
			if(downFile == null || !downFile.exists()) {
				res.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			
			String chMimeType = Files.probeContentType(downFile.toPath());
			String mimeType = downloadOnly == 1 || chMimeType == null ? "application/octet-stream" : chMimeType;
			String downName = newFileName; 
			if(!StringUtils.hasText(downName)){
				downName = fm.get("fileName").toString();		
			}
			
			writeToResponse(req, res, downFile, mimeType, downName);
			
		}catch(Exception e) {
			log.error("Exception", e);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
	}
	
	/**
	 * 이미지 다운로드
	 * @param req
	 * @param res
	 * @throws IOException
	 */
	//Note : 이미지 캐시 90일 (7776000)
	//@CacheControl(maxAge=7776000, policy = { CachePolicy.PUBLIC })
	@RequestMapping(value = "/image.do")
	public void downloadImage(HttpServletRequest req, HttpServletResponse res) throws IOException
	{
		String fileId = ServletRequestUtils.getStringParameter(req, "fId", "").trim();
		String newFileName = ServletRequestUtils.getStringParameter(req, "nfn", "").trim();
		
		if(!StringUtils.hasText(fileId)){
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		//TODO : 권한체크
		
		try{
			FileSearchDTO fileScDTO = new FileSearchDTO();
			fileScDTO.setFileId(fileId);
			
			Map<String, Object> fm = fileService.load(fileScDTO);
			
			if(fm == null){
				log.debug("Cannot find file:{}", fileId);
				res.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			
			FileSetDTO fileDTO = new FileSetDTO();
			fileDTO.setFilePath(fm.get("filePath").toString());
			fileDTO.setFileNameDest(fm.get("fileNameDest").toString());
			
			File downFile = fileRepo.getOrgFile(fileDTO);	
			
			String chMimeType = Files.probeContentType(downFile.toPath());			
			String mimeType = chMimeType == null ? "application/octet-stream" : chMimeType;			
			String downName = newFileName; 
			if(!StringUtils.hasText(downName)){
				downName = fm.get("fileName").toString();			
			}
			
			writeToResponse(req, res, downFile, mimeType, downName);
			
			//res.setHeader("Cache-Control", "public, max-age=7776000");
			
		}catch(Exception e) {
			log.error("Exception", e);
			
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
	}
	
		
	
	
	public static void writeToResponse(HttpServletRequest req, HttpServletResponse res, File srcFile, String mimeType) throws IOException {
		writeToResponse(req, res, srcFile, mimeType, null);
	}
	
	/**
	 * 스트림을 Response로 전송
	 * @param req
	 * @param res
	 * @param srcFile
	 * @param mimeType
	 * @param downName
	 * @throws IOException
	 */
	public static void writeToResponse(HttpServletRequest req, HttpServletResponse res, File srcFile, String mimeType, String downName) throws IOException {
		if(mimeType != null && !mimeType.trim().equals("")) {
			res.setContentType(mimeType);
		} else {
			res.setContentType("application/octet-stream");
		}
		
		if(!srcFile.exists()) {
			log.warn("파일을 찾을수 없습니다:{}", srcFile);
			res.setStatus(HttpStatus.NOT_FOUND.value());
			return;
		}
		
		if(downName != null) {
			//브라우져에 따른 파일명 기입
			setContentDispositionForDownload(req, res, downName);
		}
		
		OutputStream os = res.getOutputStream();
		try{
			WritableByteChannel outputChannel = Channels.newChannel(os);
			FileInputStream is = new FileInputStream(srcFile);
			FileChannel fc = is.getChannel();
			
			res.setContentLength(is.available());
			fc.transferTo(0, is.available(), outputChannel);
			
			is.close();
			os.close();
		} catch (IOException e){
			e.printStackTrace();
			log.error("File writing error!", e);			
		} finally {
			if (os != null) {
				try{
					os.close();
				} catch (Exception e) {
					log.error("Outout stream close error!", e);
				}
			}
		}
	}	
	
	// Browser 설정에 영향이 미치는 부분 처리
	private static void setContentDispositionForDownload(HttpServletRequest req, HttpServletResponse res, String downName) throws UnsupportedEncodingException {
		
		// 브라우져에 따른 파일명 기입		
		String resFilename = java.net.URLEncoder.encode(downName, "UTF-8");
		resFilename = resFilename.replaceAll("\\+", "%20");
		
		String userAgent = req.getHeader("User-Agent");
		userAgent = userAgent == null ? "" : userAgent.toUpperCase(Locale.getDefault());

		// Browser 설정에 영향이 미치는 부분 처리
		if (userAgent.indexOf("MSIE 5.5") > -1) {
			res.setHeader("Content-Disposition", "filename=" + resFilename + ";");
			
		} else if (userAgent.indexOf("MSIE 6.0") > -1) {
			res.setHeader("Content-Disposition", "attachment; filename=" + resFilename + ";");
			
		} else if (userAgent.indexOf("MSIE") > -1 || userAgent.indexOf("TRIDENT") > -1) {
			res.setHeader("Content-Disposition", "attachment; filename=" + resFilename + ";");
			
		} else {
			resFilename = new String(downName.getBytes("UTF-8"), "ISO-8859-1").replaceAll("[\r\n]","");
			res.setHeader("Content-Disposition", "attachment; filename=\"" + resFilename + "\";");
		}
	}
	
}
