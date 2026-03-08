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

import com.erns.coching.board.domain.BoardCommentSetDTO;
import com.erns.coching.board.domain.BoardCommentVO;
import com.erns.coching.board.domain.BoardInquirySearchDTO;
import com.erns.coching.board.domain.BoardInquirySetDTO;
import com.erns.coching.board.domain.BoardInquiryVO;
import com.erns.coching.board.domain.BoardSearchDTO;
import com.erns.coching.board.domain.BoardSetDTO;
import com.erns.coching.board.domain.BoardVO;
import com.erns.coching.board.mapper.BoardDAO;
import com.erns.coching.board.service.BoardService;
import com.erns.coching.common.Const;
import com.erns.coching.common.file.domain.FileSetDTO;
import com.erns.coching.common.file.repository.FileRepository;
import com.erns.coching.common.service.AbstractCochingApiService;
import com.erns.coching.common.type.NotificationType;
import com.erns.coching.notification.service.NotificationService;

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
	
	@Autowired
	private NotificationService notificationService;

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
		
		//등록일 때 처리
		if(0 == paramDto.getBoardSeq()){
			paramDto.setBoardSeq(boardVO.getBoardSeq());
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
	
	/**
	 * 코멘트 등록
	 * @param param
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	@Override
	public int addCmt(BoardCommentSetDTO paramDto) {
		
		BoardCommentVO boardCommentVo =  BoardCommentVO.AddBoardCommentBuilder()
				.fromDto(paramDto)
				.build();

		int retVal = dao.insertCmt(boardCommentVo);
		if(retVal <= 0) {
			return retVal;
		}

		/* 알림 등록
		 * 댓글 => 수신자: 글작성자
		 * 대댓글 => 수신자: 글작성자, 댓글작성자
		 */
		NotificationType notiType = NotificationType.SOURCING_CMT;
		
		String replyYn = paramDto.getReplyYn(); //대댓글여부
		long boardSeq = paramDto.getBoardSeq();
		String boardTitle = paramDto.getBoardTitle();
		String content = paramDto.getContent();
		long boardWriterSeq = paramDto.getBoardWriterSeq();
		long cmtWriterSeq = paramDto.getCmtWriterSeq();
		long rgtr = paramDto.getRgtr();
		
		if (rgtr != boardWriterSeq) { //수신자 = 글작성자
		    notificationService.insertNoti(notiType, boardSeq, boardWriterSeq, boardTitle, content, rgtr);
		}
		
		if ("Y".equals(replyYn) && rgtr != cmtWriterSeq) { //수신자 = 댓글작성자
		    notificationService.insertNoti(notiType, boardSeq, cmtWriterSeq, boardTitle, content, rgtr);
		}

		return retVal;
	}
	
	/**
	 * 코멘트 수정
	 * @param param
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	@Override
	public int updateCmt(BoardCommentSetDTO paramDto) {
		
		BoardCommentVO boardCommentVo =  BoardCommentVO.SetBoardCommentBuilder()
				.fromDto(paramDto)
				.build();

		int retVal = dao.updateCmt(boardCommentVo);
		if(retVal <= 0) {
			return retVal;
		}

		return retVal;
	}
	
	/**
	 * 코멘트 삭제
	 * @param param
	 * @return
	 */
	@Override
	public int updateCmtDelYn(BoardCommentVO param) {
		return dao.updateCmtDelYn(param);
	}
	
	/* 1:1 문의하기 */
	@Override
	public List<Map<String, Object>> getInqrList(BoardInquirySearchDTO param) {
		return dao.getInqrList(param);
	}

	@Override
	public int getInqrListCount(BoardInquirySearchDTO param) {
		return dao.getInqrListCount(param);
	}
	
	@Override
	public int addInqrBoard(BoardInquirySetDTO paramDto, List<MultipartFile> uploadFiles) throws IllegalStateException, IOException {
		BoardInquiryVO boardVO = BoardInquiryVO.AddBoardBuilder()
			.fromDto(paramDto)
			.build();
		
		int retVal = dao.insertInqr(boardVO);
		if(retVal <= 0) {
			return retVal;
		}
		
		//등록일 때 처리
		if(0 == paramDto.getBoardSeq()){
			paramDto.setBoardSeq(boardVO.getBoardSeq());
		}
		
		procAttachFilesInqr(paramDto, uploadFiles, paramDto.getStrDelFileIds());

		return retVal;
	}
	
	@Override
	public Map<String, Object> loadInqrDetail(BoardInquirySearchDTO param) {
		return dao.loadInqrDetail(param);
	}
	
	@Override
	public Map<String, Object> loadInqrReply(BoardInquirySearchDTO param) {
		return dao.loadInqrReply(param);
	}
	
	protected final String getInqrFileTypeCode() {
		return String.format("FT_%S", "BOARD_INQR");
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
	private List<Map<String, Object>> procAttachFilesInqr(BoardInquirySetDTO paramDto, List<MultipartFile> uploadFiles, String strDelFileIds) throws IllegalStateException, IOException {
		String fileTypeCd = getInqrFileTypeCode();

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
