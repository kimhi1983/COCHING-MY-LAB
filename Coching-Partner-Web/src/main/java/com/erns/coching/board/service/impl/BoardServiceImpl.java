package com.erns.coching.board.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.erns.coching.board.domain.BoardSearchDTO;
import com.erns.coching.board.domain.BoardSetDTO;
import com.erns.coching.board.domain.BoardVO;
import com.erns.coching.board.mapper.BoardDAO;
import com.erns.coching.board.service.BoardService;
import com.erns.coching.common.Const;
import com.erns.coching.common.file.domain.FileSetDTO;
import com.erns.coching.common.file.repository.FileRepository;
import com.erns.coching.common.service.AbstractCochingApiService;

/**
*
* <p>게시물 관리 Service</p>
*
* @author Hunwoo Park
*
*/
@Service
@Transactional
public class BoardServiceImpl extends AbstractCochingApiService implements BoardService{

	@Autowired
	private BoardDAO dao;

	@Autowired
	private FileRepository fileRepo;

	@Override
	public List<Map<String, Object>> getList(BoardSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(BoardSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public BoardVO load(int boardSeq) {
		return dao.load(boardSeq);
	}

	@Override
	public Map<String, Object> loadDetail(BoardSearchDTO param) {
		return dao.loadDetail(param);
	}


	@Override
	public List<Map<String, Object>> getCmt(BoardSearchDTO param) {
		return dao.getCmt(param);
	}

	/**
	 * 게시글 등록
	 * @param param
	 * @param loginUser
	 * @param uploadFiles
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	@Override
	public int addBoard(BoardSetDTO paramDto, List<MultipartFile> uploadFiles) throws IllegalStateException, IOException {
		BoardVO boardVO = BoardVO.AddBoardBuilder()
			.fromDto(paramDto)
			.build();

		int retVal = insert(boardVO);
		if(retVal <= 0) {
			return retVal;
		}

		procAttachFiles(paramDto, uploadFiles, paramDto.getStrDelFileIds());

		return retVal;
	}

	/**
	 * 게시글 수정
	 * @param param
	 * @param loginUser
	 * @param uploadFiles
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	@Override
	public int updateBoard(BoardSetDTO paramDto, List<MultipartFile> uploadFiles) throws IllegalStateException, IOException {
		BoardVO param = BoardVO.SetBoardBuilder()
			.boardSeq(paramDto.getBoardSeq())
			.fromDto(paramDto)
			.build();

		int retVal = update(param);
		if(retVal <= 0 ){
			return retVal;
		}

		procAttachFiles(paramDto, uploadFiles, paramDto.getStrDelFileIds());
		return retVal;
	}

	@Override
	public int insert(BoardVO param) {
		return dao.insert(param);
	}

	@Override
	public int update(BoardVO param) {
		return dao.update(param);
	}

	@Override
	public int updateDelYn(BoardVO param) {
		return dao.updateDelYn(param);
	}

	@Override
	public int updateViewCnt(BoardVO param) {
		return dao.updateViewCnt(param);
	}

	@Override
	public int delete(long boardSeq) {
		return dao.delete(boardSeq);
	}

	protected final String getFileTypeCode() {
		return String.format("FT_%S", "BOARD");
	}

	/**
	 * 첨부파일 처리
	 * @param paramDto
	 * @param uploadFiles
	 * @param strDelFileIds
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	private List<Map<String, Object>> procAttachFiles(BoardSetDTO paramDto, List<MultipartFile> uploadFiles, String strDelFileIds) throws IllegalStateException, IOException {
		String fileTypeCd = getFileTypeCode();

		// 파일 추출 및 저장
		List<FileSetDTO> newFiles = new ArrayList<>();
		for(MultipartFile mFile : uploadFiles) {
			FileSetDTO fDto = fileRepo.storeFile(mFile, fileTypeCd);
			fDto.setRefSeq(paramDto.getBoardSeq());
			fDto.setRgtr(paramDto.getChnr());
			newFiles.add(fDto);
		}

		String[] delFileIds = !StringUtils.hasText(strDelFileIds) ? new String[0] : strDelFileIds.split(",");
		List<FileSetDTO> delFiles = new ArrayList<>();
		if(delFileIds.length > 0) {
			List<Map<String, Object>> delList = fileRepo.getList(fileTypeCd, paramDto.getBoardSeq(), delFileIds);
			for(Map<String, Object> delFile : delList) {

				FileSetDTO item = new FileSetDTO();
				item.setFileId(delFile.get("fileId").toString());
				item.setChnr(paramDto.getChnr());

				delFiles.add(item);
			}
		}

		//파일 추가 및 삭제
		List<Map<String, Object>> list = fileRepo.setFiles(newFiles, delFiles, Const.DELETE_PHYSICAL_FILE);

		return list;
	}
}
