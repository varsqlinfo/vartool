<%@ page language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/include/initvariable.jspf"%>

<c:set var="randomVal" value="${vartoolfn:randomVal(20000)}" /> 

<!-- Bootstrap Core CSS -->
<link href="${pageContextPath}/webstatic/css/bootstrap.min.css" rel="stylesheet">

<!-- Custom Fonts -->
<link href="${pageContextPath}/webstatic/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">

<link href="${pageContextPath}/webstatic/css/jquery-ui.min.css" rel="stylesheet">
<link href="${pageContextPath}/webstatic/css/pub.all.min.css" rel="stylesheet">
<link href="${pageContextPath}/webstatic/css/jquery.toast.min.css" rel="stylesheet">

<link href="${pageContextPath}/webstatic/css/vt-app-style.min.css?v=${randomVal}" rel="stylesheet">

<script src="${pageContextPath}/webstatic/js/plugins/polyfill/polyfill.min.js"></script>
<script src="${pageContextPath}/webstatic/js/jquery-3.3.1.min.js"></script>
<script src="${pageContextPath}/webstatic/js/jquery-ui.min.js"></script>
<script src="${pageContextPath}/webstatic/js/plugins/toast/jquery.toast.min.js"></script>

<script src="${pageContextPath}/webstatic/js/pub.ep.js"></script>
<script src="${pageContextPath}/webstatic/js/pub.context.js"></script>

<script src="${pageContextPath}/webstatic/i18n/vartool.lang.js"></script>

<script src="${pageContextPath}/webstatic/js/vue.min.js"></script>

<script src="${pageContextPath}/webstatic/js/vartool.web.js"></script>

<script src="${pageContextPath}/webstatic/js/plugins/handlebars/handlebars-v4.7.6.js"></script>
<script src="${pageContextPath}/webstatic/js/plugins/handlebars/handlebars.tool.js"></script>

<script src="${pageContextPath}/webstatic/js/vue.vartool.js"></script>
<script src="${pageContextPath}/webstatic/js/vartool.web.ui.js"></script>