<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/tagLib.jspf"%>
<style>
.main-container{
	width: 100%;
	height :100%;
}
.side-container{
	width:0px;
	height:100%;	
}
</style>
<div id="mainPage" class="wh100">
	<div id="mainContainer" class="main-container"></div>
	<div class="main-spliter" data-prev-min-size="50" data-next-min-size="0"></div>
	<div class="side-container">
		<iframe id="mainArticleFrame" src="" style="width: 100%;height: 100%;border: 0px solid transparent;"></iframe>
	</div>
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
	<textarea id="sqlFormatViewArea" class="wh100"></textarea>
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

var initFlag = false; 
$.pubSplitter('.main-spliter',{
	orientation: 'vertical'
	,handleSize : 10
	,border :true
	,useButton : true		// 한번에 이동 버튼 사용유무
	,percent: {vertical:true}
	,button : {					// button option
		enabled : true			// enabled 활성화 여부 default true
		,toggle : true			// button min , max button 토글로 하나 보일지 두개 보일지 여부.
		,click : function (mode){	// click callback
			if(mode=='prev' && initFlag === false){
				initFlag = true; 
				$('#mainArticleFrame').attr('src','<vartool:boardUrl boardCode="${cmpGroupInfo.groupId}"/>');
			}
		}
	}
	,start :function (){
		if(initFlag === false){
			initFlag = true; 
			$('#mainArticleFrame').attr('src','<vartool:boardUrl boardCode="${cmpGroupInfo.groupId}"/>');
		}
	}
	,stop:function (){
		VARTOOL.ui.layoutResize();
	}
});


}());


</script>

