package com.erns.coching.raw.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.erns.coching.code.domain.CodeSearchDTO;
import com.erns.coching.code.domain.CodeVO;
import com.erns.coching.code.service.CodeService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PreDestroy;

import com.erns.coching.common.Const;
import com.erns.coching.common.file.domain.FileSetDTO;
import com.erns.coching.common.file.domain.FileVO;
import com.erns.coching.common.file.domain.PdfInfoVO;
import com.erns.coching.common.file.repository.FileRepository;
import com.erns.coching.common.file.service.PdfInfoService;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.service.AbstractCochingApiService;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.raw.domain.RawDetailSearchDTO;
import com.erns.coching.raw.domain.RawDetailSetDTO;
import com.erns.coching.raw.domain.RawDetailVO;
import com.erns.coching.raw.domain.RawDocSetDTO;
import com.erns.coching.raw.domain.RawDocVO;
import com.erns.coching.raw.domain.RawElmSetDTO;
import com.erns.coching.raw.domain.RawElmVO;
import com.erns.coching.raw.domain.RawManagerSearchDTO;
import com.erns.coching.raw.domain.RawManagerSetDTO;
import com.erns.coching.raw.domain.RawManagerVO;
import com.erns.coching.raw.domain.RawMasterSearchDTO;
import com.erns.coching.raw.domain.RawMasterSetDTO;
import com.erns.coching.raw.domain.RawMasterVO;
import com.erns.coching.raw.domain.RawTypeSetDTO;
import com.erns.coching.raw.domain.RawTypeVO;
import com.erns.coching.raw.domain.IngdDicSearchDTO;
import com.erns.coching.raw.domain.IngdDicVO;
import com.erns.coching.raw.mapper.RawDAO;
import com.erns.coching.raw.service.RawService;
import com.erns.coching.search.service.CochingEsRawService;
import com.erns.coching.sysProp.domain.SysPropVO;
import com.erns.coching.sysProp.service.SysPropService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

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
	private CochingEsRawService cochingEsRawService;

	@Autowired
	private SysPropService sysPropService;

	@Autowired
	private PdfInfoService pdfInfoService;

	@Autowired
	private CodeService codeService;

	@Value("${system.file.uploadpath}")
	private String uploadPath;

	//PDF 변환 쓰레드
	private final ExecutorService executor = Executors.newFixedThreadPool(5);

	/**
	 * 서비스 종료 시 임시 파일 정리
	 */
	@PreDestroy
	public void cleanup() {
		try {
			Path tempDir = Paths.get(uploadPath, "temp");
			if (Files.exists(tempDir) && Files.isDirectory(tempDir)) {
				Files.walk(tempDir)
					.filter(Files::isRegularFile)
					.forEach(path -> {
						try {
							Files.delete(path);
							log.debug("[RAW SERVICE] 임시 파일 삭제: {}", path);
						} catch (IOException e) {
							log.warn("[RAW SERVICE] 임시 파일 삭제 실패: {}", path, e);
						}
					});
				log.info("[RAW SERVICE] 임시 파일 정리 완료");
			}
		} catch (Exception e) {
			log.error("[RAW SERVICE] 임시 파일 정리 중 오류 발생", e);
		}
	}

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
	public ApiResult addRaw(RawMasterSetDTO setDTO){
		ApiResult axRet = new ApiResult();

		log.debug("[RAW ADD INFO] {}", setDTO.toString());

		//원료 마스터 insert
		RawMasterVO masterVO = RawMasterVO
				.AddRawMasterBuilder()
				.fromDto(setDTO)
				.build();
		insert(masterVO);

		//원료 성분 insert
		List<RawElmSetDTO> elmList = setDTO.getElmList();
		if(null != elmList) {
			for (RawElmSetDTO elm : elmList) {
				elm.setRawSeq(masterVO.getRawSeq());
				RawElmVO elmVO = RawElmVO
						.AddRawElmBuilder()
						.fromDto(elm)
						.seq(setDTO.getRgtr())
						.build();
				elmInsert(elmVO);
			}
		}

		//원료 구분 insert
		List<RawTypeSetDTO> typeList = setDTO.getTypeList();
		if(null != typeList) {
			for (RawTypeSetDTO type : typeList) {
				type.setRawSeq(masterVO.getRawSeq());
				RawTypeVO typeVO = RawTypeVO
						.AddRawTypeBuilder()
						.fromDto(type)
						.seq(setDTO.getRgtr())
						.build();
				typeInsert(typeVO);
			}
		}

		//원료 자료 insert
		List<RawDocSetDTO> docList = setDTO.getDocList();
		if(null != docList) {
			for (RawDocSetDTO doc : docList) {
				doc.setRawSeq(masterVO.getRawSeq());
				RawDocVO docVO = RawDocVO
						.AddRawDocBuilder()
						.fromDto(doc)
						.seq(setDTO.getRgtr())
						.build();
				docInsert(docVO);
			}
		}

		//원료 담당자 insert
		List<RawManagerSetDTO> managerList = setDTO.getManagerList();
		if(null != managerList) {
			for (RawManagerSetDTO manager : managerList) {
				manager.setRawSeq(masterVO.getRawSeq());
				RawManagerVO managerVO = RawManagerVO
						.AddRawManagerBuilder()
						.fromDto(manager)
						.seq(setDTO.getRgtr())
						.build();
				managerInsert(managerVO);
			}
		}

		//검색엔진 반영
		try{
			syncEs(new long[]{masterVO.getRawSeq()});
		}catch (Exception e){
			log.error("ES Sync Error", e);
		}

		Map<String, Object> data = new HashMap<>();
		data.put("rawSeq", masterVO.getRawSeq());

		axRet.setResultData(data).set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	@Override
	public ApiResult uploadRawExcel(RawMasterSetDTO setDTO, MultipartFile file){
		ApiResult axRet = new ApiResult();
		log.debug("[RAW UPLOAD EXCEL] {}", setDTO.toString());

		List<RawMasterSetDTO> rawDataList = new ArrayList<>();
		Workbook workbook = null;
		String fileId = null;

		try {
			InputStream is = file.getInputStream();
			workbook = new XSSFWorkbook(is);

			Sheet sheet = workbook.getSheetAt(0);

			// 전체 row 수 확인 (헤더 포함)
			int totalRowCount = sheet.getLastRowNum() + 1;
			if (totalRowCount > 10000) {
				axRet.set(ApiResultError.RAW_ERR_EXCEL_ROW_LIMIT, getLocale());
				return axRet;
			}

			// findIngdDic이 null인 행 번호를 저장할 리스트
			Set<Integer> nullRows = new HashSet<>();

			String rawName = "";
			int sortOrd = 1;
			RawMasterVO masterVO = null;

			for (Row row : sheet) {
				if (row.getRowNum() == 0 || row.getRowNum() == 1) continue; // 헤더 제외

				RawMasterSetDTO dto = new RawMasterSetDTO();
				dto.setRawName(getStringCellValue(row.getCell(0)));  // A열 (예: 원료명)
				dto.setProdCompany(getStringCellValue(row.getCell(3))); // D열 (예: 제조사)
				dto.setSupplier(getStringCellValue(row.getCell(4))); // E열 (예: 공급업체)
				dto.setWeight(getDoubleCellValue(row.getCell(5))); // F열 (예: 무게)
				String prodCountry = getStringCellValue(row.getCell(6)); // G열 (예: 제조국)
				if(!"".equals(prodCountry)) {
					CodeSearchDTO codeSearchDTO = new CodeSearchDTO();
					codeSearchDTO.setGrpCode("CH014");
					codeSearchDTO.setCodeName(prodCountry);
					CodeVO codeVO = codeService.load(codeSearchDTO);
					if(codeVO != null) {
						dto.setProdCountry(codeVO.getEtc1());
					}
				}
				dto.setPtnSeq(setDTO.getPtnSeq());
				dto.setRgtr(setDTO.getRgtr());

				if(!"".equals(dto.getRawName())) {
					if(!rawName.equals(dto.getRawName())) {
						sortOrd = 1;
						//원료 마스터 insert
						masterVO = RawMasterVO
								.AddRawMasterBuilder()
								.fromDto(dto)
								.build();
						insert(masterVO);
						rawName = masterVO.getRawName();

						//매니저 insert
						RawManagerSetDTO managerSetDTO = new RawManagerSetDTO();
						managerSetDTO.setRawSeq(masterVO.getRawSeq());
						managerSetDTO.setPtnSeq(setDTO.getPtnSeq());
						managerSetDTO.setMembSeq(setDTO.getRgtr());
						RawManagerVO managerVO = RawManagerVO
								.AddRawManagerBuilder()
								.fromDto(managerSetDTO)
								.seq(setDTO.getRgtr())
								.build();
						managerInsert(managerVO);

						//원료 default 서류 insert
						String[] docCodeList = new String[]{"001","002","003","004","005","007"};
						for (String docCode : docCodeList) {
							RawDocSetDTO doc = new RawDocSetDTO();
							doc.setRawSeq(masterVO.getRawSeq());
							doc.setDocCode(docCode);
							RawDocVO docVO = RawDocVO
									.AddRawDocBuilder()
									.fromDto(doc)
									.seq(setDTO.getRgtr())
									.build();
							docInsert(docVO);
						}

						//원료 자료 INSERT
						RawDetailSetDTO detailSetDTO = new RawDetailSetDTO();
						detailSetDTO.setRawSeq(masterVO.getRawSeq());
						detailSetDTO.setMembSeq(setDTO.getRgtr());
						String title = getStringCellValue(row.getCell(7));
						if(title.equals("")) {
							title = getStringCellValue(row.getCell(0));
						}
						detailSetDTO.setTitle(title);
						detailSetDTO.setRawDetail(getStringCellValue(row.getCell(8)));
						detailSetDTO.setMembSeq(setDTO.getRgtr());
						RawDetailVO rawDetailVO = RawDetailVO
								.SetRawDetailBuilder()
								.fromDto(detailSetDTO)
								.build();
						detailInsert(rawDetailVO);

						//원료 성분 insert
						Integer elmId = 0;
						try {
							String elmIdStr = getStringCellValue(row.getCell(9));
							if (StringUtils.hasText(elmIdStr)) {
								elmId = Integer.parseInt(elmIdStr);
							}
						} catch (NumberFormatException e) {
							elmId = 0;
						}
						String repNameKo = getStringCellValue(row.getCell(1));
						String repNameEn = getStringCellValue(row.getCell(2));

						IngdDicVO ingdDicVO = findIngdDic(elmId, repNameKo, repNameEn);
						if(ingdDicVO != null) {
							insertRawElement(masterVO, setDTO, ingdDicVO.getId(), sortOrd);
							sortOrd++;
						} else {
							// findIngdDic이 null인 경우 해당 행 번호 저장
							int rowNum = row.getRowNum();
							nullRows.add(rowNum);
						}
					}else{
						//나머지 성분 insert
						if(masterVO != null) {
							Integer elmId = 0;
							try {
								String elmIdStr = getStringCellValue(row.getCell(9));
								if (StringUtils.hasText(elmIdStr)) {
									elmId = Integer.parseInt(elmIdStr);
								}
							} catch (NumberFormatException e) {
								elmId = 0;
							}
							String repNameKo = getStringCellValue(row.getCell(1));
							String repNameEn = getStringCellValue(row.getCell(2));

							IngdDicVO ingdDicVO = findIngdDic(elmId, repNameKo, repNameEn);
							if(ingdDicVO != null) {
								insertRawElement(masterVO, setDTO, ingdDicVO.getId(), sortOrd);
								sortOrd++;
							} else {
								// findIngdDic이 null인 경우 해당 행 번호 저장
								int rowNum = row.getRowNum();
								nullRows.add(rowNum);
							}
						}
					}
				}
			}

			// null인 행들이 있으면 해당 행의 모든 셀을 노란색으로 표시하고 파일 저장
			if (!nullRows.isEmpty() && workbook instanceof XSSFWorkbook) {
				XSSFWorkbook xssfWorkbook = (XSSFWorkbook) workbook;

				// 노란색 배경색 생성
				XSSFColor yellowColor = new XSSFColor(new byte[]{(byte)255, (byte)255, (byte)0}, null); // RGB 노란색

				// 저장된 행들의 모든 셀을 노란색으로 변경
				for (Integer rowNum : nullRows) {
					Row targetRow = sheet.getRow(rowNum);
					if (targetRow != null) {
						// H열(컬럼 7)까지만 노란색 배경 적용 (0부터 7까지)
						final int MAX_COLUMN = 7; // H열

						for (int colNum = 0; colNum <= MAX_COLUMN; colNum++) {
							Cell targetCell = targetRow.getCell(colNum);
							if (targetCell == null) {
								// 셀이 없으면 생성하지 않고 건너뜀 (값이 있는 셀만 처리)
								continue;
							}

							// 기존 셀 스타일 복사 또는 새로 생성
							XSSFCellStyle cellStyle;
							if (targetCell.getCellStyle() != null) {
								cellStyle = xssfWorkbook.createCellStyle();
								cellStyle.cloneStyleFrom(targetCell.getCellStyle());
							} else {
								cellStyle = xssfWorkbook.createCellStyle();
							}

							// 배경색만 노란색으로 설정
							cellStyle.setFillForegroundColor(yellowColor);
							cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

							// 스타일 적용
							targetCell.setCellStyle(cellStyle);
						}
					}
				}

				// UUID로 파일명 생성
				fileId = UUID.randomUUID().toString();
				String fileName = fileId + ".xlsx";

				// temp 폴더 경로 생성
				Path tempDir = Paths.get(uploadPath, "temp");
				Files.createDirectories(tempDir);

				// 파일 저장
				Path filePath = tempDir.resolve(fileName);
				File tempFile = filePath.toFile();
				try (FileOutputStream fos = new FileOutputStream(tempFile)) {
					workbook.write(fos);
				}

				// JVM 종료 시 자동 삭제되도록 등록
				tempFile.deleteOnExit();

				log.debug("[RAW UPLOAD EXCEL] 임시 파일 저장 완료: {}", filePath);
			}

		} catch (IOException e) {
			log.error("[RAW UPLOAD EXCEL] 오류 발생", e);
			axRet = new ApiResult(ApiResultError.ERROR_DEFAULT, getLocale());
		} catch (Exception e) {
			log.error("[RAW UPLOAD EXCEL] 예상치 못한 오류 발생", e);
			axRet = new ApiResult(ApiResultError.ERROR_DEFAULT, getLocale());
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					log.error("[RAW UPLOAD EXCEL] Workbook close 오류", e);
				}
			}
		}

		axRet.set(ApiResultError.NO_ERROR, getLocale());

		// fileId가 있으면 ApiResult에 추가
		if (fileId != null) {
			Map<String, Object> data = new HashMap<>();
			data.put("fileId", fileId);
			// 원본 Excel 파일명 추가
			String originalFileName = file.getOriginalFilename();
			data.put("fileName", originalFileName);
			axRet.setResultData(data);
		}

		return axRet;
	}

	public static String getStringCellValue(Cell cell) {
		if (cell == null) {
			return "";
		}

		switch (cell.getCellType()) { // POI 3.17에서는 int 반환
			case Cell.CELL_TYPE_STRING:
				return cell.getStringCellValue().trim(); // 문자열인 경우

			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					// 날짜 형식일 경우 날짜를 문자열로 변환
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					return sdf.format(cell.getDateCellValue()).trim();
				} else {
					// 일반 숫자인 경우 정수 or 실수 변환
					return String.valueOf((long) cell.getNumericCellValue()).trim();
				}

			case Cell.CELL_TYPE_BOOLEAN:
				return String.valueOf(cell.getBooleanCellValue()).trim(); // 불리언 값 변환

			case Cell.CELL_TYPE_FORMULA:
				return cell.getCellFormula().trim(); // 수식 반환

			case Cell.CELL_TYPE_BLANK:
			case Cell.CELL_TYPE_ERROR:
			default:
				return "";  // 빈 값 처리
		}
	}

	private String getDoubleCellValue(Cell cell) {
		if (cell == null) {
			return "";  // null이면 "0" 반환
		}

		DecimalFormat df = new DecimalFormat("#.##"); // 소수점 2자리까지 표시

		String value = "";
		switch (cell.getCellTypeEnum()) {  // POI 3.17에서는 getCellTypeEnum() 사용
			case NUMERIC:
				value = df.format(cell.getNumericCellValue());  // 숫자 값 반환 (String 변환)
				break;

			case STRING:
				value = cell.getStringCellValue().trim();  // 문자열 값 그대로 반환
				break;

			case FORMULA:
				try {
					value = df.format(cell.getNumericCellValue());  // 수식 결과가 숫자인 경우
				} catch (IllegalStateException e) {
					value = cell.getStringCellValue().trim(); // 수식 결과가 문자열인 경우
				}
				break;

			case BOOLEAN:
				value = String.valueOf(cell.getBooleanCellValue()); // Boolean 값을 문자열로 변환
				break;

			case BLANK:
			case ERROR:
			default:
				return "";  // 빈 값 또는 오류인 경우 "0" 반환
		}

		// 숫자와 점(.)을 제외한 모든 문자 제거
		value = value.replaceAll("[^0-9.]", "");

		// 빈 문자열이 되면 "" 반환
		return value.isEmpty() || "0.00".equals(value) || "0".equals(value) ? "" : value;
	}

	@Override
	public ApiResult updateRaw(RawMasterSetDTO setDTO){
		ApiResult axRet = new ApiResult();

		log.debug("[RAW UPDATE INFO] {}", setDTO.toString());

		//원료 마스터 insert
		RawMasterVO masterVO = RawMasterVO
				.AddRawMasterBuilder()
				.fromDto(setDTO)
				.build();
		update(masterVO);

		//원료 성분 insert
		List<RawElmSetDTO> elmList = setDTO.getElmList();
		elmDelete(setDTO);
		if(null != elmList) {
			for (RawElmSetDTO elm : elmList) {
				elm.setRawSeq(masterVO.getRawSeq());
				RawElmVO elmVO = RawElmVO
						.AddRawElmBuilder()
						.fromDto(elm)
						.seq(setDTO.getRgtr())
						.build();
				elmInsert(elmVO);
			}
		}

		//원료 구분 insert
		List<RawTypeSetDTO> typeList = setDTO.getTypeList();
		typeDelete(setDTO);
		if(null != typeList) {
			for (RawTypeSetDTO type : typeList) {
				type.setRawSeq(masterVO.getRawSeq());
				RawTypeVO typeVO = RawTypeVO
						.AddRawTypeBuilder()
						.fromDto(type)
						.seq(setDTO.getRgtr())
						.build();
				typeInsert(typeVO);
			}
		}

		//원료 자료 insert
		List<RawDocSetDTO> docList = setDTO.getDocList();
		docDelete(setDTO);
		if(null != docList) {
			for (RawDocSetDTO doc : docList) {
				doc.setRawSeq(masterVO.getRawSeq());
				RawDocVO docVO = RawDocVO
						.AddRawDocBuilder()
						.fromDto(doc)
						.seq(setDTO.getRgtr())
						.build();
				docInsert(docVO);
			}
		}

		//원료 담당자 insert
		List<RawManagerSetDTO> managerList = setDTO.getManagerList();
		RawManagerSetDTO delDTO = new RawManagerSetDTO();
		delDTO.setRawSeq(setDTO.getRawSeq());
		delDTO.setPtnSeq(setDTO.getPtnSeq());
		managerDelete(delDTO);
		if(null != managerList) {
			for (RawManagerSetDTO manager : managerList) {
				manager.setRawSeq(masterVO.getRawSeq());
				RawManagerVO managerVO = RawManagerVO
						.AddRawManagerBuilder()
						.fromDto(manager)
						.seq(setDTO.getRgtr())
						.build();
				managerInsert(managerVO);
			}
		}

		Map<String, Object> data = new HashMap<>();
		data.put("rawSeq", masterVO.getRawSeq());

		axRet.setResultData(data).set(ApiResultError.NO_ERROR, getLocale());

		return axRet;
	}

	/**
	 * 원료 자료 등록
	 * @param paramDto
	 * @param uploadFiles
	 * @return
	 */
	@Override
	public int addRawDetail(RawDetailSetDTO paramDto, List<MultipartFile> uploadFiles) throws IOException {
		log.debug("[RAW DETAIL ADD INFO] {}", paramDto.toString());

		RawDetailVO rawDetailVO = RawDetailVO
				.SetRawDetailBuilder()
				.fromDto(paramDto)
				.build();

		int result = detailInsert(rawDetailVO);

		//등록일 때 처리
		if(0 == paramDto.getRawDetailSeq()){
			paramDto.setRawDetailSeq(rawDetailVO.getRawDetailSeq());
		}

		procAttachFiles(paramDto, uploadFiles, paramDto.getStrDelFileIds(), false);

		//html 이미지 파일 폴더 변경
		ObjectMapper objectMapper = new ObjectMapper();
		// 날짜 형식 지정
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		// 새로운 JavaTimeModule 추가 (Java 8 이상)
		objectMapper.registerModule(new JavaTimeModule());
		List<FileVO> detailFileList = objectMapper.readValue(paramDto.getStrDetailFiles(), new TypeReference<List<FileVO>>() {});
		fileRepo.changeRefCode(detailFileList, "FT_RAWDETAIL");

		return result;
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
	public int delete(RawMasterSetDTO setDTO) {
		return dao.delete(setDTO);
	}

	@Override
	public List<Map<String, Object>> getElmList(RawMasterSearchDTO param) {
		return dao.getElmList(param);
	}

	@Override
	public int elmInsert(RawElmVO param) {
		return dao.elmInsert(param);
	}

	@Override
	public int elmDelete(RawMasterSetDTO setDTO) {
		return dao.elmDelete(setDTO);
	}

	@Override
	public List<Map<String, Object>> getDocList(RawMasterSearchDTO param) {
		return dao.getDocList(param);
	}

	@Override
	public int docInsert(RawDocVO param) {
		return dao.docInsert(param);
	}

	@Override
	public int docDelete(RawMasterSetDTO setDTO) {
		return dao.docDelete(setDTO);
	}

	@Override
	public List<Map<String, Object>> getTypeList(RawMasterSearchDTO param) {
		return dao.getTypeList(param);
	}

	@Override
	public int typeInsert(RawTypeVO param) {
		return dao.typeInsert(param);
	}

	@Override
	public int typeDelete(RawMasterSetDTO setDTO) {
		return dao.typeDelete(setDTO);
	}

	@Override
	public List<Map<String, Object>> getManagerList(RawManagerSearchDTO param){
		return dao.getManagerList(param);
	}

	@Override
	public Map<String, Object> getManager(RawManagerSearchDTO param){
		return dao.getManager(param);
	}

	@Override
	public int managerInsert(RawManagerVO param) {
		return dao.managerInsert(param);
	}

	@Override
	public int managerDelete(RawManagerSetDTO setDTO) {
		return dao.managerDelete(setDTO);
	}

	@Override
	public Map<String, Object> getDetail(RawDetailSearchDTO param){
		return dao.getDetail(param);
	}

	@Override
	public RawDetailVO loadDetail(RawDetailSearchDTO param){
		return dao.loadDetail(param);
	}

	@Override
	public int detailInsert(RawDetailVO param){
		return dao.detailInsert(param);
	}

	@Override
	public int detailDelete(RawDetailVO param){
		return dao.detailDelete(param);
	}

	@Override
	public int updateDetailUseYn(RawDetailVO param){
		return dao.updateDetailUseYn(param);
	}

	@Override
	public int updateDetailDelYn(RawDetailVO param){
		return dao.updateDetailDelYn(param);
	}

	@Override
	public List<Map<String, Object>> getGarbageList(RawMasterSearchDTO param) {
		return dao.getGarbageList(param);
	}

	@Override
	public int getGarbageListCount(RawMasterSearchDTO param) {
		return dao.getGarbageListCount(param);
	}

	@Override
	public int updateRawManager(RawManagerSetDTO param){

		log.debug("[RAW MANAGER UPDATE INFO] {}", param.toString());

		int result = 0;
		List<RawDetailSetDTO> checklist = param.getCheckedlist();

		for(RawDetailSetDTO detail : checklist){

			if(param.getNewManagerSeq() != detail.getMembSeq()) {
				//담당자 여부 체크
				RawManagerSearchDTO mSearch = new RawManagerSearchDTO();
				mSearch.setRawSeq(detail.getRawSeq());
				mSearch.setMembSeq(param.getNewManagerSeq());
				Map<String, Object> map = getManager(mSearch);

				if (null == map) {
					//새 담당자 추가
					param.setRawSeq(detail.getRawSeq());
					param.setMembSeq(param.getNewManagerSeq());
					RawManagerVO insertVO = RawManagerVO
							.AddRawManagerBuilder()
							.fromDto(param)
							.seq(param.getRgtr())
							.build();
					managerInsert(insertVO);
				}

				//기존 담당자 자료 삭제 여부 변경
				detail.setDelYn("Y");
				RawDetailVO deleteVO = RawDetailVO
						.UpdateRawDetailYnBuilder()
						.fromDto(detail)
						.seq(param.getRgtr())
						.build();
				updateDetailDelYn(deleteVO);

				RawDetailSearchDTO search = new RawDetailSearchDTO();
				search.setRawDetailSeq(deleteVO.getRawDetailSeq());
				RawDetailVO load = loadDetail(search);

				RawDetailVO detailVO = RawDetailVO
						.ChangeRawDetailBuilder()
						.fromDto(load)
						.membSeq(param.getNewManagerSeq())
						.seq(param.getRgtr())
						.build();
				//새 담당자 자료 추가
				result = detailInsert(detailVO);

				//기존 담당자 삭제
				param.setRawSeq(detail.getRawSeq());
				param.setMembSeq(detail.getMembSeq());
				managerDelete(param);
			}else{
				result = 1;
			}
		}

		//검색엔진 반영
		try{
			Set<Long> rawSeqSet = new HashSet<>();
			for(RawDetailSetDTO setDto : checklist){
				rawSeqSet.add(setDto.getRawSeq());
			}
			long[] rawSeqs = rawSeqSet.stream().mapToLong(Long::longValue).toArray();
			int retSync = syncEs(rawSeqs);
		}catch (Exception e){
			log.error("ES Sync Error", e);
		}

		return result;
	}

	@Override
	public List<Map<String, Object>> getCmTypeList() {
		return dao.getCmTypeList();
	}

	@Override
	public List<IngdDicVO> getIngdDic(IngdDicSearchDTO param) {
		return dao.getIngdDic(param);
	}

	protected final String getFileTypeCode(String fileType) {
		return String.format("FT_%S", fileType);
	}

	/**
	 * 성분 사전에서 성분을 찾는 메서드
	 * ID로 먼저 검색하고, 없으면 영문명으로, 없으면 한글명으로 검색
	 * @param elmId 성분 ID (0이면 무시)
	 * @param repNameKo 한글 대표명
	 * @param repNameEn 영문 대표명
	 * @return 찾은 성분 정보, 없으면 null
	 */
	private IngdDicVO findIngdDic(Integer elmId, String repNameKo, String repNameEn) {
		// ID로 검색
		if(elmId != null && elmId != 0) {
			IngdDicSearchDTO searchIdDTO = new IngdDicSearchDTO();
			searchIdDTO.setId(elmId);
			List<IngdDicVO> ingdDicList = getIngdDic(searchIdDTO);
			if(ingdDicList != null && !ingdDicList.isEmpty()) {
				return ingdDicList.get(0);
			}
		}

		// 영문명으로 검색
		if(StringUtils.hasText(repNameEn)) {
			IngdDicSearchDTO searchEnDTO = new IngdDicSearchDTO();
			searchEnDTO.setRepNameEn(repNameEn);
			List<IngdDicVO> ingdDicList = getIngdDic(searchEnDTO);
			if(ingdDicList != null && !ingdDicList.isEmpty()) {
				return ingdDicList.get(0);
			}
		}

		// 한글명으로 검색
		if(StringUtils.hasText(repNameKo)) {
			IngdDicSearchDTO searchKoDTO = new IngdDicSearchDTO();
			searchKoDTO.setRepName(repNameKo);
			List<IngdDicVO> ingdDicList = getIngdDic(searchKoDTO);
			if(ingdDicList != null && !ingdDicList.isEmpty()) {
				return ingdDicList.get(0);
			}
		}

		return null;
	}

	/**
	 * 원료 성분을 삽입하는 메서드
	 * @param masterVO 원료 마스터 정보
	 * @param setDTO 원료 설정 DTO
	 * @param ingdDicId 성분 사전 ID
	 * @param sortOrd 정렬 순서
	 */
	private void insertRawElement(RawMasterVO masterVO, RawMasterSetDTO setDTO, Integer ingdDicId, int sortOrd) {
		RawElmSetDTO elmSetDTO = new RawElmSetDTO();
		elmSetDTO.setRawSeq(masterVO.getRawSeq());
		elmSetDTO.setRawElmId(ingdDicId);
		elmSetDTO.setSortOrd(sortOrd);

		RawElmVO elmVO = RawElmVO
				.AddRawElmBuilder()
				.fromDto(elmSetDTO)
				.seq(setDTO.getRgtr())
				.build();
		elmInsert(elmVO);
	}

	/**
	 * 썸네일 처리
	 * @param paramDto
	 * @param uploadFiles
	 * @param strDelFileIds
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	@Override
	public List<Map<String, Object>> procAttachThumbFiles(RawMasterSetDTO paramDto, List<MultipartFile> uploadFiles, String strDelFileIds, boolean isPreview) throws IllegalStateException, IOException {
		String fileTypeCd = getFileTypeCode(paramDto.getFileType());

		// 파일 추출 및 저장
		List<FileSetDTO> newFiles = new ArrayList<>();
		for(MultipartFile mFile : uploadFiles) {
			FileSetDTO fDto = fileRepo.storeFile(mFile, fileTypeCd);
			fDto.setRefSeq(paramDto.getPtnSeq());
			fDto.setRgtr(paramDto.getRgtr());
			newFiles.add(fDto);
		}

		String[] delFileIds = !StringUtils.hasText(strDelFileIds) ? new String[0] : strDelFileIds.split(",");
		List<FileSetDTO> delFiles = new ArrayList<>();
		if(delFileIds.length > 0) {
			List<Map<String, Object>> delList = fileRepo.getList(fileTypeCd, paramDto.getPtnSeq(), delFileIds);
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
	public List<Map<String, Object>> procAttachFiles(RawDetailSetDTO paramDto, List<MultipartFile> uploadFiles, String strDelFileIds, boolean isPreview) throws IllegalStateException, IOException {
		String fileTypeCd = getFileTypeCode(paramDto.getFileType());

		// 파일 추출 및 저장
		List<FileSetDTO> newFiles = new ArrayList<>();
		for(MultipartFile mFile : uploadFiles) {
			FileSetDTO fDto = fileRepo.storeFile(mFile, fileTypeCd);
			fDto.setRefSeq(paramDto.getRawDetailSeq());
			fDto.setRgtr(paramDto.getMembSeq());
			newFiles.add(fDto);
		}

		String[] delFileIds = !StringUtils.hasText(strDelFileIds) ? new String[0] : strDelFileIds.split(",");
		List<FileSetDTO> delFiles = new ArrayList<>();
		if(delFileIds.length > 0) {
			List<Map<String, Object>> delList = fileRepo.getList(fileTypeCd, paramDto.getRawDetailSeq(), delFileIds);
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
	 * 첨부파일 PDF 처리
	 * 비동기로 쓰레드 풀에 추가 처리
	 */
	@Override
	public void addPdfProcess(List<String> fileIdList){

		executor.submit(() -> {
			try {
				for(String fileId : fileIdList) {
					if(!StringUtils.hasText(fileId)) {
						continue;
					}

					PdfInfoVO pdfVO = pdfInfoService.loadVo(fileId);
					if(pdfVO != null) {
						continue;
					}

					pdfInfoService.pdfAllProcess(fileId);
					log.info("파일 변환 완료: {}", fileId);
				}

			} catch (Exception ex) {
				log.error("addPdfProcess Error", ex);
			}
		});

	}

	/**
	 * [ES] 환경설정에서 ES index 명을 가져온다.
	 * @param syskey
	 * @return
	 */
	private String getIndexName(String syskey) {
		SysPropVO loadProp = sysPropService.loadVo(syskey);
		if(null != loadProp){
			return loadProp.getSysValue();
		}

		return null;
	}

	public int syncEs(long[] rawSeqs){
		//검색엔진 반영
		String rawIndexName = getIndexName(Const.ES_COCHING_RAW_ACTIVE_INDEX_SYSKEY);
		if(!StringUtils.hasText(rawIndexName)){
			return 0;
		}

		int retSyncCnt = cochingEsRawService.bulkSyncDocToEs(rawIndexName, rawSeqs);

		return retSyncCnt;
	}
}
