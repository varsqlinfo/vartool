<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %>
<%@ page import=" java.util.*, java.io.*" %>
<%@ include file="/WEB-INF/include/tagLib.jspf"%>

<!DOCTYPE html>
<HTML>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title><spring:message code="home.title"/> ~ Login</title>
<head>
<script src="${pageContextPath}/webstatic/js/jquery-3.3.1.min.js"></script>
<script src="${pageContextPath}/webstatic/js/bootstrap.min.js" type="text/javascript"></script>
<script src="${pageContextPath}/webstatic/js/bootstrapValidator.js" type="text/javascript"></script>

<!-- Bootstrap Core CSS -->
<link href="${pageContextPath}/webstatic/css/bootstrap.min.css" rel="stylesheet">

<!-- Custom Fonts -->
<link href="${pageContextPath}/webstatic/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">


<style>
body {
  padding-top: 40px;
  padding-bottom: 40px;
  background-color: #eee;
}

.form-signin {
  max-width: 330px;
  padding: 15px;
  margin: 0 auto;
}
.form-signin .form-signin-heading,
.form-signin .checkbox {
  margin-bottom: 10px;
}
.form-signin .checkbox {
  font-weight: normal;
}
.form-signin .form-control {
  position: relative;
  height: auto;
  -webkit-box-sizing: border-box;
     -moz-box-sizing: border-box;
          box-sizing: border-box;
  padding: 10px;
  font-size: 16px;
}
.form-signin .form-control:focus {
  z-index: 2;
}
.form-signin input[type="email"] {
  margin-bottom: -1px;
  border-bottom-right-radius: 0;
  border-bottom-left-radius: 0;
}
.form-signin input[type="password"] {
  margin-bottom: 10px;
  border-top-left-radius: 0;
  border-top-right-radius: 0;
}


</style>
</head>
<body>
	<div class="container">
		<form name="f" action="${loginUrl}" method="post" class="form-signin" role="form">
			<h2 class="form-signin-heading">로그인</h2>
			
			<input class="form-control" id="vtool_login_id" name="vtool_login_id" type="text" placeholder="id" style="margin-bottom: 5px;" autofocus> 
			<input class="form-control" id="vtool_login_password" name="vtool_login_password" type="password" placeholder="password" value="">
			<div class="checkbox">
				<label>
					<input type="checkbox" id="rememberMe" value="remember-me">
					Remember me
				</label>
			</div>
			<c:if test="${login=='fail'}">
				<div class="error">
					<p>
						실패
					<p>
				</div>
			</c:if>
			<div style="padding-bottom:10px;">
				<button class="btn btn-lg btn-primary btn-block login-btn" type="button">
					로그인
				</button>
			</div>
			<div>
				<a href="./join/" class="btn btn-block btn-success"><spring:message code="btn.signup" /></a>
				
				<c:if test="${vartoolfn:isPasswordResetModeEmail()}">
					<span style="float:right;margin-top:10px;">
						<a href="<c:url value="/lostPassword" />"><spring:message code="msg.lost.password" /></a>
					</span>
				</c:if>
			</div>
			
			<!-- div class="text-center panel-footer">
				<a href="javascript:;" class="">아이디찾기</a>
				<a href="javascript:;" class="">비밀번호찾기</a>
				<a href="./join/" class="">회원가입</a>
			</div  -->
			
		</form>
	</div>
	<!-- /container -->
</body>

</html>
<script>
$(document).ready(function (){
	//document.f.submit();
	$('#vtool_login_password').keydown(function() {
		if(event.keyCode =='13') {
			$('.login-btn').trigger('click');
		};
	});
	
	if(localStorage.getItem('vtoolLoginID') && localStorage.getItem('vtoolLoginID') !=''){
		$('#rememberMe').prop('checked',true);
		$('#vtool_login_id').val(localStorage.getItem('vtoolLoginID'));
	}
	
	$('.login-btn').on('click', function (){
		
		if($('#rememberMe').is(':checked')){
			localStorage.setItem('vtoolLoginID', $('#vtool_login_id').val());
		}else{
			localStorage.removeItem('vtoolLoginID');
		}
		
		document.f.submit();
	})
})
</script>

<%
String id= "";
String pw = "";

String remoteAddr = request.getRemoteAddr();
if("127.0.0.1".equals(remoteAddr) 
		|| "0:0:0:0:0:0:0:1".equals(remoteAddr) ){
	id = "admin";
	pw = "admin1234!";
	
}

if(!"".equals(id)){
%>
<script>
$('#vtool_login_id').val('<%=id%>');
$('#vtool_login_password').val('<%=pw%>');
//$(".login-btn").trigger('click');
</script>
<%}%>