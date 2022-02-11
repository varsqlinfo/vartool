<%@ page language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/include/tagLib.jspf"%>

<div id="logo" class="navbar-header col-xs-12 col-sm-2">
	<a id="logo-title" href="javascript:location.reload();">
		<spring:message	code="home.title" />
	</a>

	<div class="pull-right">
		<a href="#" class="show-sidebar"> <i class="fa fa-bars"></i></a>
	</div>
</div>
<!-- Top Menu Items -->
<div id="top-panel" class="col-xs-12 col-sm-10">
	<div class="pull-right">
		<ul class="nav navbar-right top-nav">
			 <li class="dropdown">
		         <a href="javascript:;" class="dropdown-toggle btn-user-profile" data-toggle="dropdown">
		         	<sec:authentication property="principal.username" /> <b class="caret"></b>
		         </a>
		         <ul class="dropdown-menu">
		         	<%@ include file="/WEB-INF/include/screen.jsp"%>
		             <li>
		                 <a href="<c:url value="/logout" />"><i class="fa fa-fw fa-power-off"></i> <spring:message code="btn.logout"/></a>
		             </li>
		         </ul>
		     </li>
		</ul>
	</div>
</div>