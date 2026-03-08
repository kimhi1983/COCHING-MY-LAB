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
<%--<spring:eval var="ga_key" expression="@configProp['ga.key.bo']"/> --%>


<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8" />
	  <title>ErnsEsApi</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width,initial-scale=1.0,maximum-scale=1" />
    <link rel="shortcut icon" href="/favicon.png" type="image/x-icon">
    <link href="/css/app.css?00e0ca41301dd104392c" rel="stylesheet"></head>
  <body>
    <noscript>
      <strong>
        We're sorry but 
        ErnsEsApi doesn't work properly without JavaScript enabled. 
        Please enable it to continue.
      </strong>
    </noscript>
    <div id="app">
      <div id="ie-div" style="display:none;">
        본 사이트는 Internet Exploere에서 실행이 불가합니다.<br/>
        아래를 클릭하여 Microsoft Edge에서 실행 하십시오.<br/> 
        <a href='microsoft-edge:${currentURL}'>Microsoft Edge 에서 실행하기</a>  
      </div>  
      <script>
        var agent = navigator.userAgent.toLowerCase();
        if ( (navigator.appName == 'Netscape' && agent.indexOf('trident') != -1) || (agent.indexOf("msie") != -1)) {
             // ie일 경우
             var elem = document.getElementById("ie-div");
             elem.style.display = 'block';
        }else{
             // ie가 아닐 경우
        }    
      </script>
    </div>
    <!-- Libs -->
    <script></script>
    <!-- built files will be auto injected -->
  <script src="/js/app.js?00e0ca41301dd104392c"></script></body>    
  <!-- Global site tag (gtag.js) - Google Analytics -->
  <!--  
  -->
</html>

