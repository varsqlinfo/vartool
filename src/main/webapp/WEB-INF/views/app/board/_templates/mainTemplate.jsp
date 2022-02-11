<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/tagLib.jspf"%>

<!doctype html>
<html>
<head>
<title>- <spring:message code="home.title"/> -</title>
<%@ include file="/WEB-INF/include/appHead.jsp"%>
</head>

<body>
	<div id="wrapper" class="height100">
		<!--Start Content-->
		<div id="main-content" class="height100 col-xs-12">
			<tiles:insertAttribute name="body" />
		</div>
		<!--End Content-->
	</div>
</body>
</html>