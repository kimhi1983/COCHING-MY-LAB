<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/inc/jsp_commons.jinc"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:useBean id="today" class="java.util.Date" />
<%--<fmt:formatDate value="${today}" pattern="yyyyMMdd" type="date" var="nowDate" /> --%>
<fmt:formatDate value="${today}" pattern="yyyyMMddhhmm" var="nowDate" />
<fmt:formatDate value="${today}" pattern="yyyyMM" var="varsion" />
<fmt:formatDate value="${today}" pattern="yyyy" var="libVarsion" />
<c:set var="ga_key" value="${applicationScope.gaKey}" />
<c:set var="safePreviewData" value="${previewData != null ? previewData : ''}" />
<spring:eval var="escapedPreviewData"
             expression="T(org.apache.commons.lang3.StringEscapeUtils).escapeEcmaScript(safePreviewData)" />

