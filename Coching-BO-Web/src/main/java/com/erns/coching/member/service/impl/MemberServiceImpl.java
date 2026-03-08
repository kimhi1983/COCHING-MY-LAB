package com.erns.coching.member.service.impl;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import com.erns.coching.common.Const;
import com.erns.coching.common.file.domain.FileSetDTO;
import com.erns.coching.common.file.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.member.domain.MemberSearchDTO;
import com.erns.coching.member.domain.MemberSetDTO;
import com.erns.coching.member.domain.MemberVO;
import com.erns.coching.member.mapper.MemberDAO;
import com.erns.coching.member.service.MemberService;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;

/**
 *
 * <p>회원 기본정보 Service</p>
 *
 * @author Hunwoo Park
 *
 */
@Slf4j
@Transactional
@Service
public class MemberServiceImpl implements MemberService {

	@Autowired
	private MemberDAO dao;

	@Autowired
	private FileRepository fileRepo;

	@Override
	public List<Map<String, Object>> getList(MemberSearchDTO param) {
		return dao.getMembList(param);
	}

	@Override
	public int getListCount(MemberSearchDTO param) {
		return dao.getMembListCount(param);
	}

	@Override
	public MemberVO loadMemb(MemberSearchDTO param) {
		return dao.loadMemb(param);
	}

	@Override
	public Map<String, Object> load(MemberSearchDTO param) {
		return dao.load(param);
	}

	@Override
	public int insert(MemberVO param) {
		return dao.insertMemb(param);
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
	public int updateUseYn(MemberVO memberVO) {
		return dao.updateUseYn(memberVO);
	}

	@Override
	public int getMembInfoListCount(){
		return dao.getMembInfoListCount();
	}

	@Override
	public List<Map<String, Object>> getMembInfoList(){
		return dao.getMembInfoList();
	}

	@Override
	public Map<String, Object> chkDuplicate(MemberVO param){
		return dao.chkDuplicate(param);
	}

	@Override
	public int updateMember(MemberSetDTO setDTO){
		MemberVO memberVO = MemberVO
				.UpdateMemberBuilder()
				.dto(setDTO)
				.build();

		return dao.update(memberVO);
	}

	@Override
	public Map<String, Object> updateMemberWithPswd(MemberSetDTO setDTO){
		Map<String, Object> map = new HashMap<>();

		MemberVO memberVO = MemberVO
				.UpdateMemberBuilder()
				.dto(setDTO)
				.build();
		update(memberVO);

		if(null != setDTO.getPswd()){
			//비밀번호 변경
			MemberVO pswdAdminVO = MemberVO
					.MemberUpdatePswdBuilder()
					.pswd(setDTO.getPswd())
					.pswdInitYn("N")
					.membSeq(setDTO.getMembSeq())
					.seq(setDTO.getRgtr())
					.build();
			updatePassword(pswdAdminVO);
		}

		MemberSearchDTO loadParam = MemberSearchDTO.builder()
				.membSeq(memberVO.getMembSeq())
				.build();

		map = load(loadParam);

		return map;
	}

	@Override
	public int updateMemberUseYn(MemberSetDTO setDTO){
		MemberVO memberVO = MemberVO
				.UpdateMemberUseYnBuilder()
				.membSeq(setDTO.getMembSeq())
				.useYn(setDTO.getUseYn())
				.chnr(setDTO.getChnr())
				.build();

		return dao.updateUseYn(memberVO);
	}

	@Override
	public int updateState(Collection<MemberVO> list) {
		int retCnt = 0;

		for(MemberVO param : list){
			retCnt += dao.updateState(param);
		}

		return retCnt;
	}

	@Override
	public int initPasswd(MemberSetDTO paramDto){

		String initPswd = "123456";

		MemberVO memberVO = MemberVO
				.MemberUpdatePswdBuilder()
				.membSeq(paramDto.getMembSeq())
				.pswd(initPswd)
				.pswdInitYn("Y")
				.build();

		int result = updatePassword(memberVO);

		return result;
	}

	@Override
	public List<MemberVO> selectProfileBatchList(int maxCnt){
		return dao.selectProfileBatchList(maxCnt);
	}

	@Override
	public void createProfileImg(int maxCnt){

		List<MemberVO> memberList = selectProfileBatchList(maxCnt);

		int sendCount = memberList.size();
		log.info("get batch profile member count : {}", sendCount);

		if(sendCount <= 0){
			return;
		}

		for(MemberVO member : memberList){
			try {
				String fileName = "profile_"+ member.getMembSeq() + ".png";
				String profileName = "002".equals(member.getMembType()) ? member.getPtnName() : member.getMembName();
				if("002".equals(member.getMembType())) {
					Pattern COMPANY_PATTERN = Pattern.compile(
							"(^|\\s|\\()?(주식회사|주|유|사)(\\s|\\)|$)?|㈜|㈔|(\\((주|유|사)\\))|((주|유|사)\\)$)|\\b(ltd|inc|co\\.?|corp|llc|gmbh)\\b",
							Pattern.CASE_INSENSITIVE
					);
					profileName = COMPANY_PATTERN.matcher(profileName).replaceAll("");
					profileName = profileName.replaceAll("\\s{2,}", " ").trim();
				}
				MultipartFile avatarFile = createAvatarMultipartFile(profileName, 100, 100, fileName);

				List<MultipartFile> fileList = new ArrayList<>();
				fileList.add(avatarFile);

				procAttachFiles(member, fileList, "PROFILE");

			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	protected final String getFileTypeCode(String fileType) {
		return String.format("FT_%S", fileType);
	}

	/**
	 * 첨부파일 처리
	 * @param paramDto
	 * @param uploadFiles
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public List<Map<String, Object>> procAttachFiles(MemberVO paramDto, List<MultipartFile> uploadFiles, String fileType) throws IllegalStateException, IOException {
		String fileTypeCd = getFileTypeCode(fileType);

		// 파일 추출 및 저장
		List<FileSetDTO> newFiles = new ArrayList<>();
		for(MultipartFile mFile : uploadFiles) {
			FileSetDTO fDto = fileRepo.storeFile(mFile, fileTypeCd);
			fDto.setRefSeq(paramDto.getMembSeq());
			fDto.setRgtr(paramDto.getMembSeq());
			newFiles.add(fDto);
		}

		List<Map<String, Object>> list = null;
		//파일 추가 삭제
		list = fileRepo.setFiles(newFiles, null, Const.DELETE_PHYSICAL_FILE);

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
		}catch(Exception e){
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
