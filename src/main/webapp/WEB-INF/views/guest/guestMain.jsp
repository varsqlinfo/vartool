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
    <h3 class="page-header"><spring:message code="home.title"/> <spring:message code="join.form.title"/></h3>
    <!-- form start -->
    ㅁㄴㅇㄹ
    <div class="form-group">
          <div class="col-sm-12 text-center">
              <button type="button" class="btn btn-info btn-join"><spring:message code="btn.join"/></button>
              <button type="button" class="btn btn-default btnMain"><spring:message code="btn.main"/></button>
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
						alert('아이디  중복 입니다.');
						return ;
					}
				}
				$('#vsql_login_id').val( $('#uid').val());
				$('#vsql_login_password').val($('#upw').val());

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