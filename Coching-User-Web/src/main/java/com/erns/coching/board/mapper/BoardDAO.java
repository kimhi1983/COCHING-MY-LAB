package com.erns.coching.board.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erns.coching.board.domain.BoardCommentVO;
import com.erns.coching.board.domain.BoardInquirySearchDTO;
import com.erns.coching.board.domain.BoardInquiryVO;
import com.erns.coching.board.domain.BoardSearchDTO;
import com.erns.coching.board.domain.BoardVO;

/**
 *
 * <p>게시물 DAO Mapper</p>
 *
 * @author Hunwoo Park
 *
 */
@Mapper
public interface BoardDAO {

	public List<Map<String, Object>> getList(BoardSearchDTO param);
	public int getListCount(BoardSearchDTO param);
	public BoardVO load(int boardSeq);
	public Map<String, Object> loadDetail(BoardSearchDTO param);
	
	public int insert(BoardVO param);
	public int update(BoardVO param);
	public int updateDelYn(BoardVO param);
	public int updateViewCnt(BoardVO param);
	public int delete(long boardSeq);
	
	//코멘트
	public List<Map<String, Object>> getCmt(BoardSearchDTO param);
	public int insertCmt(BoardCommentVO param);
	public int updateCmt(BoardCommentVO param);
	public int updateCmtDelYn(BoardCommentVO param);

	//1:1 문의하기
	public List<Map<String, Object>> getInqrList(BoardInquirySearchDTO param);
	public int getInqrListCount(BoardInquirySearchDTO param);
	
	public int insertInqr(BoardInquiryVO param);
	public Map<String, Object> loadInqrDetail(BoardInquirySearchDTO param);
	public Map<String, Object> loadInqrReply(BoardInquirySearchDTO param);
}
