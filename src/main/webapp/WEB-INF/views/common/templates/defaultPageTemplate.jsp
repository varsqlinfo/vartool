<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/tagLib.jspf"%>
<!doctype html>

<html>
<head>
<title>- <spring:message code="home.title"/> -</title>
<%@ include file="/WEB-INF/include/head.jsp"%>
</head>

<body class="vartool-main scroll-on">
	<div class="vartool-menu-wrapper">
		<nav class="navbar navbar-default navbar-static-top"
			role="navigation" style="margin-bottom: 0">
			<tiles:insertAttribute name="header" />
		</nav>
	</div>

	<div id="page-wrapper" class="vartool-body-wrapper">
		<tiles:insertAttribute name="body" />
	</div>
</body>
</html>