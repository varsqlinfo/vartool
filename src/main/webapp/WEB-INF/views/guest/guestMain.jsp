<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %>
<%@ page import=" java.util.*, java.io.*" %>
<%@ include file="/WEB-INF/include/tagLib.jspf"%>

<!DOCTYPE html>
<HTML>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title><spring:message code="home.title"/> ~ Sign</title>
<head>
<!-- Bootstrap Core CSS -->
<link href="${pageContextPath}/webstatic/css/bootstrap.min.css" rel="stylesheet">

<!-- Custom Fonts -->
<link href="${pageContextPath}/webstatic/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">


<script src="${pageContextPath}/webstatic/js/jquery-3.3.1.min.js"></script>
<script src="${pageContextPath}/webstatic/js/bootstrap.min.js" type="text/javascript"></script>
<script src="${pageContextPath}/webstatic/js/bootstrapValidator.js" type="text/javascript"></script>
<script src="${pageContextPath}/webstatic/js/jquery.serializeJSON.js"></script>

<script src="${pageContextPath}/webstatic/i18n/vartool.lang.js"></script>
<script src="${pageContextPath}/webstatic/js/vartool.web.js"></script>

</head>
<body>

<div class="container" style="margin: 20px auto 50px;">
    <h3 class="page-header" style="text-align:center;"><spring:message code="home.title"/></h3>
    <!-- form start -->
    
    <c:choose>
    	<c:when test="${param.join_success eq 'Y'}">
    		<div>회원 가입 되었습니다.<br>사용은 관리자 승인 후 사용 가능합니다.</div>
    	</c:when>
    	<c:otherwise>
    		<div>가입 승인이 되지 않았습니다. <br> 관리자에게 문의 하세요.</div>
    	</c:otherwise>
    </c:choose>
    	
    <div class="form-group">
          <div class="col-sm-12 text-center">
              <button type="button" class="btn btn-default btnMain"><spring:message code="btn.login"/></button>
          </div>
      </div>

    <!--/form-->
</div>

</body>
</html>

<script>
$(function (){
var joinForm = {
	init : function (){
		var _this = this;

		_this.initEvt();
	}
	,initEvt : function (){
		var _this = this;

		$('.btnMain').click(function (e){
			location.href ='<c:url value="/logout" />';
		});
	}
}

joinForm.init();
}());

</script>