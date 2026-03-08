package com.erns.coching.common.file.repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.erns.coching.common.file.domain.FileSetDTO;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * <p>파일 Repository
 * 파일 저장, 읽기</p>
 *
 * @author Hunwoo Park
 *
 */
@Slf4j
public class BaseFileRepository {

	private final SimpleDateFormat SDF_YYYYMM = new SimpleDateFormat("yyyyMM");

	@Value("${system.file.uploadpath}")
	protected String uploadRootPath;

	@PostConstruct
	public void startup()
	{
		log.info("BaseFileRepository is started");
	}

	/**
	 * Utils
	 */
	/**
	 *
	 * @param request
	 */
	public void printFilesInfo(HttpServletRequest request) {
    	MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        Iterator<String> iterator = multipartHttpServletRequest.getFileNames();
        MultipartFile multipartFile = null;
        while (iterator.hasNext()) {
        	 multipartFile = multipartHttpServletRequest.getFile(iterator.next());
            if (multipartFile.isEmpty() == false) {
                log.debug("------------- file start -------------");
                log.debug("name : " + multipartFile.getName());
                log.debug("filename : " + multipartFile.getOriginalFilename());
                log.debug("size : " + multipartFile.getSize());
                log.debug("-------------- file end --------------\n");
            }
        }
    }

	/**
	 * Multipart request 로 부터 파일 저장
	 * @param request
	 * @param inputPreFix
	 * @return
	 */
	public List<MultipartFile> getUploadFiles(HttpServletRequest request, String inputPreFix) {
		String fileParamRegex = "^"+inputPreFix+"_\\d+";

		// 파일 추출
		MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> files = multiRequest.getFileMap();
		List<MultipartFile> retFiles = new ArrayList<MultipartFile>();

		for(String paramName : files.keySet()) {
			if(!Pattern.matches(fileParamRegex, paramName)) {
				continue;
			}

			List<MultipartFile> mfList = multiRequest.getFiles(paramName);
			for(MultipartFile mFile : mfList) {

				retFiles.add(mFile);
			}
		}

		return retFiles;
	}

	/**
	 * 파일저장로 root로 부터 상대경로 리턴
	 * @param subId
	 * @return
	 */
	public Path getSubPath(String subId) {
		Calendar now = Calendar.getInstance();
		Path p = Paths.get(SDF_YYYYMM.format(now.getTime()), subId);
		return p;
	}

	/**
	 * 파일 저장시 파일명 생성
	 * @param ext
	 * @return
	 */
	public String getNewDestFileName(String ext) {
		String destFileName = UUID.randomUUID().toString() + "." + ext;
		return destFileName;
	}

	/**
	 * 파일 저장
	 * @param mFile
	 * @param fileType
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public FileSetDTO storeFile(MultipartFile mFile, String fileType) throws IllegalStateException, IOException {
		//FileVO 설정
		String orgFilename = mFile.getOriginalFilename();
		String ext = getFileExt(orgFilename);

		Path subPath = getSubPath(fileType);
		String fileDest = getNewDestFileName(ext);

		FileSetDTO retFm = new FileSetDTO();
		retFm.setRefcode(fileType);
		retFm.setFileName(orgFilename);
		retFm.setFileExt(ext);
		retFm.setFileNameDest(fileDest);
		retFm.setFilePath(subPath.toString());
		retFm.setFileSize(mFile.getSize());


		File storedFile = getOrgFile(retFm);
		{	//저장 폴더 생성
			if(storedFile == null) {
				throw new RuntimeException("Cannot save file!");
			}

			Path pathFolder = storedFile.toPath().getParent();
			if(pathFolder == null) {
				throw new RuntimeException("Cannot save file!, cannot create folder to save.");
			}

			File temp = pathFolder.toFile();
			if(! temp.exists()){
				temp.mkdirs();
			}
		}

		//파일 저장
		mFile.transferTo(storedFile);

		return retFm;
	}

	/**
	 * 확장자 추출
	 * @param fileName
	 * @return
	 */
	private String getFileExt(String fileName) {
		String retExt = "";

		String[] ext = fileName.split("\\.");

		if(ext.length > 0) {
			retExt = ext[ext.length - 1];
		}

		return retExt;
	}

	/**
	 * 물리파일을 삭제한다.
	 * @param delFiles
	 * @return
	 */
	public int deleteFiles(Collection<FileSetDTO> delFiles) {
		if(delFiles == null || delFiles.size() <= 0) {
			return 0;
		}

		int count = 0;
		for(FileSetDTO fm : delFiles) {

			File delOrgFile = getOrgFile(fm);
			if(delOrgFile.delete()) {
				count++;
			}

			// 썸네일 삭제
			List<File> delThumbFiles = new ArrayList<File>();

			for(File delThumbFile : delThumbFiles) {
				delThumbFile.delete();
			}
		}

		return count;
	}

	/**
	 * 저장소에서 물리 파일을 리턴
	 * @param FileVO
	 * @return
	 */
	public File getOrgFile(FileSetDTO FileDTO) {
		if(FileDTO == null) {
			return null;
		}

		String retPath = FileDTO.getFilePath();
		if(!StringUtils.hasText(retPath)) {
			if(!"/".equals(File.separator) && retPath.indexOf("/") >=0) {
				retPath = retPath.replaceAll("/", "\\\\");
			}

			if(!"\\".equals(File.separator) && retPath.indexOf("\\") >=0) {
				retPath = retPath.replaceAll("\\\\", File.separator);
			}
		}

		Path pFilePath = Paths.get(this.uploadRootPath, retPath, FileDTO.getFileNameDest());
		return pFilePath.toFile();
	}


	/*
	public static final void main(String args[]) {
		Path path = Paths.get("/root/sub1/sub2", "test.png");
		File file = path.toFile();

		log.info("path:{}", path);
		log.info("file:{}", file);
		log.info("path.getParent:{}", path.getParent());
	}
	*/


}
