package com.erns.coching.common.excel;

public class ExcelHeader {

	private String headerName;
	private String columnName;

	public ExcelHeader() {
		this(null, null);
	}

	public ExcelHeader(String headerName, String columnName) {
		this.headerName = headerName;
		this.columnName = columnName;
	}

	public String getHeaderName() {
		return headerName;
	}

	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

}
