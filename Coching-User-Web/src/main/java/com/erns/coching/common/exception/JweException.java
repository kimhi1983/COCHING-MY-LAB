package com.erns.coching.common.exception;

import com.erns.coching.common.type.ApiResultError;

/**
 * 
 * <p>JWE 처리시 발생가능한 Exception<br/>
 * 주로 JWE 복호화시 발생 처리<br/> 
 * </p> 
 *
 * @author Hunwoo Park 
 *
 */
public class JweException extends Exception {

	private static final long serialVersionUID = 2287905851925635164L;
	
	private final ApiResultError code;

	public JweException(ApiResultError code) {
		super();
		this.code = code;
	}

	public JweException(String message, Throwable cause, ApiResultError code) {
		super(message, cause);
		this.code = code;
	}

	public JweException(String message, ApiResultError code) {
		super(message);
		this.code = code;
	}

	public JweException(Throwable cause, ApiResultError code) {
		super(cause);
		this.code = code;
	}

	public ApiResultError getCode() {
		return this.code;
	}

}
