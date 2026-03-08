package com.erns.coching.raw.service.impl;

import com.erns.coching.common.Const;
import com.erns.coching.common.file.domain.FileSetDTO;
import com.erns.coching.common.file.repository.FileRepository;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.service.AbstractCochingApiService;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.LocaleType;
import com.erns.coching.common.type.MailType;
import com.erns.coching.common.type.NotificationType;
import com.erns.coching.common.type.ShortUrlType;
import com.erns.coching.mail.service.MailService;
import com.erns.coching.member.domain.MemberVO;
import com.erns.coching.member.service.MemberService;
import com.erns.coching.notification.service.NotificationService;
import com.erns.coching.raw.domain.RawDetailSearchDTO;
import com.erns.coching.raw.domain.RawDetailSetDTO;
import com.erns.coching.raw.domain.RawDetailVO;
import com.erns.coching.raw.domain.RawMasterSearchDTO;
import com.erns.coching.raw.domain.RawMasterVO;
import com.erns.coching.raw.domain.RawRequestReplySetDTO;
import com.erns.coching.raw.domain.RawRequestReplyVO;
import com.erns.coching.raw.domain.RawRequestSearchDTO;
import com.erns.coching.raw.domain.RawRequestSetDTO;
import com.erns.coching.raw.domain.RawRequestTypeSetDTO;
import com.erns.coching.raw.domain.RawRequestTypeVO;
import com.erns.coching.raw.domain.RawRequestVO;
import com.erns.coching.raw.mapper.RawDAO;
import com.erns.coching.raw.service.RawService;
import com.erns.coching.url.domain.ShortUrlVO;
import com.erns.coching.url.service.ShortUrlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 *
 * <p>원료 Service</p>
 *
 * @author Kyungmin Lee
 *
 */
@Slf4j
@Transactional
@Service
public class RawServiceImpl extends AbstractCochingApiService implements RawService {

	@Autowired
	private RawDAO dao;

	@Autowired
	private FileRepository fileRepo;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private ShortUrlService shortUrlService;

	@Autowired
	private MailService mailService;

	@Autowired
	private MemberService memberService;

	@Override
	public List<Map<String, Object>> getList(RawMasterSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(RawMasterSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public Map<String, Object> load(RawMasterSearchDTO param) {
		return dao.load(param);
	}

	@Override
	public int insert(RawMasterVO param) {
		return dao.insert(param);
	}

	@Override
	public int update(RawMasterVO param) {
		return dao.update(param);
	}

	@Override
	public int updateUseYn(RawMasterVO param) {
		return dao.updateUseYn(param);
	}

	@Override
	public int updateDelYn(RawMasterVO param) {
		return dao.updateDelYn(param);
	}

	@Override
	public int updateRequestCnt(RawDetailVO param) {
		return dao.updateRequestCnt(param);
	}

	@Override
	public int delete(long rawSeq) {
		return dao.delete(rawSeq);
	}

	@Override
	public RawDetailVO loadDetail(RawDetailSearchDTO param){

		RawDetailVO retVal = dao.loadDetail(param);
		if(null != retVal) {
			dao.updateDetailViewCnt(retVal.getRawDetailSeq());
		}

		return retVal;
	}

	@Override
	public List<Map<String, Object>> getRequestList(RawRequestSearchDTO param) {
		return dao.getRequestList(param);
	}

	@Override
	public int getRequestListCount(RawRequestSearchDTO param) {
		return dao.getRequestListCount(param);
	}
	@Override
	public Map<String, Object> loadRequest(RawRequestSearchDTO param) {
		return dao.loadRequest(param);
	}
	@Override
	public List<Map<String, Object>> getRequestTypeList(RawRequestSearchDTO param) {
		return dao.getRequestTypeList(param);
	}
	@Override
	public List<Map<String, Object>> getRequestReplyList(RawRequestSearchDTO param) {
		return dao.getRequestReplyList(param);
	}
	@Override
	public int insertRequest(RawRequestVO param) {
		return dao.insertRequest(param);
	}

	@Override
	public int insertRequestType(RawRequestTypeVO param) {
		return dao.insertRequestType(param);
	}

	@Override
	public ApiResult addRequest(RawRequestSetDTO setDTO){
		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);

		log.debug("[REQUEST INFO] {}", setDTO.toString());

		try {
			List<RawDetailSetDTO> rawList = setDTO.getRawList();
			for(RawDetailSetDTO detail : rawList) {
				setDTO.setRawDetailSeq(detail.getRawDetailSeq());
				RawRequestVO requestVO = RawRequestVO
						.AddRequestBuilder()
						.fromDto(setDTO)
						.build();
				insertRequest(requestVO);

				//요정자료 INSERT
				List<RawRequestTypeSetDTO> typeList = setDTO.getTypeList();
				for (RawRequestTypeSetDTO type : typeList) {
					type.setRawRequestSeq(requestVO.getRawRequestSeq());
					RawRequestTypeVO typeVO = RawRequestTypeVO
							.AddRequestTypeBuilder()
							.fromDto(type)
							.seq(setDTO.getRgtr())
							.build();
					insertRequestType(typeVO);
				}

				//요청된 담당자 requestCnt 추가
				RawDetailVO detailVO = RawDetailVO
						.AddRequestCntBuilder()
						.rawSeq(detail.getRawSeq())
						.membSeq(detail.getMembSeq())
						.build();
				updateRequestCnt(detailVO);

				notificationService.insertNoti(NotificationType.REQUEST,
						requestVO.getRawRequestSeq(), detail.getMembSeq(), null, detail.getRawName(), setDTO.getRgtr());

				//담당자에게 메일보내기
				{
					//담당자 정보
					MemberVO managerInfo = MemberVO
							.MemberMailBuilder()
							.membSeq(detail.getMembSeq())
							.build();
					managerInfo = memberService.load(managerInfo);
					LocalDateTime now = LocalDateTime.now();  // 현재 시간
					// 날짜 포맷 정의
					DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h:mm분", Locale.KOREAN);
					// 포맷 적용
					String fmtRequestRgtDttm = dateFormat.format(now);
					String type001 = typeList.stream()
							.anyMatch(type -> "001".equals(type.getCode()))
							? "box_active"
							: "box_default";
					String type002 = typeList.stream()
							.anyMatch(type -> "002".equals(type.getCode()))
							? "box_active"
							: "box_default";
					String type003 = typeList.stream()
							.anyMatch(type -> "003".equals(type.getCode()))
							? "box_active"
							: "box_default";

					String checkToken = UUID.randomUUID().toString();

					//Short URL 생성
					Map<String, Object> urlParam = new HashMap<String, Object>();
					urlParam.put("rawRequestSeq", requestVO.getRawRequestSeq());

					ShortUrlVO suvo = ShortUrlVO.ShortUrlTypeBuilder()
							.urlType(ShortUrlType.VIEW_RAW_REQUEST)
							.token(checkToken) //check token
							.parameters(urlParam)
							.build();
					shortUrlService.insert(suvo);

					log.debug("suvo:{}", suvo);
					String relationNo = String.valueOf(requestVO.getRawRequestSeq());
					MailType mailType = MailType.CREATE_REQUEST;
					LocaleType locale = getLocale();
					Map<String, Object> mailData = new HashMap<String, Object>();
					mailData.put("buttonLink", suvo.getRequestUrl());
					mailData.put("fmtRequestRgtDttm",fmtRequestRgtDttm);	//시간
					mailData.put("ptnName", setDTO.getPtnName());	//회사이름
					mailData.put("membName", setDTO.getMembName());	//요청자이름
					mailData.put("phone", setDTO.getPhone());	//연락처
					mailData.put("email", setDTO.getEmail());	//이메일
					mailData.put("address", setDTO.getAddress());	//주소
					mailData.put("title", detail.getTitle());	//title
					mailData.put("supplier", detail.getSupplier());	//공급사
					mailData.put("managerName", managerInfo.getMembName());	//담당자이름
					mailData.put("type001", type001);	//자료 001
					mailData.put("type002", type002);	//자료 002
					mailData.put("type003", type003);	//자료 003
					mailData.put("reqDetail", setDTO.getReqDetail());	//요청사항

					mailService.sendTemplateMail(
							managerInfo.getEmail(), detail.getMembSeq(),
							mailType, locale, null, 0, mailData,
							null, relationNo, null);
				}
			}

			axRet.set(ApiResultError.NO_ERROR, getLocale());
		}catch (Exception e){
			e.printStackTrace();
			axRet.set(ApiResultError.ERROR_DEFAULT, getLocale());
		}

		return axRet;
	}

	@Override
	public int updateStatus(RawRequestSetDTO setDTO){
		return dao.updateStatus(setDTO);
	}

	@Override
	public int insertRequestReply(RawRequestReplyVO param){
		return dao.insertRequestReply(param);
	}

	@Override
	public int addRequestReply(RawRequestReplySetDTO setDTO, List<MultipartFile> uploadFiles) throws IOException{
		log.debug("[REQUEST REPLY ADD INFO] {}", setDTO.toString());

		RawRequestReplyVO replyVO = RawRequestReplyVO
				.AddRequestReplyBuilder()
				.fromDto(setDTO)
				.build();
		//답변 저장
		int result = insertRequestReply(replyVO);

		setDTO.setRawReplySeq(replyVO.getRawReplySeq());

		//파일처리
		procAttachFiles(setDTO, uploadFiles, setDTO.getStrDelFileIds());

		//알림 등록
		if(setDTO.getManagerSeq() == setDTO.getMembSeq()) {
			//상태 변경
			RawRequestSetDTO dto = new RawRequestSetDTO();
			dto.setRawRequestSeq(setDTO.getRawRequestSeq());
			dto.setStatus("003");
			updateStatus(dto);

			NotificationType notiType = NotificationType.REQUEST_SEND;
			notificationService.insertNoti(notiType,
					setDTO.getRawRequestSeq(), setDTO.getMembSeq(), setDTO.getPtnName(), setDTO.getRawName(), setDTO.getRgtr());

			{    //메일보내기
				//요청자 정보
				MemberVO membInfo = MemberVO
						.MemberMailBuilder()
						.membSeq(setDTO.getMembSeq())
						.build();
				membInfo = memberService.load(membInfo);
				String checkToken = UUID.randomUUID().toString();

				//Short URL 생성
				Map<String, Object> urlParam = new HashMap<String, Object>();
				urlParam.put("rawRequestSeq", setDTO.getRawRequestSeq());

				ShortUrlVO suvo = ShortUrlVO.ShortUrlTypeBuilder()
						.urlType(ShortUrlType.VIEW_RAW_REQUEST_REPLY)
						.token(checkToken) //check token
						.parameters(urlParam)
						.build();
				shortUrlService.insert(suvo);

				log.debug("suvo:{}", suvo);
				String relationNo = String.valueOf(setDTO.getRawRequestSeq());
				MailType mailType = MailType.SEND_REQUEST;
				LocaleType locale = getLocale();
				Map<String, Object> mailData = new HashMap<String, Object>();
				mailData.put("buttonLink", suvo.getRequestUrl());
				mailData.put("buttonName", "요청원료 바로가기");

				mailService.sendTemplateMail(
						membInfo.getEmail(), setDTO.getMembSeq(),
						mailType, locale, null, 0, mailData,
						null, relationNo, null);
			}
		}

		return result;
	}

	@Override
	public int updateReplyDelYn(RawRequestReplySetDTO setDTO){
		return dao.updateReplyDelYn(setDTO);
	}

	@Override
	public List<Map<String, Object>> getCmTypeList() {
		return dao.getCmTypeList();
	}

	protected final String getFileTypeCode() {
		return String.format("FT_%S", "REPLY");
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
	private List<Map<String, Object>> procAttachFiles(RawRequestReplySetDTO paramDto, List<MultipartFile> uploadFiles, String strDelFileIds) throws IllegalStateException, IOException {
		String fileTypeCd = getFileTypeCode();

		// 파일 추출 및 저장
		List<FileSetDTO> newFiles = new ArrayList<>();
		for(MultipartFile mFile : uploadFiles) {
			FileSetDTO fDto = fileRepo.storeFile(mFile, fileTypeCd);
			fDto.setRefSeq(paramDto.getRawReplySeq());
			fDto.setRgtr(paramDto.getRgtr());
			newFiles.add(fDto);
		}

		String[] delFileIds = !StringUtils.hasText(strDelFileIds) ? new String[0] : strDelFileIds.split(",");
		List<FileSetDTO> delFiles = new ArrayList<>();
		if(delFileIds.length > 0) {
			List<Map<String, Object>> delList = fileRepo.getList(fileTypeCd, paramDto.getRawReplySeq(), delFileIds);
			for(Map<String, Object> delFile : delList) {

				FileSetDTO item = new FileSetDTO();
				item.setFileId(delFile.get("fileId").toString());
				item.setChnr(paramDto.getRgtr());

				delFiles.add(item);
			}
		}
		List<Map<String, Object>> list = null;
		//파일 추가 삭제
		list = fileRepo.setFiles(newFiles, delFiles, Const.DELETE_PHYSICAL_FILE);

		return list;
	}
}
