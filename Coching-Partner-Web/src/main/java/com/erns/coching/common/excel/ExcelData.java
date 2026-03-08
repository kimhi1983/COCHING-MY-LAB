package com.erns.coching.common.excel;

import java.util.List;
import java.util.Map;

public class ExcelData {

	private List<ExcelHeader> headerList;
	private List<Map<String, Object>> dataList;

	private String sheetTitle;
	private String subTitle;
	private String description;

	public ExcelData() {
		this(null, null, null, null, null);
	}

	public ExcelData(List<ExcelHeader> headerList, List<Map<String, Object>> dataList, String sheetTitle,
			String subTitle, String description) {
		this.headerList = headerList;
		this.dataList = dataList;
		this.sheetTitle = sheetTitle;
		this.subTitle = subTitle;
		this.description = description;
	}

	public List<ExcelHeader> getHeaderList() {
		return headerList;
	}

	public void setHeaderList(List<ExcelHeader> headerList) {
		this.headerList = headerList;
	}

	public List<Map<String, Object>> getDataList() {
		return dataList;
	}

	public void setDataList(List<Map<String, Object>> dataList) {
		this.dataList = dataList;
	}

	public String getSheetTitle() {
		return sheetTitle;
	}

	public void setSheetTitle(String sheetTitle) {
		this.sheetTitle = sheetTitle;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
