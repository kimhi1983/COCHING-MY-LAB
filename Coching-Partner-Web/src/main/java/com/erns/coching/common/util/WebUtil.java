package com.erns.coching.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * <p>Web Util</p> 
 *
 * @author Hunwoo Park 
 *
 */
public class WebUtil {
	
	//안드로이드 구분 정규식
	private static final String androidReg = "\\bandroid|Nexus\\b";
	//IOS 구분 정규식
	private static final String iosReg = "ip(hone|od|ad)";
	
	private static final Pattern androidPat = Pattern.compile(androidReg, Pattern.CASE_INSENSITIVE);
	private static final Pattern iosPat = Pattern.compile(iosReg, Pattern.CASE_INSENSITIVE);
	
	/**
	 * UserAgent로 안드로이드 인지 구분
	 * @param userAgent
	 * @return
	 */
	public static boolean likeAndroid(String userAgent){
		if(null == userAgent){
			userAgent = "";
		}
		//Match
		Matcher matcherAndroid = androidPat.matcher(userAgent);
		if(matcherAndroid.find()){
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * UserAgent로 아이폰인지 구분
	 * @param userAgent
	 * @return
	 */
	public static boolean likeIOS(String userAgent){
		if(null == userAgent){
			userAgent = "";
		}
		//Match
		Matcher matcherIOS = iosPat.matcher(userAgent);
		if(matcherIOS.find()){
			return true;
		} else {
			return false;
		}
	}

}
