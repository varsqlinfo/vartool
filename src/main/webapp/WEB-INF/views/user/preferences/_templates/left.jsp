<%@ page language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/include/tagLib.jspf"%>

<ul class="nav left-menu">
	<li>
        <a href="<c:url value="/user/" />" style="text-align:center;">
        	<i class="fa fa-home"></i><spring:message code="database.screen" text="메인화면 가기"/>
        </a>
    </li>
    <li class="<c:if test="${fn:endsWith(originalURL,'/user/preferences')}"> active</c:if>">
        <a href="<c:url value="/user/preferences?header=${headerview}" />">
        	<spring:message code="user.prefernces.menu.general" />
        </a>
    </li>
    <li class="<c:if test="${fn:endsWith(originalURL,'/preferences/password')}"> active</c:if>">
        <a href="<c:url value="/user/preferences/password?header=${headerview}" />">
        	<spring:message code="user.prefernces.menu.pasword" />
        </a>
    </li>
</ul>