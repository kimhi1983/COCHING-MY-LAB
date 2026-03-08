package com.erns.coching.board.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.erns.coching.member.domain.MemberSearchDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

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
import com.erns.coching.common.type.LocaleType;
import com.erns.coching.common.type.MailType;
import com.erns.coching.common.type.NotificationType;
import com.erns.coching.common.type.ShortUrlType;
import com.erns.coching.mail.service.MailService;
import com.erns.coching.member.domain.MemberVO;
import com.erns.coching.member.service.MemberService;
import com.erns.coching.notification.service.NotificationService;
import com.erns.coching.url.domain.ShortUrlVO;
import com.erns.coching.url.service.ShortUrlService;

import lombok.extern.slf4j.Slf4j;

/**
*
* <p>게시물 관리 Service</p>
*
* @author Hunwoo Park
*
*/
@Slf4j
@Service
@Transactional
public class BoardServiceImpl extends AbstractCochingApiService implements BoardService{

	@Autowired
	private BoardDAO dao;

	@Autowired
	private FileRepository fileRepo;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private MemberService memberService;

	@Autowired
	private ShortUrlService shortUrlService;

	@Autowired
	private MailService mailService;


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
	public int updateDelYn(Collection<BoardVO> list) {
		int retCnt = 0;

		for(BoardVO param : list){
			retCnt += dao.updateDelYn(param);
		}

		return retCnt;
	}

	@Override
	public int updateViewCnt(BoardVO param) {
		return dao.updateViewCnt(param);
	}

	@Override
	public int delete(long boardSeq) {
		return dao.delete(boardSeq);
	}

	protected final String getFileTypeCode(String fileType) {
		return String.format("FT_%S", fileType);
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
	public List<Map<String, Object>> procAttachFiles(BoardSetDTO paramDto, List<MultipartFile> uploadFiles, String strDelFileIds) throws IllegalStateException, IOException {
		String fileTypeCd = getFileTypeCode(paramDto.getFileType());

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


		//수신자 정보
		long rcvBoardSeq = paramDto.getOrgSeq();
		BoardInquirySearchDTO dto = new BoardInquirySearchDTO();
		dto.setBoardSeq(rcvBoardSeq);
		Map<String, Object> receiver = dao.loadInqrDetail(dto);
		String rcvTitle = (String) receiver .get("title");
		String rcvEmail = (String) receiver .get("email");
		long rcvRgtr = ((Number) receiver.getOrDefault("rgtr", 0)).longValue();

		//발신자 정보
		long sendRgtr = paramDto.getRgtr();

		//알림 등록
		NotificationType notiType = NotificationType.INQUIRY_SEND;
		notificationService.insertNoti(notiType,
				rcvBoardSeq, rcvRgtr, null, rcvTitle, sendRgtr);

		{    //메일보내기
			//요청자 정보
			MemberSearchDTO search = new MemberSearchDTO();
			search.setMembSeq(rcvRgtr);
			MemberVO membInfo = memberService.loadMemb(search);
			String checkToken = UUID.randomUUID().toString();

			//Short URL 생성
			Map<String, Object> urlParam = new HashMap<String, Object>();
			urlParam.put("boardSeq", rcvBoardSeq);

			ShortUrlVO suvo = ShortUrlVO.ShortUrlTypeBuilder()
					.urlType(ShortUrlType.VIEW_INQUIRY)
					.token(checkToken) //check token
					.parameters(urlParam)
					.build();
			shortUrlService.insert(suvo);

			log.debug("suvo:{}", suvo);
			String relationNo = String.valueOf(rcvBoardSeq);
			MailType mailType = MailType.SEND_INQUIRY;
			LocaleType locale = LocaleType.KO;
			Map<String, Object> mailData = new HashMap<String, Object>();
			mailData.put("buttonLink", suvo.getRequestUrl());
			mailData.put("buttonName", "문의내용 바로가기");

			mailService.sendTemplateMail(
					rcvEmail, membInfo.getMembSeq(),
					mailType, locale, null, 0, mailData,
					null, relationNo, null);
		}

		return retVal;
	}

	@Override
	public int updateInqrBoard(BoardInquirySetDTO paramDto, List<MultipartFile> uploadFiles) throws IllegalStateException, IOException {
		BoardInquiryVO boardVO = BoardInquiryVO.SetBoardBuilder()
				.boardSeq(paramDto.getBoardSeq())
				.fromDto(paramDto)
				.build();

		int retVal = dao.updateInqr(boardVO);
		if(retVal <= 0 ){
			return retVal;
		}

		procAttachFilesInqr(paramDto, uploadFiles, paramDto.getStrDelFileIds());
		return retVal;
	}

	@Override
	public int updateInqrDelYn(BoardInquiryVO param) {
		return dao.updateInqrDelYn(param);
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
