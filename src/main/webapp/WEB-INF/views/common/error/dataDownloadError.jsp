<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<textarea id="errorMessageArea" style="display:none;">${errorMessage}</textarea>
<script>
var viewWin = window; 
if(parent){
	viewWin = parent;
}

viewWin.VARTOOL.alertMessage(document.getElementById('errorMessageArea').value);
</script>
