package com.erns.coching.common.file.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.erns.coching.common.file.domain.FileSearchDTO;
import com.erns.coching.common.file.domain.FileSetDTO;
import com.erns.coching.common.file.domain.FileVO;
import com.erns.coching.common.file.service.FileService;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
*
* <p>파일저장소</p>
*
* @author Hunwoo Park
*
*/
@Transactional
@Repository
@Slf4j
public class FileRepository extends BaseFileRepository{

	@Autowired
	private FileService fileService;

	@Getter
	@Value("${system.file.uploadpath}")
	private String uploadPath;

	@PostConstruct
	public void startup()
	{
		log.info("FileService is started");
		super.startup();
	}

	/**
	 * 파일그룹의 파일 목록을 가져온다.
	 * @param refCode
	 * @param refSeq
	 * @return
	 */
	public List<Map<String, Object>> getList(String refCode, long refSeq){
		return getList(refCode, refSeq, null);
	}

	public List<Map<String, Object>> getList(String refCode, long refSeq, String[] fileIds){
		FileSearchDTO param = new FileSearchDTO();
		param.setRefCode(refCode);
		param.setRefSeq(refSeq);
		param.setFileIds(fileIds);

		return fileService.getList(param);
	}

	public List<Map<String, Object>> getList(String refCode, long refSeq, String[] fileIds, int rowsize){
		FileSearchDTO param = new FileSearchDTO();
		param.setRefCode(refCode);
		param.setRefSeq(refSeq);
		param.setFileIds(fileIds);
		if(rowsize > 0) {
			param.setRowSize(rowsize);
		}

		return fileService.getList(param);
	}

	/**
	 * 파일 상태 변경
	 * @param fileId
	 * @param delYn
	 * @param chnr
	 * @return
	 */
	public int updateFile(String fileId, String delYn, long chnr){
		FileVO fileVO = FileVO.builder()
				.fileId(fileId)
				.delYn(delYn)
				.chnr(chnr)
				.build();
		return fileService.updateDelYn(fileVO);
	}

	/**
	 * 파일 등록, 삭제 동시처리
	 * @param newFiles
	 * @param delFiles
	 */
	public void setFiles(Collection<FileSetDTO> newFiles, Collection<FileSetDTO> delFiles) {
		setFiles(newFiles, delFiles, false);
	}

	/**
	 * 파일 등록 삭제 동시처리
	 * @param newFiles
	 * @param delFiles
	 * @param deletePhysical false: DelYn 을 'Y'로 바꾸고 물리파일은 그대로 /true:DB에서도 지우고, 물리파일도 삭제
	 */
	public List<Map<String, Object>> setFiles(Collection<FileSetDTO> newFiles, Collection<FileSetDTO> delFiles, boolean deletePhysical) {
		List<Map<String, Object>> fileList = new ArrayList<>();
		if(newFiles != null && newFiles.size() > 0) {
			for(FileSetDTO fm : newFiles) {
				log.debug("==============================================> {}", fm);
				FileVO fileVO = FileVO.builder()
								.refCode(fm.getRefcode())
								.refSeq((fm.getRefSeq()))
								.fileName(fm.getFileName())
								.fileNameDest(fm.getFileNameDest())
								.fileExt(fm.getFileExt())
								.filePath(fm.getFilePath())
								.fileSize(fm.getFileSize())
								.rgtr(fm.getRgtr())
								.chnr(fm.getChnr())
								.build();

				log.debug("==============================================> {}", fileVO);
				fileService.insert(fileVO);
				FileSearchDTO search = new FileSearchDTO();
				search.setFileId(fileVO.getFileId());
				Map<String, Object> data = fileService.load(search);
				fileList.add(data);
			}
		}

		if(delFiles == null || delFiles.size() <= 0) {
			return fileList;
		}

		for(FileSetDTO fm : delFiles) {
			String fileId = fm.getFileId();
			if(deletePhysical) {
				fileService.delete(fileId);
			}else {
				FileVO fileVO = FileVO.builder()
						.fileId(fileId)
						.delYn("Y")
						.chnr(fm.getChnr())
						.build();
				fileService.updateDelYn(fileVO);
			}
		}

		if(deletePhysical) deleteFiles(delFiles);

		return fileList;
	}

	// 공지사항 저장 시 파일 참조키 업데이트
	public void updateRefIds(List<FileSetDTO> files) {
		for(FileSetDTO item : files) {

			FileVO fileVO = FileVO.builder()
					.refSeq(item.getRefSeq())
					.chnr(item.getChnr())
					.build();

			if(item.getRefSeq() > 0) {
				fileService.updateRefSeq(fileVO);
			}
		}
	}

	/**
	 * 파일 그룹을 변경한다
	 * @param file
	 * @param newRefCode
	 * @return
	 * @throws IOException
	 */
	public int changeRefCode(FileVO file, String newRefCode) throws IOException {
		return changeRefCode(Collections.singletonList(file), newRefCode);
	}

	/**
	 * 파일 그룹을 변경한다
	 * @param files
	 * @param newRefCode
	 * @return
	 * @throws IOException
	 */
	public int changeRefCode(Collection<FileVO> files, String newRefCode) throws IOException {
		if(!StringUtils.hasText(newRefCode)) {
			return 0;
		}

		int retCnt = 0;

		for(FileVO file : files) {
			String oldRefCode = file.getRefCode();
			String oldFilePath = file.getFilePath();

			if(
				!StringUtils.hasText(oldRefCode) ||
				!StringUtils.hasText(oldFilePath)
			) {
				continue;	//파일정보가 유효하지 않음
			}

			//같으면 변경할 필요 없음
			if(file.getRefCode().equals(newRefCode)) {
				continue;
			}

			String newFilePath = oldFilePath.replace(oldRefCode, newRefCode);

			// 원본 파일 경로
			Path sourcePath = Paths.get(uploadRootPath, oldFilePath, file.getFileNameDest());
			// 이동할 대상 경로 (폴더가 없을 수도 있음)
			Path targetPath = Paths.get(uploadRootPath, newFilePath, file.getFileNameDest());

			try {
				// 대상 폴더가 없으면 생성
				Files.createDirectories(targetPath.getParent());

				// 파일 이동 (REPLACE_EXISTING 옵션 사용 시 기존 파일 덮어쓰기)
				Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

				log.debug("파일이 성공적으로 이동되었습니다. {} => {}", sourcePath, targetPath);
			} catch (IOException e) {
				log.error("파일 이동 중 오류 발생: ", e);
				throw e;
			}

			FileVO fileVO = FileVO.builder()
					.refCode(newRefCode)
					.filePath(newFilePath)
					.fileId(file.getFileId())
					.build();

			retCnt += fileService.updateRefCode(fileVO);
		}

		return retCnt;
	}
}
