package com.erns.coching.member.service.impl;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.erns.coching.common.Const;
import com.erns.coching.common.file.domain.FileSetDTO;
import com.erns.coching.common.file.domain.FileVO;
import com.erns.coching.common.file.repository.FileRepository;
import com.erns.coching.common.file.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.common.service.AbstractCochingApiService;
import com.erns.coching.member.domain.MemberSearchDTO;
import com.erns.coching.member.domain.MemberSetDTO;
import com.erns.coching.member.domain.MemberTermsAgreementHistoryVO;
import com.erns.coching.member.domain.MemberVO;
import com.erns.coching.member.domain.MemberWithdrawInfoVO;
import com.erns.coching.member.mapper.MemberDAO;
import com.erns.coching.member.mapper.MemberWithdrawInfoDAO;
import com.erns.coching.member.service.MemberService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;


/**
*
* <p>회원 Service</p>
*
* @author Kyungmin Lee
*
*/
@Slf4j
@Transactional
@Service
public class MemberServiceImpl extends AbstractCochingApiService implements MemberService {

	@Autowired
	private MemberDAO dao;

	@Autowired
	private MemberWithdrawInfoDAO withdrawInfoDao;

	@Autowired
	private FileRepository fileRepo;

	@Autowired
	private FileService fileService;

	@Override
	public List<Map<String, Object>> getList(MemberSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(MemberSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public MemberVO load(MemberVO param) {
		return dao.load(param);
	}

	@Override
	public int insert(MemberVO param) {
		return dao.insert(param);
	}

	@Override
	public int update(MemberVO param) {
		return dao.update(param);
	}

	@Override
	public int updatePassword(MemberVO param) {
		return dao.updatePassword(param);
	}

	@Override
	public int delete(long membSeq) {
		return dao.delete(membSeq);
	}

	@Override
	public Map<String, Object> loadMyInfo(long membSeq) {
		return dao.loadMyInfo(membSeq);
	}

	@Override
	public int withdraw(MemberWithdrawInfoVO param) {
		// 1.탈회정보 입력
		int retVal = withdrawInfoDao.insert(param);

		// 3.사용자 프로필에 탈회정보 기록
		MemberVO withdrawParam = MemberVO.MemberWithdrawBuilder()
				.seq(param.getMembSeq())
				.wtdrYn("Y") //탈회여부
				.build();
		dao.updateWithdrawYn(withdrawParam);

		//TODO : 탈회 처리


		return retVal;
	}

	@Override
	public MemberVO loadPassword(MemberSearchDTO searchDTO) {
		return dao.loadPassword(searchDTO);
	}

	@Override
	public int insertAgreementHistory(MemberTermsAgreementHistoryVO param){
		return dao.insertAgreementHistory(param);
	}

//	@Override
//	public List<Map<String, Object>> getEmailList() {
//		return dao.getEmailList();
//	}

	@Override
	public int updatePwChngDttm(MemberSetDTO param) {
		return dao.updatePwChngDttm(param);
	}

//	@Override
//	public int updateWithdrawYn(MemberVO param) {
//		return dao.updateWithdrawYn(param);
//	}

//	@Override
//	public int updateDormantYn(MemberVO param) {
//		return dao.updateDormantYn(param);
//	}


	@Override
	public Map<String, Object> chkDuplicate(MemberVO param){
		return dao.chkDuplicate(param);
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
	@Override
	public List<Map<String, Object>> procAttachFiles(MemberSetDTO paramDto, List<MultipartFile> uploadFiles, String strDelFileIds, boolean isPreview) throws IllegalStateException, IOException {
		String fileTypeCd = getFileTypeCode(paramDto.getFileType());

		// 파일 추출 및 저장
		List<FileSetDTO> newFiles = new ArrayList<>();
		for(MultipartFile mFile : uploadFiles) {
			FileSetDTO fDto = fileRepo.storeFile(mFile, fileTypeCd);
			fDto.setRefSeq(paramDto.getMembSeq());
			fDto.setRgtr(paramDto.getMembSeq());
			newFiles.add(fDto);
		}

		fileRepo.updateDelYnByRefId(fileTypeCd, paramDto.getMembSeq(), "Y", paramDto.getMembSeq());
		String[] delFileIds = !StringUtils.hasText(strDelFileIds) ? new String[0] : strDelFileIds.split(",");
		List<FileSetDTO> delFiles = new ArrayList<>();
		if(delFileIds.length > 0) {
			List<Map<String, Object>> delList = fileRepo.getList(fileTypeCd, paramDto.getMembSeq(), delFileIds);
			for(Map<String, Object> delFile : delList) {

				FileSetDTO item = new FileSetDTO();
				item.setFileId(delFile.get("fileId").toString());
				item.setChnr(paramDto.getMembSeq());

				delFiles.add(item);
			}
		}
		List<Map<String, Object>> list = null;
		//파일 추가 삭제
		list = fileRepo.setFiles(newFiles, delFiles, Const.DELETE_PHYSICAL_FILE);

		return list;
	}

	/**
	 * 이름을 받아 BufferedImage 아바타를 만들고, MultipartFile로 변환
	 *
	 * @param name      사용자 이름 (예: "홍길동")
	 * @param width     아바타 너비
	 * @param height    아바타 높이
	 * @param fileName  파일 이름 (예: "avatar.png")
	 * @return MultipartFile (MockMultipartFile 형태)
	 */
	public MultipartFile createAvatarMultipartFile(String name, int width, int height, String fileName) throws Exception {
		// 1. BufferedImage 생성
		BufferedImage image = generateAvatar(name, width, height);

		// 2. ByteArrayOutputStream 에 이미지 쓰기 (PNG)
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "png", baos);
		baos.flush();

		byte[] imageBytes = baos.toByteArray();
		baos.close();

		// 3. MultipartFile 변환 (Spring 테스트용 클래스 사용)
		return new MockMultipartFile(
				"file",             // form 필드 이름
				fileName,           // 클라이언트 파일 이름
				"image/png",        // MIME 타입
				imageBytes          // 파일 데이터
		);
	}

	public static BufferedImage generateAvatar(String name, int width, int height) {
		// 배경+글자색 세트 중 하나 선택
		AvatarColorSet colorSet = getRandomColorSet();

		String initial = name != null && name.length() > 0
				? name.substring(0, 2)
				: "?";

		// 이미지 및 그래픽 객체 생성
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = image.createGraphics();

		try {
			// 안티앨리어싱
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			// 배경
			g2d.setColor(colorSet.background);
			g2d.fillRect(0, 0, width, height);

			// 이니셜 텍스트
			g2d.setColor(colorSet.text);

			int fontSize = (int) (width * 0.4);
			InputStream fontStream = MemberServiceImpl.class.getClassLoader().getResourceAsStream("/fonts/NanumGothicBold.ttf");

			if (fontStream == null) {
				throw new RuntimeException("폰트 파일을 찾을 수 없습니다.");
			}

			Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(Font.BOLD, fontSize);
			g2d.setFont(font);

			FontMetrics fm = g2d.getFontMetrics();
			int x = (width - fm.stringWidth(initial)) / 2;
			int y = ((height - fm.getHeight()) / 2) + fm.getAscent();

			g2d.drawString(initial, x, y);
			g2d.dispose();
		}catch (Exception e){
			e.printStackTrace();
		}
		return image;
	}

	// 🎨 배경+텍스트 색상 세트 정의
	private static final List<AvatarColorSet> COLOR_SETS = new ArrayList<>();
	static {
		COLOR_SETS.add(new AvatarColorSet(new Color(255, 226, 231), new Color(255, 56, 92))); // Light pink
		COLOR_SETS.add(new AvatarColorSet(new Color(210, 234, 239), new Color(98, 148, 159))); // Light Sky Blue
		COLOR_SETS.add(new AvatarColorSet(new Color(189, 120, 234), Color.white)); // Purple
		COLOR_SETS.add(new AvatarColorSet(new Color(253, 147, 43), Color.white)); // Orange
	}

	private static AvatarColorSet getRandomColorSet() {
		Random random = new Random();
		return COLOR_SETS.get(random.nextInt(COLOR_SETS.size()));
	}

	// 🎯 배경 + 텍스트 색상 세트 클래스
	public static class AvatarColorSet {
		public final Color background;
		public final Color text;

		public AvatarColorSet(Color background, Color text) {
			this.background = background;
			this.text = text;
		}
	}
}
