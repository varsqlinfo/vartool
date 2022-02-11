<%@ page language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/include/initvariable.jspf"%>

<%double version = java.lang.Math.round(java.lang.Math.random() * 20000); %>
<!-- Bootstrap Core CSS -->
<link href="${pageContextPath}/webstatic/css/bootstrap.min.css" rel="stylesheet">

<!-- Custom Fonts -->
<link href="${pageContextPath}/webstatic/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">

<link href="${pageContextPath}/webstatic/css/layout/goldenlayout-base.css" rel="stylesheet" type="text/css">
<link href="${pageContextPath}/webstatic/css/layout/goldenlayout-light-theme.css" rel="stylesheet" type="text/css">

<link href="${pageContextPath}/webstatic/css/pub.all.min.css" rel="stylesheet">
<link href="${pageContextPath}/webstatic/css/jquery.toast.min.css" rel="stylesheet">
<link href="${pageContextPath}/webstatic/css/pub-logviewer.min.css?v=<%=version %>" rel="stylesheet" >
<link href="${pageContextPath}/webstatic/css/pub.splitter.min.css?v=<%=version %>" rel="stylesheet" >

<link href="${pageContextPath}/webstatic/css/vt-tool.min.css?v=<%=version %>" rel="stylesheet">

<script src="${pageContextPath}/webstatic/js/jquery-3.3.1.min.js"></script>
<script src="${pageContextPath}/webstatic/js/jquery-ui.min.js"></script>
<script src="${pageContextPath}/webstatic/js/bootstrap.min.js"></script>
<script src="${pageContextPath}/webstatic/js/plugins/logFormatter/sql-formatter.min.js"></script>
<script src="${pageContextPath}/webstatic/js/plugins/filesaver/FileSaver.min.js"></script>
<script src="${pageContextPath}/webstatic/js/plugins/notify/sockjs.min.js"></script>
<script src="${pageContextPath}/webstatic/js/plugins/notify/stomp.min.js"></script>
<script src="${pageContextPath}/webstatic/js/plugins/toast/jquery.toast.min.js"></script>
<script src="${pageContextPath}/webstatic/js/plugins/layout/goldenlayout.min.js"></script>

<script src="${pageContextPath}/webstatic/js/pub.ep.js"></script>
<script src="${pageContextPath}/webstatic/js/pub.tab.js"></script>
<script src="${pageContextPath}/webstatic/js/pub.context.js"></script>
<script src="${pageContextPath}/webstatic/js/pub.logviewer.js?v=<%=version %>" charset="utf-8"></script>
<script src="${pageContextPath}/webstatic/js/pub.splitter.js?v=<%=version %>" charset="utf-8"></script>

<script src="${pageContextPath}/webstatic/i18n/vartool.lang.js?v=<%=version %>"></script>

<script src="${pageContextPath}/webstatic/js/vue.min.js"></script>

<script src="${pageContextPath}/webstatic/js/vartool.web.js"></script>

<script src="${pageContextPath}/webstatic/js/plugins/handlebars/handlebars-v4.7.6.js"></script>
<script src="${pageContextPath}/webstatic/js/plugins/handlebars/handlebars.tool.js"></script>

<script src="${pageContextPath}/webstatic/js/vue.vartool.js?v=<%=version %>"></script>
<script src="${pageContextPath}/webstatic/js/vartool.web.ui.js?v=<%=version %>"></script>

<script src="${pageContextPath}/webstatic/js/vartool.main.ui.js?v=<%=version %>"></script>
