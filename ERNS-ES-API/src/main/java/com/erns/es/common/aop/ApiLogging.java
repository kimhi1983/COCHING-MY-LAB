package com.erns.es.common.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * <p>AOP Api log interface</p>
 *
 * @author Hunwoo Park
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface ApiLogging {

}
