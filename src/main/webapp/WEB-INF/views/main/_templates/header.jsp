<%@ page language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/include/tagLib.jspf"%>

<sec:authorize var="isManager" access="hasAnyAuthority('MANAGER','ADMIN')"/>
<div id="gnbArea">
	
	<span class="navbar-header" style="float: left;">
	    <a class="navbar-brand" href="javascript:location.reload()"><spring:message code="home.title"/></a>
	</span>
		
	<c:if test="${fn:length(userGroupList) > 0}">
	  <ul class="nav top-menu" style="float: left;">
		<li role="layout-parent">
			<a href="javascript:;" class="header-toggle-btn">Log</a>
			<table class="header-layout-info header-menu" style="left:0px">
	        	<tbody>
	 			<c:forEach var="item" items="${userGroupList}" varStatus="status">
					<tr>
						<td class="name">
							<c:choose>
								<c:when test="${item.groupId eq cmpGroupInfo.groupId}">
									<span>${item.groupName}</span>
									<a href="javascript:;" @click="viewCmpGroup('${item.groupId}', 'new')" class="pull-right">새창보기</a>
								</c:when>
								<c:otherwise>
									<a href="javascript:;" @click="viewCmpGroup('${item.groupId}')">${item.groupName}</a>
								</c:otherwise>
							</c:choose>
						</td> 
					</tr>
				</c:forEach>
				</tbody>
	        </table>
	    </li>
	 </ul>
	</c:if>
	<!-- Top Menu Items -->
	<ul class="nav top-menu" style="float:right !important;">
		<li id="gnbSocketStatus"></li>
		<sec:authorize var="isAdmin" access="hasAnyAuthority('ADMIN')"/>
		<c:if test="${isAdmin}">
			<li>
		        <a href="javascript:;" @click="defaultLayout()">기본 레이아웃<i class="fa fa-share-alt"></i></a>
		    </li>
	    </c:if>
	    
	    <%--log list start --%>
	    <li role="layout-parent" v-if="logList.length > 0">
	        <a href="javascript:;" class="header-toggle-btn">
	        	로그 <i class="fa fa-eye"></i>
	        </a>
	        
	        <table class="header-layout-info">
	        	<thead>
	        		<tr>
		        		<td colspan="2" class="header-layout-title">Log List</td>
		        	</tr>
	        	</thead>
	        	
	        	<tbody>
		        	<tr v-for="(item,index) in logList">
						<td class="name">- {{item.name}}</td> 
						<td class="event-btn-area">
							<button class="btn btn-default btn-sm" @click="downloadComponentItem('appLog',item)" title="Download"><i class="fa fa-download"></i></button>
							<button class="btn btn-success btn-sm" @click="addComponentItem('appLog',item, 'view')" title="View">View</button>
						</td>
					</tr>
				</tbody>
	        </table>
	    </li>
	    <%--log list end --%>
	    
	     <%--deploy list start --%>
	    <li role="layout-parent" v-if="deployList.length > 0">
	        <a href="javascript:;" class="header-toggle-btn">
	        	Deploy <i class="fa fa-cloud-upload"></i>
	        </a>
	        
	        <table class="header-layout-info">
	        	<thead>
	        		<tr>
		        		<td colspan="2" class="header-layout-title">Deploy</td>
		        	</tr>
	        	</thead>
	        	<tbody>
		        	<tr v-for="(item,index) in deployList">
						<td class="name">- {{item.name}}</td> 
						<td class="event-btn-area">
							<button class="btn btn-default btn-sm" @click="downloadComponentItem('appLog',item)" title="Download"><i class="fa fa-download"></i></button>
							<button class="btn btn-default btn-sm" @click="deployAction(item, 'view')" title="pull">보기</button>
							<c:if test="${isManager}">
								<button class="btn btn-success btn-sm" @click="deployAction(item, 'pull')" title="pull">pull</button>
								<button class="btn btn-primary btn-sm" @click="deployAction(item, 'all')" title="pull and deploy">p&d</button>
							</c:if>
						</td>
					</tr>
				</tbody>
	        </table>
	    </li>
	    <%--deploy list end --%>
	    
	    <%-- Command list start --%>
	    <li role="layout-parent" v-if="commandList.length > 0">
	        <a href="javascript:;" class="header-toggle-btn">
	        	Command<i class="fa fa-wrench"></i>
	        </a>
	        
	        <table class="header-layout-info">
	        	<thead>
	        		<tr>
		        		<td colspan="2" class="header-layout-title">Command</td>
		        	</tr>
	        	</thead>
	        	<tbody>
	        		<tr v-for="(item,index) in commandList">
						<td class="name">- {{item.name}}</td>
						<td class="event-btn-area">
							<button class="btn btn-default btn-sm" @click="downloadComponentItem('appLog',item)" title="Download"><i class="fa fa-download"></i></button>
							<button class="btn btn-default btn-sm" @click="cmd(item, 'stop', 'view')">log</button>
							<c:if test="${isManager}">
								<button class="btn btn-danger btn-sm" @click="cmd(item, 'stop')">stop</button>
								<button class="btn btn-success btn-sm" @click="cmd(item, 'start')">start</button>
							</c:if>
						</td>
					</tr>
				</tbody>
	        </table>
	    </li>
	    <%-- Command list end --%>
	    
	     <li role="layout-parent">
	        <a href="javascript:;" @click="boardToggle()" class="header-toggle-btn">
	        	게시판
	        </a>
	    </li>
	    
	    <li role="layout-parent">
	        <a href="javascript:;" class="header-toggle-btn">
	        	<sec:authentication property="principal.username" /> <i class="fa fa-user"></i>
	        </a>
	        <ul class="top-setting-menu dropdown-menu pull-right">
	            <li>
	            	<div class="ta-c">테마</div>
					<a href="javascript:;" @click="changeTheme('dark')"> Dark </a> 
					<a href="javascript:;" @click="changeTheme('light')"> Light </a>
				</li>
				<li class="divider"></li>	
				<li>
					<a href="javascript:;" @click="layoutInit()">레이아웃 초기화<i class="fa fa-undo"></i></a>
				</li>
	            <li class="divider"></li>
	            <jsp:include page="/WEB-INF/include/screen.jsp" flush="false">
					<jsp:param name="popup_yn" value="y" />
				</jsp:include>
				<li>
					<a href="<c:url value="/user/preferences" />" target="_blank" class="preferences"><i class="fa fa-fw fa-user"></i> <spring:message code="label.user.preferences"/></a>
				</li>
				<li class="divider"></li>

	            <li>
	                <a href="<c:url value="/logout" />"><i class="fa fa-fw fa-power-off"></i> <spring:message code="btn.logout"/></a>
	            </li>
	        </ul>
	    </li>
	</ul>
</div>


<script>
(function() {
	
VartoolAPP.vueServiceBean({
	el: '#gnbArea'
	,data: {
		list_count :10
		,searchVal : ''
		,searchType: ''
		,deployStr : 'stop'
		,deployList : []
		,logList : []
		,cmpGroupInfo : ${vartoolfn:objectToJson(cmpGroupInfo)}||{}
		,commandList : []
		,initBoard : false
		,showBoard : false // 게시판 보기 여부
	}
	,created : function (){
		var userCmpMap = ${userCmpMap}; 
		
		this.deployList = userCmpMap.DEPLOY||[]; 
		this.logList = userCmpMap.LOG||[]; 
		this.commandList = userCmpMap.COMMAND||[]; 
	}
	,methods:{
		init : function (){
			var _this =this; 
			
			window.name = this.cmpGroupInfo.groupId;
		}
		,viewCmpGroup : function (groupId, mode){
			var popupName =  (mode||'')+groupId; 
			VARTOOLUI.popup.open(VARTOOL.getContextPathUrl('/main/'+groupId),{
				name :popupName
			})
		}
		,addComponentItem : function (type, viewItem, mode){
			VARTOOL.ui.addComponent(type, VARTOOL.util.objectMerge({id: viewItem.cmpId, title: viewItem.name}, viewItem));
		}
		,layoutInit : function (){
			if(!confirm('레이아웃을 초기화하면 화면 구성이 초기화 됩니다.\n레이아웃을 초기화하시겠습니까?')) return ;
			
			VARTOOL.req.ajax({
				url: {type:VARTOOL.uri.ignore, url:'/pref/initLayout?groupId='+ this.cmpGroupInfo.groupId}
				,method:'get'
				,success: function(resData) {
					location.href = location.href;
				}
			})
		}
		// source clean and deply
		,deployAction : function (item, action){
			
			this.addComponentItem('deployLog', item, 'view')
			
			if(action=='view') return ;
			
			var param = VARTOOL.util.objectMerge({action : action}, item);
			
			this.$ajax({
				url: {type:VARTOOL.uri.cmp, url:'/deploy/action'}
				,data : param
				,loadSelector : VARTOOL.ui.getLoadSelector('deployLog', item.cmpId)
				,success: function(resData) {
					var msg = resData.message;
					
					if(msg !=null  && msg !=''){
						VARTOOLUI.toast.open({text : msg});
					}
				}
			})
		}
		,cmd : function (item, action, mode){
			
			this.addComponentItem('commandLog', item, 'view');
			
			if(mode=='view') return ; 
			
			var param = VARTOOL.util.objectMerge({action : action}, item);
			
			this.$ajax({
				url: {type:VARTOOL.uri.cmp, url:'/command/startAndStop'}
				,data : param
				,loadSelector :VARTOOL.ui.getLoadSelector('commandLog', item.cmpId)
				,success: function(resData) {
					var msg = resData.message;
					
					if(msg !=null  && msg !=''){
						VARTOOLUI.toast.open({text : msg});
					}
				}
			})
		}
		,changeTheme: function (theme){
			VARTOOL.ui.setTheme(theme);
		}
		,defaultLayout :function (){
			
			if(!confirm('기본 레이아웃을 지정하시겠습니까?')) return ; 
			
			this.$ajax({
				url: {type:VARTOOL.uri.ignore, url:'/pref/defaultLayout'}
				,data : {
					groupId : this.cmpGroupInfo.groupId
					,prefKey : 'main_layout' 
					,prefVal : VARTOOL.ui.currentLayout()
				}
				,success: function(resData) {
					
				}
			})
		}
		,downloadComponentItem : function (type, viewItem){
			if(!confirm('다운로드 하시겠습니까?')) return ; 
			
			VARTOOL.req.download({
				url : VARTOOL.url('/cmp/download')
				,params : {
					cmpId : viewItem.cmpId
					,cmpType : viewItem.cmpType
				}
			});
			
		}
		// 게시판 토글
		,boardToggle : function (){
			if(this.initBoard === false){
				$('#mainArticleFrame').attr('src','<vartool:boardUrl boardCode="${cmpGroupInfo.groupId}"/>');	
				this.initBoard = true; 
			}
			
			this.showBoard = !this.showBoard;
			
			if(this.showBoard){
				$('#boardWrapper').addClass('on');
			}else{
				$('#boardWrapper').removeClass('on');
			}
			
		}
	}
});


setTimeout(function() {
	var conChkTimer = setInterval(function() {
		if(VARTOOL.socket.isCreate === true && VARTOOL.socket.isConnect() ===false){
			clearInterval(conChkTimer);
			$('#gnbSocketStatus').empty().html('<a href="" style="color: #ff3400;font-weight: bold;">Web Socket 연결이 해제 되었습니다. 새로고침 해주세요.</a>')
		};
	}, 5000);
}, 60000);

}());

</script>
        
<script>
$('.header-toggle-btn').on('click', function (){
	var liParentEl = $(this).closest('li'); 
	if(liParentEl.hasClass('open')){
		liParentEl.removeClass('open');
	}else{
		liParentEl.addClass('open');
	}
})

// background click check
$(document).on('mousedown.vartech.background', function (e){
	if(e.which !==2){
		var targetEle = $(e.target);
		var pubGridLayterEle = targetEle.closest('[role="layout-parent"].open');
		if(pubGridLayterEle.length < 1 ){
			$('[role="layout-parent"].open').removeClass('open');
		}else{
			$('[role="layout-parent"].open').each(function (){
				var sEle = $(this);

				if(pubGridLayterEle[0] != sEle[0]){
					sEle.removeClass('open');
				}
			})
		}
	}
})
</script>

