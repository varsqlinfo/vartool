<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/tagLib.jspf"%>
<!doctype html>

<html>
<head>
<title>${cmpGroupInfo.groupName} - <spring:message code="home.title"/></title>
<%@ include file="/WEB-INF/include/head.jsp"%>
</head>

<body class="vartool-main">
	<div class="vartool-menu-wrapper">
		<nav class="navbar" role="navigation">
			<tiles:insertAttribute name="header" />
		</nav>
	</div>

	<div id="page-wrapper" class="vartool-body-wrapper">
		<tiles:insertAttribute name="body" />
	</div>
</body>
</html>