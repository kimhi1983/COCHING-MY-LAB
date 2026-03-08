package com.erns.coching.board.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.erns.coching.board.domain.BoardCommentSetDTO;
import com.erns.coching.board.domain.BoardCommentVO;
import com.erns.coching.board.domain.BoardInquirySearchDTO;
import com.erns.coching.board.domain.BoardInquirySetDTO;
import com.erns.coching.board.domain.BoardSearchDTO;
import com.erns.coching.board.domain.BoardSetDTO;
import com.erns.coching.board.domain.BoardVO;

/**
*
* <p>게시물 관리 Service</p>
*
* @author Hunwoo Park
*
*/
public interface BoardService {

	public List<Map<String, Object>> getList(BoardSearchDTO param);
	public int getListCount(BoardSearchDTO param);
	public Map<String, Object> loadDetail(BoardSearchDTO param);
	public BoardVO load(int boardSeq);
	
	public int addBoard(BoardSetDTO setDTO, List<MultipartFile> uploadFiles) throws IllegalStateException, IOException;
	public int updateBoard(BoardSetDTO setDTO, List<MultipartFile> uploadFiles) throws IllegalStateException, IOException;
	public int insert(BoardVO param);
	public int update(BoardVO param);
	public int updateDelYn(BoardVO param);
	public int updateViewCnt(BoardVO param);
	
	public int delete(long boardSeq);
	
	//코멘트
	public List<Map<String, Object>> getCmt(BoardSearchDTO param);
	public int addCmt(BoardCommentSetDTO setDTO);
	public int updateCmt(BoardCommentSetDTO setDTO);
	public int updateCmtDelYn(BoardCommentVO param);
	
	//1:1 문의
	public List<Map<String, Object>> getInqrList(BoardInquirySearchDTO param);
	public int getInqrListCount(BoardInquirySearchDTO param);
	public int addInqrBoard(BoardInquirySetDTO setDTO, List<MultipartFile> uploadFiles) throws IllegalStateException, IOException;
	public Map<String, Object> loadInqrDetail(BoardInquirySearchDTO param);
	public Map<String, Object> loadInqrReply(BoardInquirySearchDTO param);
}

