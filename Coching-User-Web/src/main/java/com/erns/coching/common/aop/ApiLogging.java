package com.erns.coching.common.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.erns.coching.common.type.UserLogType;
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiLogging {

  String value() default ""; 
	
	UserLogType logType() default UserLogType.DEFAULT;
	
	String action() default "default"; //기본 default, updateLast :마지막 로그를 별도로 갱신
    
}
