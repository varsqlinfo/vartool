<%@ page language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/include/tagLib.jspf"%>
<div class="navbar-header">
    <a class="navbar-brand" href="<c:url value="/" />"><spring:message code="home.title"/></a>
</div>
<!-- Top Menu Items -->
<ul class="nav navbar-top-links navbar-right">
    <li class="dropdown">
        <a href="<c:url value="/logout" />"><i class="fa fa-fw fa-power-off"></i> <spring:message code="btn.logout"/></a>
    </li>
</ul>
