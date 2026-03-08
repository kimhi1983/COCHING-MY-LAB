package com.erns.coching.common.util;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

/**
 *
 * <p>날짜 관련 유틸</p>
 *
 * @author Hunwoo Park
 *
 */
public class DateUtil {

	public static final DateTimeFormatter DTF_DATEDN = DateTimeFormatter.ofPattern("yyyyMMdd");

	public static final DateTimeFormatter DTF_DATEDDASH = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public static final DateTimeFormatter DTF_DATEDOT = DateTimeFormatter.ofPattern("yyyy.MM.dd");

	public static final DateTimeFormatter DTF_DATEDOT_HHMMSS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static final SimpleDateFormat SDF_DATEDN = new SimpleDateFormat("yyyyMMdd");

	public static final SimpleDateFormat SDF_DATEDDASH = new SimpleDateFormat("yyyy-MM-dd");

	public static final SimpleDateFormat SDF_DATEDOT = new SimpleDateFormat("yyyy.MM.dd");

	public static final SimpleDateFormat SDF_DATEDDASH_HHMMSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

}
