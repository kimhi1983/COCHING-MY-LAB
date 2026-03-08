package com.erns.coching.mltln.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.erns.coching.common.aop.ApiLogging;
import com.erns.coching.common.controller.AbstractApiController;
import com.erns.coching.common.file.repository.FileRepository;
import com.erns.coching.common.model.ApiResult;
import com.erns.coching.common.model.PageInfo;
import com.erns.coching.common.type.ApiResultError;
import com.erns.coching.common.type.UserLogType;
import com.erns.coching.login.domain.LoginUserDTO;
import com.erns.coching.mltln.domain.MltlnSearchDTO;
import com.erns.coching.mltln.domain.MltlnSetDTO;
import com.erns.coching.mltln.domain.MltlnVO;
import com.erns.coching.mltln.domain.vg.ValidMltln0001;
import com.erns.coching.mltln.domain.vg.ValidMltln0002;
import com.erns.coching.mltln.domain.vg.ValidMltln0003;
import com.erns.coching.mltln.service.MltlnService;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * <p>다국어 API Controller</p>
 *
 * @author Kyungmin Lee
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/mltln")
@PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_ADMIN')")
public class MltlnApiController extends AbstractApiController {

	public static final String FILE_INPUT_NAME = "mltln_file";

	@Autowired
	private MltlnService mltlnService;

	@Autowired
    private FileRepository fileRepo;

	public static final int EXPORT_EXCEL_HEADER_ROW_IDX = 0;
	public static final int EXPORT_EXCEL_DATA_ROW_IDX = 1;

	/**
	 * MLTLN 목록
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MLTLN_LIST)
	@PostMapping("/mst/list.api")
	public ApiResult getMltlnMasterList(
			HttpServletRequest request,
			@RequestBody MltlnSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

//        log.info("검색된 mltln 확인 => {}", param);


		//API Call
		PageInfo pi = new PageInfo(0, param);
		int totalItem = mltlnService.getListCount(param);
		List<Map<String, Object>> list = null;
		if(totalItem <= 0) {
			list = new ArrayList<Map<String, Object>>();
		} else {
			list = mltlnService.getList(param);
			pi.setCurrentPage(totalItem, param.getPage());
		}

		axRet.setPageInfo(pi).setSc(param).setList(list);
		return axRet.set(ApiResultError.NO_ERROR);
	}

	/**
	 * MLTLN 상세
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MLTLN_GET)
	@PostMapping("/mst/get.api")
	public ApiResult getMltlnMaster(
			HttpServletRequest request,
			@RequestBody @Validated(ValidMltln0001.class) MltlnSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		//API Call
		Map<String, Object> mltlnMaster = mltlnService.load(param);

		axRet.setResultData(mltlnMaster).set(ApiResultError.NO_ERROR);

		return axRet;
	}

	/**
	 * MLTLN에서 CODE 존재여부 확인
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MLTLN_IS_HAVE_CODE)
	@PostMapping("/mst/isHaveCode.api")
	public ApiResult isHaveCode(
			HttpServletRequest request,
			@RequestBody MltlnSearchDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		int isHaveCode = mltlnService.isHaveCode(param);

		Map<String, Boolean> result = new HashMap<>();
		if(isHaveCode > 0) {
			// 중복 데이터 존재 => 등록 불가
			result.put("isHave", true);
		} else if(isHaveCode == 0) {
			// 등록 가능
			result.put("isHave", false);
		}

		axRet.setResultData(result);

		return axRet.set(ApiResultError.NO_ERROR);
	}


	/**
	 * MLTLN 생성
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MLTLN_ADD)
	@PostMapping("/mst/insertMltln.api")
	public ApiResult insertMltln(
			HttpServletRequest request,
			@RequestBody @Validated(ValidMltln0002.class) MltlnSetDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		LoginUserDTO loginUser = getLoginedUserObject();
		MltlnVO newMltln = MltlnVO.AddMltlnBuilder()
				.addUser(loginUser)
				.fromDto(param)
				.build();

		mltlnService.insert(newMltln);

		MltlnSearchDTO scDTO = new MltlnSearchDTO();
		scDTO.setCode(newMltln.getCode());

		Map<String, Object> retMltln = mltlnService.load(scDTO);
		axRet.setResultData(retMltln).set(ApiResultError.NO_ERROR);

		return axRet;
	}

	/**
	 * MLTLN 수정
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MLTLN_UDT)
	@PostMapping("/mst/setMltln.api")
	public ApiResult updateMltln(
			HttpServletRequest request,
			@RequestBody @Validated(ValidMltln0003.class) MltlnSetDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			LoginUserDTO loginUser = getLoginedUserObject();
			MltlnVO newMltln = MltlnVO.SetMltlnBuilder()
					.updateUser(loginUser)
					.fromDto(param)
					.code(param.getCode())
					.build();

			mltlnService.update(newMltln);

			MltlnSearchDTO scDTO = new MltlnSearchDTO();
			scDTO.setCode(newMltln.getCode());

			Map<String, Object> retMltln = mltlnService.load(scDTO);
			axRet.setResultData(retMltln).set(ApiResultError.NO_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}

	/**
	 * MLTLN json export
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MLTLN_EXPORT)
	@PostMapping("/mst/exportJson.api")
	public ApiResult exportJson(
			HttpServletRequest request,
			@RequestBody @Validated() MltlnSetDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			boolean ret = mltlnService.exportMltlnJson();

			if(ret) {
				axRet.set(ApiResultError.NO_ERROR);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet;
	}


	/**
	 * MLTLN excel import
	 * @param request
	 * @param param
	 * @param errors
	 * @return
	 */
	@ApiLogging(logType = UserLogType.MLTLN_IMPORT)
	@PostMapping("/mst/uploadFile.api")
	public ApiResult importExcel(
			HttpServletRequest request,
			@Validated() MltlnSetDTO param,
			Errors errors) {

		ApiResult axRet = new ApiResult(ApiResultError.ERROR_DEFAULT);
		if (errors.hasErrors()) { //파라미터 바인딩 오류시 리턴
			return bindError(errors, axRet);
		}

		try {
			// 파일 등록
			fileRepo.printFilesInfo(request);

			List<MultipartFile>  getUploadFiles = fileRepo.getUploadFiles(request, FILE_INPUT_NAME);
			log.debug("Files:{}", getUploadFiles.size());

			MultipartFile mf = getUploadFiles.get(0);
			Workbook wbs = WorkbookFactory.create(mf.getInputStream());

			Sheet sheet = wbs.getSheetAt(0);
			int fRow = sheet.getFirstRowNum(); // 0
			int lRow = sheet.getLastRowNum();

			Row headerRow = sheet.getRow(fRow);
			Map<String, Integer> headerMap = new HashMap<>();

			LoginUserDTO loginUser = getLoginedUserObject();

			for(int i = headerRow.getFirstCellNum(); i <= headerRow.getLastCellNum(); i++) {
				Cell headerCell = headerRow.getCell(i);
				String headerValue = getCellValueToString(headerCell);
				headerMap.put(headerValue, i);
			}


			List<MltlnVO> list = new ArrayList<>();

			for(int i=fRow+1; i <= lRow; i++) {

				Row row = sheet.getRow(i);

				if(row != null) {
					Cell cellCode = row.getCell(headerMap.get("CODE"));
					Cell cellCodeKo = row.getCell(headerMap.get("CODE_NM_KO"));
					Cell cellCodeEn = row.getCell(headerMap.get("CODE_NM_EN"));


					if (cellCode != null ) {
						MltlnSetDTO mltlnSetDTO = new MltlnSetDTO();
						mltlnSetDTO.setCode(getCellValueToString(cellCode));
						mltlnSetDTO.setCodeNmKo(getCellValueToString(cellCodeKo));
						mltlnSetDTO.setCodeNmEn(getCellValueToString(cellCodeEn));

						MltlnVO newMltln = MltlnVO.AddMltlnBuilder()
								.addUser(loginUser)
								.fromDto(mltlnSetDTO)
								.build();

						list.add(newMltln);
					}
				}
			}

			if(list.size() > 0) {
				mltlnService.importMltlnJson(list);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return axRet.set(ApiResultError.ERROR_DEFAULT);
		}

		return axRet.set(ApiResultError.NO_ERROR);
	}

	/**
	 * 테스트용 : Cell 값을 string으로 변환하여 가져옴
	 * (CellType.STRING, CellType.NUMERIC 경우만)
	 * @param cell
	 * @return
	 */
	public static String getCellValueToString(Cell cell) {
		if(cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
			return " ";
		}

		int ct = cell.getCellType();

		switch (ct) {
			case Cell.CELL_TYPE_STRING:
				return cell.getStringCellValue();
			case Cell.CELL_TYPE_NUMERIC:
				cell.setCellType(Cell.CELL_TYPE_STRING);
				return cell.getStringCellValue();
			case Cell.CELL_TYPE_FORMULA:
				return cell.getCellFormula();
			default:
				return " ";
		}

	}

}
