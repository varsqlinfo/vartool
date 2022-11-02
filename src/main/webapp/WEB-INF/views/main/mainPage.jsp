<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/tagLib.jspf"%>
<div id="mainPage" class="wh100">
	<div id="mainContainer" class="main-container"></div>
</div>
<div id="boardWrapper" class="board-wrapper">
	<iframe id="mainArticleFrame" src="" style="width: 100%;height: 100%;border: 0px solid transparent;"></iframe>
</div>

<%--app log component template --%>
<script id="appLogComponent" type="text/vartool-template">
<div class="app-log-component wh100">
	<div component-viewerid="{{id}}" class="log-view-area full">
	</div>
</div>
</script>

<%--deploy log component template --%>
<script id="deployLogComponent" type="text/vartool-template">
<div class="deploy-log-component wh100">
	<div component-viewerid="{{id}}" class="log-view-area full">
	</div>
</div>
</script>

<%--command log component template --%>
<script id="commandLogComponent" type="text/vartool-template">
<div class="command-log-component wh100">
	<div component-viewerid="{{id}}" class="log-view-area full">
	</div>
</div>
</script>

<%--sql format view template --%>
<div id="sqlFormatDialog" title="Format View" class="sql-format-dialog wh100" style="display:none;overflow: hidden;">
	<textarea id="sqlFormatViewArea" class="wh100" style="resize: none;"></textarea>
</div>

<script>

(function() {
	
$(document).on('keydown',function (e) {
	var evt =window.event || e;
	if(evt.ctrlKey){
		var returnFlag = true;
		switch (evt.keyCode) {
			case 83: // keyCode 83 is s
				returnFlag = false;
				break;
			case 80: // 70 is f
				returnFlag = false;
				break;
			default:
				break;
		}
		return returnFlag;
	}
	return true;
}).on('dragover', function (){
	return false; 
}).on('drop', function (){
	return false; 
})

VARTOOL.ui.create({
	logItem : ${vartoolfn:objectToJson(cmpGroupInfo)}
	,articleSaveUrl : '<vartool:boardUrl boardCode="${cmpGroupInfo.groupId}" addUrl="save"/>'
	,mainSettingInfo : VARTOOL.util.objectMerge({layoutConfig:'',theme:'light'}, ${mainLayoutInfo})
	,userCmpMap : ${userCmpMap}
});

}());


</script>

