package com.erns.coching.board.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.erns.coching.board.domain.BoardCommentSetDTO;
import com.erns.coching.board.domain.BoardCommentVO;
import com.erns.coching.board.domain.BoardInquirySearchDTO;
import com.erns.coching.board.domain.BoardInquirySetDTO;
import com.erns.coching.board.domain.BoardSearchDTO;
import com.erns.coching.board.domain.BoardSetDTO;
import com.erns.coching.board.domain.BoardVO;
import com.erns.coching.board.domain.vg.ValidBoard0011;
import com.erns.coching.board.domain.vg.ValidBoard0012;
import com.erns.coching.board.domain.vg.ValidBoard0015;
import com.erns.coching.board.domain.vg.ValidBoard0016;
import com.erns.coching.board.domain.vg.ValidBoard0017;
import com.erns.coching.board.domain.vg.ValidBoard0020;
import com.erns.coching.board.domain.vg.ValidBoard0021;
import com.erns.coching.board.domain.vg.ValidBoard0030;
import com.erns.coching.board.domain.vg.ValidBoard0040;
import com.erns.coching.board.domain.vg.ValidBoardComment0011;
import com.erns.coching.board.domain.vg.ValidBoardComment0020;
import com.erns.coching.board.domain.vg.ValidBoardComment0030;
import com.erns.coching.board.domain.vg.ValidBoardComment0040;
import com.erns.coching.board.domain.vg.ValidBoardMst0012;
import com.erns.coching.board.service.BoardMasterService;
import com.erns.coching.board.service.BoardService;
import com.erns.coching.common.aop.ApiLogging;
import com.erns.coching.common.controller.AbstractApiController;
import com.erns.coching.common.file.repository.FileRepository;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.model.PageInfo;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.login.domain.LoginUserDTO;

import lombok.extern.slf4j.Slf4j;

/**
*
* <p>게시판관리 API Controller</p>
*
* @author Hunwoo Park
*
*/
@Slf4j
@RestController
@RequestMapping("/api/board")
@PreAuthorize("permitAll")
public class BoardApiController extends AbstractApiController {

	@Autowired
	protected BoardMasterService boardMasterService;

	@Autowired
	protected BoardService boardService;

	public static final String FILE_INPUT_NAME = "board_files";

	@Autowired
	private FileRepository fileRepo;

	/**
	 * 게시판마스터 상세
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.BOARD_MST_GET)
	@PostMapping("/mst/get.api")
	public ApiResult getBoardMaster(
			HttpServletRequest request,
			@RequestBody @Validated(ValidBoardMst0012.class) BoardSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT, getLocale());
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		Map<String, Object> boardMaster = boardMasterService.load(param);
		axRet.setResultData(boardMaster)
			.set(ApiResultError.NO_ERROR, getLocale());
		return axRet;
	}

	/**
	 * 게시물 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.BOARD_LIST)
	@PostMapping("/list.api")
	public ApiResult getBoardArticleList(
            HttpServletRequest request,
            @RequestBody @Validated(ValidBoard0011.class) BoardSearchDTO param,
            Errors errors) {

        ApiResult axRet = new ApiResult();
        if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		PageInfo pi = new PageInfo(0, param);
		int totalItem = boardService.getListCount(param);
		List<Map<String, Object>> list = null;
		if(totalItem <= 0) {
			list = new ArrayList<>();
		}else {
			list = boardService.getList(param);
		}
        pi.setCurrentPage(totalItem, param.getPage());
		axRet.setSc(param)
			.setPageInfo(pi)
			.setList(list)
			.set(ApiResultError.NO_ERROR, getLocale());

        return axRet;
	}

	/**
	 * 게시물 상세
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.BOARD_GET)
	@PostMapping("/get.api")
	public ApiResult getBoardArticle(
			HttpServletRequest request,
			@RequestBody @Validated(ValidBoard0012.class) BoardSearchDTO param,
			Errors errors) {
		
		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}
		
		//조회수 증가
		BoardVO boardVO = BoardVO.UpdateViewBuilder()
				.boardSeq(param.getBoardSeq())
				.build();

		boardService.updateViewCnt(boardVO);
		

		//API Call
		Map<String, Object> data = boardService.loadDetail(param); //게시글 정보
		if(null == data){
			return axRet;
		}

		//파일 정보
		long boardSeq = ((Number) data.get("boardSeq")).longValue();
		List<Map<String, Object>> fileList = fileRepo.getList("FT_BOARD", boardSeq, null);
		if(null != fileList){
			data.put("fileList", fileList);
		}
		axRet.setSc(param)
			.setResultData(data)
			.set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * 게시글 등록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.BOARD_ADD)
	@PostMapping("/set.api")
	@PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_COCHING_USER')")
	public ApiResult setBoard(
			HttpServletRequest request,
			@ModelAttribute @Validated(ValidBoard0020.class) BoardSetDTO param,
			Errors errors) {		
		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		LoginUserDTO loginUser = getLoginedUserObject();

		try {
			// 파일 등록
			fileRepo.printFilesInfo(request);
			List<MultipartFile> getUploadFiles = fileRepo.getUploadFiles(request, FILE_INPUT_NAME);
			log.debug("Files:{}", getUploadFiles.size());

			param.setRgtr(loginUser.getSeq()); //등록자
			param.setChnr(loginUser.getSeq()); //수정자
			param.setWriter(loginUser.getUserName()); //작성자
			int ret = boardService.addBoard(param, getUploadFiles);
			if(ret > 0) {
				axRet.set(ApiResultError.NO_ERROR, getLocale()).setResultData(ret);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return axRet.set(ApiResultError.ATTACH_ERROR, getLocale());
		}


		return axRet;
	}

	/**
	 * <p>게시글 수정</p>
	 * @param request
	 * @param setDTO
	 * @param errors 파라미터 바인딩 오류 내역
	 * @return
	 */
	@ApiLogging(logType = UserLogType.BOARD_SET)
	@PostMapping("/update.api")
	@PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_COCHING_USER')")
	public ApiResult updateBoard(
			HttpServletRequest request,
			@ModelAttribute @Validated(ValidBoard0030.class) BoardSetDTO param, 
			Errors errors)  {
		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		LoginUserDTO loginUser = getLoginedUserObject();

		try {
			// 파일 등록
			fileRepo.printFilesInfo(request);
			List<MultipartFile> getUploadFiles = fileRepo.getUploadFiles(request, FILE_INPUT_NAME);
			log.debug("Files:{}", getUploadFiles.size());

			param.setChnr(loginUser.getSeq()); //수정자
			param.setWriter(loginUser.getUserName()); //작성자
			int ret = boardService.updateBoard(param, getUploadFiles);
			if(ret > 0) {
				axRet.set(ApiResultError.NO_ERROR, getLocale()).setResultData(ret);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return axRet.set(ApiResultError.ATTACH_ERROR, getLocale());
		}

		return axRet.set(ApiResultError.NO_ERROR, getLocale());
	}
	
	/**
	 * <p>게시글 삭제</p>
	 * @param request
	 * @param setDTO
	 * @param errors 파라미터 바인딩 오류 내역
	 * @return
	 */
	@ApiLogging(logType = UserLogType.BOARD_DEL)
	@PostMapping("/delete.api")
	public ApiResult deleteBoard(
			HttpServletRequest request,
			@RequestBody @Validated(ValidBoard0040.class) BoardSetDTO param, 
			Errors errors)  {
		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		LoginUserDTO loginUser = getLoginedUserObject();
		
		BoardVO boardVO = BoardVO.UpdateDelYnBuilder()
				.boardSeq(param.getBoardSeq())
				.delYn(param.getDelYn())
				.updateUser(loginUser)
				.build();

		try {
			int ret = boardService.updateDelYn(boardVO);
			if(ret > 0) {
				axRet.set(ApiResultError.NO_ERROR, getLocale()).setResultData(ret);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return axRet.set(ApiResultError.ATTACH_ERROR, getLocale());
		}

		return axRet.set(ApiResultError.NO_ERROR, getLocale());
	}
	
	/**
	 * 게시물 코멘트 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.BOARD_COMMENT_LIST)
	@PostMapping("/get/cmt.api")
	public ApiResult getBoardArticleCmt(
			HttpServletRequest request,
			@RequestBody @Validated(ValidBoardComment0011.class) BoardSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		List<Map<String, Object>> cmt = boardService.getCmt(param);
		axRet.setSc(param)
			.setList(cmt)
			.set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}
	

	/**
	 * 게시물 코멘트 등록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.BOARD_COMMENT_ADD)
	@PostMapping("/set/cmt.api")
	public ApiResult setBoardArticleCmt(
			HttpServletRequest request,
			@RequestBody @Validated(ValidBoardComment0020.class) BoardCommentSetDTO param,
			Errors errors) {
		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}
		
		//API Call
		LoginUserDTO loginUser = getLoginedUserObject();
		
		try {
			param.setRgtr(loginUser.getSeq()); //등록자
			param.setChnr(loginUser.getSeq()); //수정자
			param.setWriter(loginUser.getUserName()); //작성자
			int ret = boardService.addCmt(param);
			if(ret > 0) {
				axRet.set(ApiResultError.NO_ERROR, getLocale()).setResultData(ret);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return axRet.set(ApiResultError.ATTACH_ERROR, getLocale());
		}

		return axRet;
	}
	
	/**
	 * 게시물 코멘트 수정
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.BOARD_COMMENT_SET)
	@PostMapping("/update/cmt.api")
	public ApiResult updateBoardArticleCmt(
			HttpServletRequest request,
			@RequestBody @Validated(ValidBoardComment0030.class) BoardCommentSetDTO param,
			Errors errors) {
		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}
		
		//API Call
		LoginUserDTO loginUser = getLoginedUserObject();
		
		try {
			param.setRgtr(loginUser.getSeq()); //등록자
			param.setChnr(loginUser.getSeq()); //수정자
			param.setWriter(loginUser.getUserName()); //작성자
			int ret = boardService.updateCmt(param);
			if(ret > 0) {
				axRet.set(ApiResultError.NO_ERROR, getLocale()).setResultData(ret);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return axRet.set(ApiResultError.ATTACH_ERROR, getLocale());
		}

		return axRet;
	}
	
	/**
	 * 게시물 코멘트 삭제
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.BOARD_COMMENT_DEL)
	@PostMapping("/delete/cmt.api")
	public ApiResult deleteBoardArticleCmt(
			HttpServletRequest request,
			@RequestBody @Validated(ValidBoardComment0040.class) BoardCommentSetDTO param,
			Errors errors) {
		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}
		
		//API Call
		LoginUserDTO loginUser = getLoginedUserObject();
		
		BoardCommentVO boardCommentVO = BoardCommentVO.UpdateDelYnBuilder()
				.boardCmtSeq(param.getBoardCmtSeq())
				.delYn(param.getDelYn())
				.updateUser(loginUser)
				.build();
		
		try {
			int ret = boardService.updateCmtDelYn(boardCommentVO);
			if(ret > 0) {
				axRet.set(ApiResultError.NO_ERROR, getLocale()).setResultData(ret);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return axRet.set(ApiResultError.ATTACH_ERROR, getLocale());
		}

		return axRet;
	}

	/**
	 * 나의 게시물 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.BOARD_MY_LIST)
	@PostMapping("/my/list.api")
	@PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_COCHING_USER')")
	public ApiResult getMyBoardArticleList(
		HttpServletRequest request,
		@RequestBody @Validated(ValidBoard0015.class) BoardSearchDTO param,
		Errors errors) {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		LoginUserDTO loginUser = getLoginedUserObject();
		param.setRgtr(loginUser.getSeq());
		param.setMyBoard(true);

		//API Call
		PageInfo pi = new PageInfo(0, param);
		int totalItem = boardService.getListCount(param);
		List<Map<String, Object>> list = null;
		if(totalItem <= 0) {
			list = new ArrayList<>();
		}else {
			list = boardService.getList(param);
		}
		pi.setCurrentPage(totalItem, param.getPage());
		axRet.setSc(param)
			.setPageInfo(pi)
			.setList(list)
			.set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}
	
	/**
	 * 1:1 문의 게시물 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.BOARD_INQR_LIST)
	@PostMapping("/inqr/list.api")
	@PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_COCHING_USER')")
	public ApiResult getInqrBoardList(
		HttpServletRequest request,
		@RequestBody @Validated(ValidBoard0016.class) BoardInquirySearchDTO param,
		Errors errors) {

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		LoginUserDTO loginUser = getLoginedUserObject();
		param.setRgtr(loginUser.getSeq());
		
		//API Call
		PageInfo pi = new PageInfo(0, param);
		int totalItem = boardService.getInqrListCount(param);
		List<Map<String, Object>> list = null;
		if(totalItem <= 0) {
			list = new ArrayList<>();
		}else {
			list = boardService.getInqrList(param);
		}
		pi.setCurrentPage(totalItem, param.getPage());
		axRet.setSc(param)
			.setPageInfo(pi)
			.setList(list)
			.set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}
	
	
	/**
	 * 1:1 문의 등록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.BOARD_INQR_ADD)
	@PostMapping("/inqr/new.api")
	@PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_COCHING_USER')")
	public ApiResult insertInqrBoard(
			HttpServletRequest request,
			@ModelAttribute @Validated(ValidBoard0021.class) BoardInquirySetDTO param,
			Errors errors) {		
		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		LoginUserDTO loginUser = getLoginedUserObject();

		try {
			// 파일 등록
			fileRepo.printFilesInfo(request);
			List<MultipartFile> getUploadFiles = fileRepo.getUploadFiles(request, FILE_INPUT_NAME);
			log.debug("Files:{}", getUploadFiles.size());

			param.setRgtr(loginUser.getSeq()); //등록자
			param.setChnr(loginUser.getSeq()); //수정자
			param.setWriter(loginUser.getUserName()); //작성자
			int ret = boardService.addInqrBoard(param, getUploadFiles);
			if(ret > 0) {
				axRet.set(ApiResultError.NO_ERROR, getLocale()).setResultData(ret);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return axRet.set(ApiResultError.ATTACH_ERROR, getLocale());
		}


		return axRet;
	}
	
	/**
	 * 1:1 문의 게시물 상세
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.BOARD_INQR_GET)
	@PostMapping("/inqr/get.api")
	public ApiResult getInqrBoardArticle(
			HttpServletRequest request,
			@RequestBody @Validated(ValidBoard0017.class) BoardInquirySearchDTO param,
			Errors errors) {
		
		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		Map<String, Object> data = boardService.loadInqrDetail(param); //게시글 정보
		if(null == data){
			return axRet;
		}

		//파일 정보
		long boardSeq = ((Number) data.get("boardSeq")).longValue();
		List<Map<String, Object>> fileList = fileRepo.getList("FT_BOARD_INQR", boardSeq, null);
		if(null != fileList){
			data.put("fileList", fileList);
		}
		axRet.setSc(param)
			.setResultData(data)
			.set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}
	
	/**
	 * 1:1 문의 게시물 답변
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.BOARD_INQR_GET)
	@PostMapping("/inqr/reply.api")
	public ApiResult getInqrBoardReply(
			HttpServletRequest request,
			@RequestBody @Validated() BoardInquirySearchDTO param,
			Errors errors) {
		
		log.debug("param : {}", param);

		ApiResult axRet = new ApiResult();
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		Map<String, Object> data = boardService.loadInqrReply(param); //게시글 정보
		if(null != data){
			//파일 정보
			long boardSeq = ((Number) data.get("boardSeq")).longValue();
			List<Map<String, Object>> fileList = fileRepo.getList("FT_BOARD_INQR", boardSeq, null);
			if(null != fileList){
				data.put("fileList", fileList);
			}
		}

		axRet.setSc(param)
			.setResultData(data)
			.set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}
	
}
