package com.erns.coching.common.excel;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.document.AbstractXlsView;

public class CommonExcelViewWrap extends AbstractXlsView {

	public static final String KEY_FILE_NAME = "fileName";
	public static final String KEY_EXCEL_DATA = "excelData";

	public static final String DEFAULT_FILE_NAME = "excelData";

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook
			, HttpServletRequest req
			, HttpServletResponse res) throws Exception {

		CellStyle cs = workbook.createCellStyle();
		cs.setWrapText(true);

		String fileName = (String) model.get(KEY_FILE_NAME);
		String downFileName = getFileExtension(workbook, mapToFileName(fileName));
		setFileName(req, res, downFileName);

		@SuppressWarnings("unchecked")
		List<ExcelData> sheetList = (List<ExcelData>) model.get(KEY_EXCEL_DATA);

		Sheet[] worksheet = new Sheet[sheetList.size()];
		for (int i = 0; i < sheetList.size(); i++) {

			ExcelData sheetData = sheetList.get(i);
			if(sheetData == null) {
				continue;
			}

			Row row = null;
			int curRow = 1;

			{	//Create Sheet
				String sheetTitle = StringUtils.hasText(sheetData.getSheetTitle()) ? sheetData.getSheetTitle() : "";
				if(!StringUtils.hasText(sheetTitle)) {
					worksheet[i] = workbook.createSheet();
				}else {
					worksheet[i] = workbook.createSheet(sheetTitle);
				}
			}

			{	// Write Title
				String excelSubTitle = StringUtils.hasText(sheetData.getSubTitle()) ? sheetData.getSubTitle() : "";
				if(StringUtils.hasText(excelSubTitle)) {
					row = worksheet[i].createRow(curRow++);
					if (excelSubTitle.indexOf("|") == -1) {
						row.createCell(0).setCellValue(excelSubTitle);
					} else {
						String[] titleAr = excelSubTitle.split("\\|");
						for (int j = 0; j < titleAr.length; j++) {
							row.createCell(j).setCellValue(titleAr[j]);
						}
					}
				}
			}

			// Data
			List<ExcelHeader> ehs = sheetData.getHeaderList();
			List<Map<String, Object>> eds = sheetData.getDataList();
			if (ehs != null && !ehs.isEmpty()) {
				curRow++;
				row = worksheet[i].createRow(curRow++);

				int cIdx = 0;
				for (ExcelHeader ex : ehs) {
					Cell cell = row.createCell(cIdx++);
					cell.setCellValue(ex.getHeaderName());
					cell.setCellStyle(cs);
				}

				if (eds != null) {
					for (Map<String, Object> rd : eds) {
						row = worksheet[i].createRow(curRow++);

						cIdx = 0;
						for (ExcelHeader ex : ehs) {
							String keyName = ex.getColumnName();
							Object value = rd.get(keyName);
							if (value == null) {
								cIdx++;
								continue;
							}

							Cell cell = row.createCell(cIdx++);
							cell.setCellValue(value.toString());
							cell.setCellStyle(cs);
						}
					}
				}
			}else{
				if (eds != null) {
					for (Map<String, Object> rd : eds) {
						row = worksheet[i].createRow(curRow++);

						int cIdx = 0;
						for(Object value : rd.values()) {
							if (value == null) {
								cIdx++;
								continue;
							}

							Cell cell = row.createCell(cIdx++);
							cell.setCellValue(value.toString());
							cell.setCellStyle(cs);
						}
					}
				}
			}

			{	//description
				String description = StringUtils.hasText(sheetData.getDescription()) ? sheetData.getDescription() : "";
				if(StringUtils.hasText(description)) {
					curRow += 3;
					// Write description
					if (description.indexOf("|") == -1) {
						row = worksheet[i].createRow(curRow++);
						row.createCell(0).setCellValue(description);
					} else {
						String[] descAr = description.split("\\|");
						for (int j = 0; j < descAr.length; j++) {
							row = worksheet[i].createRow(curRow++);
							row.createCell(0).setCellValue(descAr[j]);
						}
					}
				}
			}
		}

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		workbook.write(byteArrayOutputStream);
		res.setContentLength(byteArrayOutputStream.size());

		OutputStream os = res.getOutputStream();
		workbook.write(os);

		os.flush();
		os.close();
		workbook.close();
	}

	private String mapToFileName(String fileName) {
		String excelFileName = fileName;
		if(!StringUtils.hasText(excelFileName)) {
			excelFileName = DEFAULT_FILE_NAME;
		}

		try {
			excelFileName = URLEncoder.encode(excelFileName, "UTF-8");
		}catch(UnsupportedEncodingException uex) {
			excelFileName = DEFAULT_FILE_NAME;
		}

		return excelFileName;
	}

	private void setFileName(HttpServletRequest req, HttpServletResponse res, String fileName) throws UnsupportedEncodingException {
		res.setContentType("application/octet-stream");

		String userAgent = req.getHeader("User-Agent");
		userAgent = userAgent == null ? "" : userAgent.toUpperCase();

		String resFilename = fileName;

		if (userAgent.indexOf("MSIE 5.5") > -1) {
			res.setHeader("Content-Disposition", "filename=" + resFilename + ";");

		} else if (userAgent.indexOf("MSIE 6.0") > -1) {
			res.setHeader("Content-Disposition", "attachment; filename=" + resFilename + ";");

		} else if (userAgent.indexOf("MSIE") > -1 || userAgent.indexOf("TRIDENT") > -1) {
			res.setHeader("Content-Disposition", "attachment; filename=" + resFilename + ";");

		} else {

			resFilename = new String(fileName.getBytes("UTF-8"), "ISO-8859-1").replaceAll("[\r\n]","");
			res.setHeader("Content-Disposition", "attachment; filename=\"" + resFilename + "\";");
		}
	}

	private String getFileExtension(Workbook workbook, String fileName) {
		if (workbook instanceof XSSFWorkbook) {
			fileName += ".xlsx";
		}
		if (workbook instanceof SXSSFWorkbook) {
			fileName += ".xlsx";
		}
		if (workbook instanceof HSSFWorkbook) {
			fileName += ".xls";
		}

		return fileName;
	}

}
