<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %>
<%@ page import=" java.util.*, java.io.*" %>
<%@ include file="/WEB-INF/include/tagLib.jspf"%>

<!DOCTYPE html>
<HTML>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title><spring:message code="home.title"/> ~ Sign</title>
<%@ include file="/WEB-INF/include/head-meta.jspf"%>
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
    <h3 class="page-header"><spring:message code="home.title"/> <spring:message code="join.form.title"/></h3>
    <!-- form start -->
    <form name="joinForm" id="joinForm" method="POST" action="<c:url value='/join/join' />"  class="form-horizontal well" role="form" onsubmit="return false;">

        <div class="form-group">
            <label for="inputEmail3" class="col-sm-3 control-label"><spring:message code="join.form.uid"/></label>

            <div class="col-sm-6 col-md-6">
                <input type="text" class="form-control required" id="uid" name="uid" placeholder="<spring:message code="join.form.uid" />" />
            </div>
        </div>
         <div class="form-group">
            <label class="col-sm-3 control-label"><spring:message code="join.form.name"/></label>

            <div class="col-sm-6 col-md-6">
                <input type="text" class="form-control" id="uname" name="uname" placeholder="<spring:message code="join.form.name"/>"/>
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-3 control-label"><spring:message code="join.form.email"/></label>

            <div class="col-sm-6 col-md-6">
                <input type="email" class="form-control" id="uemail" name="uemail" placeholder="<spring:message code="join.form.email" />"/>
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-3 control-label"><spring:message code="join.form.organization"/></label>

            <div class="col-sm-6 col-md-6">
                <input type="text" class="form-control" name="orgNm" placeholder="<spring:message code="join.form.organization"/>"/>
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-3 control-label"><spring:message code="join.form.password"/></label>

            <div class="col-sm-6 col-md-6">
                <input type="password" class="form-control" id="upw" name="upw" placeholder="<spring:message code="join.form.password"/>" />
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-3 control-label"><spring:message code="join.form.password.confirm"/></label>

            <div class="col-sm-6 col-md-6">
                <input type="password" class="form-control" id="confirmUpw" name="confirmUpw" placeholder="<spring:message code="join.form.password.confirm" />" />
            </div>
        </div>
    </form>
    <div class="form-group">
          <div class="col-sm-12 text-center">
              <button type="button" class="btn btn-info btn-join"><spring:message code="btn.join"/></button>
              <button type="button" class="btn btn-default btnMain"><spring:message code="btn.main"/></button>
          </div>
      </div>

    <div style="display:none;">
		<form name="f" action="${vartoolfn:loginUrl(pageContext.request)}" method="post" onsubmit="return false;">
			<input type="text" id="vtool_login_id" name="vtool_login_id" value="">
			<input type="password" id="vtool_login_password" name="vtool_login_password" value="">
			<input type="hidden" id="join_success" name="join_success" value="Y">
		</form>
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
			location.href ='<c:url value="/" />';
		});

		$('.btn-join').on('click',function (){
			$('#joinForm').submit();
		});

		var idChecVal = -1;
		$('#uid').focusout(function(e) {
			
			var tmpVal =  $.trim($(this).val());

			$(this).val(tmpVal);

			VARTOOL.req.ajax({
				url: {type:VARTOOL.uri.join, url:'/idCheck'},
				data:{
					uid : $.trim(tmpVal)
				},
				success: function(resData) {
					if(resData.item  > 0){
						idChecVal = 1;
					}else{
						idChecVal = 0;
					}
					$('#joinForm')
				    .data('bootstrapValidator')
				    .updateStatus('uid', 'VALIDATING')
				    .validateField('uid');
				}
			});
		});

		var emailChecVal = -1;

		$('#uemail').focusout(function(e) {

			var tmpVal =  $.trim($(this).val());

			$(this).val(tmpVal);

			VARTOOL.req.ajax({
				url: {type:VARTOOL.uri.join, url:'/emailCheck'},
				data:{
					uemail : $.trim(tmpVal)
				},
				success: function(resData) {
					if(resData.item  > 0){
						emailChecVal = 1;
					}else{
						emailChecVal = 0;
					}
					$('#joinForm')
				    .data('bootstrapValidator')
				    .updateStatus('uemail', 'VALIDATING')
				    .validateField('uemail');
				}
			});
		});

		$('#joinForm').bootstrapValidator({
			message: 'This value is not valid',
			feedbackIcons: {
				valid: 'glyphicon glyphicon-ok',
				invalid: 'glyphicon glyphicon-remove',
				validating: 'glyphicon glyphicon-refresh'
			}
			,fields: {
				uid: {
					validators: {
						notEmpty: { message: '필수 입력사항입니다.'}
						,stringLength: { min: 3, max: 100, message: '사이즈는 3~100 사이여야 합니다'}
						,callback: {
		                     message: '이미 존재하는 아이디 입니다.',
		                     callback: function (value, validator, $field) {
		                   	  	return idChecVal < 1;
		                     }
		                }
					}
			  	}
				,uname: {
					validators: {
						notEmpty: { message: '필수 입력사항입니다.'}
						,callback: {
		                     message: '크기는 2~100 사이여야 합니다',
		                     callback: function (value, validator, $field) {
		                   	  	return $.trim(value).length > 1;
		                     }
		                 }
					}
			  	}
				,uemail: {
					validators: {
						notEmpty: { message: '필수 입력사항입니다.'}
						,stringLength: { min: 0, max: 500, message: '크기는 3~100 사이여야 합니다'}
						,emailAddress: {
							message: 'The input is not a valid email address'
						}
						,callback: {
		                     message: '이미 존재하는 이메일 입니다.',
		                     callback: function (value, validator, $field) {
		                   	  	return emailChecVal < 1;
		                     }
		                }
				  }
			  	}
				,upw: {
	                validators: {
	                    notEmpty: {
	                        message: '필수 입력사항입니다.'
	                    }
	                    ,different: {
	                        field: 'uid',
	                        message: '아이디와 달라야 합니다.'
	                    }
	                    ,identical: {
	                        field: 'confirmUpw',
	                        message: '비밀번호가 같아야합니다.'
	                    }
	                    ,stringLength: {
	                        min: 4,
	                        max: 500,
	                        message: '최소 4글자 이상 이여야 합니다.'
	                    }
	                }
	            }
	            ,confirmUpw: {
	                validators: {
	                    notEmpty: {
	                        message: '필수 입력사항입니다.'
	                    }
	                    ,identical: {
	                        field: 'upw',
	                        message: '비밀번호가 같아야합니다.'
	                    }
	                }
	            }

			}
		}).on('success.form.bv', function(e) {
			// Prevent form submission
			e.preventDefault();

			_this.saveInfo();
		});
	}
	,saveInfo: function (){
		var params  =$('#joinForm').serializeJSON();

		VARTOOL.req.ajax({
			url: {type:VARTOOL.uri.join, url:'/save'},
			data:params,
			success: function(resData) {
				if(!VARTOOL.req.validationCheck(resData)){
					return ;
				}else{
					if(resData.code ==409){
						VARTOOL.alertMessage('아이디  중복 입니다.');
						return ;
					}
				}
				
				$('#vtool_login_id').val( $('#uid').val());
				$('#vtool_login_password').val($('#upw').val());

				document.f.submit();
			},
			error: function(xhr, status, e) {
				VARTOOL.log(status + " : " + e + xhr.responseText);
			}
		});
	}
}

joinForm.init();
}());

</script>
