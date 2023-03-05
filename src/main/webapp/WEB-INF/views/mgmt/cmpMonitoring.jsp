<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/tagLib.jspf"%>
<div class="col-lg-12">
	<h1>
		Log Monitoring
	</h1>
</div>
<div class="display-off" id="appViewArea">
	<div class="col-lg-6">
		<div class="panel panel-default">
			<div class="panel-body" >
				<div class="list-group">
					<div class="pull-right" style=" padding: 5px 0px;">
						<span style="color: #ec4343;font-weight: bold;">{{message}}</span><span style="padding-right:10px;">{{refreshTime > 0 ? refreshTime : 'refresh...' }}</span><span class="fa fa-refresh">Refresh Sec</span>
						<select v-model="refreshSettingTime" @change="changeRefreshTime()">
							<option v-for="(item,index) in refreshSettingArr" :value="item">{{item}}</option>
						</select>
						초
						<button type="button" @click="stopStart()">{{isStart ?'stop':'start'}}</button>
					</div>
					<table
						class="table table-striped table-bordered table-hover dataTable no-footer"
						id="dataTables-example" style="table-layout:fixed;width:100%;">
						<thead>
							<tr role="row">
								<th style="width: 90px;">
									타입
								</th>
								<th style="width: 120px;">
									컴포넌트명
								</th>
								<th style="width:*;min-width:50px;">
									로그 정보
								</th>
								<th style="width: 60px;">
									Size
								</th>
								<th style="width: 50px;">
									상태
								</th>
								<th style="width: 150px;">
									&nbsp;
								</th>
							</tr>
						</thead>
						<tbody>
							<tr v-for="(item,index) in gridData" class="gradeA" :class="(index%2==0?'add':'even')" :data-group-id="item.logItemUid != item.logUid ? item.logItemUid : ''">
								<td>
									{{item.cmpType}}
								</td>
								<td>
									<div class="text-ellipsis ellipsis3" :title="item.name">
										<a href="javascript:;" @click="logView(item)">{{item.name}}</a>
									</div>
								</td>
								<td>
									<div class="text-ellipsis ellipsis3" :title="item.path">{{item.path}}</div>
								</td>
								<td class="ta-r">
									<div :title="item.currentLogSize">{{item.currentLogSize}}</div>
								</td>
								<td class="ta-c">
									<span class="status-circle big" :class="item.running?'green':'red'" :title="item.running?'Start':'Stop'"></span>
								</td>
								<td>
									<template v-if="item.cmpType == 'LOG'">
										<button type="button" class="btn btn-xs btn-info" @click="clearInfo(item)">Clear</button>
										<button type="button" class="btn btn-xs btn-danger" @click="stopTail(item)">Stop</button>
										<button type="button" class="btn btn-xs btn-success " @click="startTail(item)">Start</button>
									</template>
									<template v-else-if="item.cmpType == 'COMMAND'">
										<button type="button" class="btn btn-xs btn-info" @click="killInfo(item)">Kill</button>
									</template>
								</td>
							</tr>
							<tr v-if="gridData.length === 0">
								<td colspan="5"><div class="text-center">데이타 없음</div></td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
	<div class="col-lg-6">
		<div id="logViewer" style="height:500px;"></div>
	</div>
</div>
					
<div id="errorMsg"></div>

<script>
(function() {
	
VartoolAPP.vueServiceBean({
	el: '#appViewArea'
	,data: {
		list_count :10
		,searchVal : ''
		,refreshSettingArr : [5,10,15,20,25,30,35,40,45,50,55,60]
		,refreshSettingTime : 5
		,refreshTime : -1
		,pageInfo : {}
		,gridData :  []
		,detailItem :{}
		,message:''
		,refreshTimer : -1
		,isStart : true
		,allComponentInfos : {}
		,cmpCheckInfo : {}
		,logViewer : false
	}
	,methods:{
		init : function (){
			this.logInfoList();
			
			this.logViewer = $.pubLogViewer('#logViewer',{
				itemMaxCount : 10000
				,items : []
				,contextMenu : [
					{key : 'sqlFormatView', name :'Sql Format View', callback: function (){
						getFormatSql(_this.logElement[logId].selectionData());
					}}
				]
			});
		}
		,stopStart : function (){
			this.isStart = !this.isStart; 
			
			if(this.isStart){
				this.changeRefreshTime();
			}else{
				this.refreshTime =0;
				clearInterval(this.refreshTimer);
			}
		}
		,logView: function (item){
			var _this =this; 
			
			this.$ajax({
				url: {type:VARTOOL.uri.manager, url:'/cmpMonitoring/logLoad'}
				,data : {
					cmpId : item.cmpId
					,cmpType : item.cmpType
				}
				,loadSelector : '#logViewer'
				,success: function(resData) {
					var item = resData.item;
					
					try{
						_this.logViewer.setStatus('start');
						_this.logViewer.clearLog();
						_this.logViewer.setData(item.logList);
					}catch(e){
						console.log(e);
					}
					
				}
				,error :  function (){
					console.log(arguments);
				}
			})
		}
		// refresh
		,changeRefreshTime: function(){
			var _this =this; 
			
			clearInterval(this.refreshTimer);
			
			this.refreshTime = this.refreshSettingTime;
			
			this.refreshTimer = setInterval(function() {
				--_this.refreshTime;
				
				if(_this.refreshTime < 1){
					_this.checkComponent(function (result){
						
						if(result=='error'){
							clearInterval(_this.refreshTimer);
							_this.message = '페이지를 새로고침 해주세요.'
							return ; 
						}
						_this.changeRefreshTime()
					});
				}
			}, 1000);
			
		}
		,logInfoList : function(){
			var _this = this; 
			
			var param = {
				page: 1
				,rows: _this.list_count
				,'searchVal':_this.searchVal
			};
			
			this.$ajax({
				url: {type:VARTOOL.uri.manager, url:'/cmpMonitoring/list'}
				,data : param
				,success: function(resData) {
					var items = resData.list;
					
					var allComponentInfos= {};
					var cmpCheckInfo = {};
					
					for(var i=0 ,len = items.length; i<len; i++){
						var item = items[i];
						cmpCheckInfo[item.cmpId] = item.cmpType;
						allComponentInfos[item.cmpId] = item; 
					}
					
					_this.gridData = allComponentInfos;
					
					_this.cmpCheckInfo = cmpCheckInfo;
					_this.allComponentInfos = allComponentInfos;
					_this.changeRefreshTime();
				}
				,error :  function (){
					console.log(arguments);
					if(VARTOOL.isFunction(callback)){
						callback('error');
					}
				}
			})
		}
		,checkComponent :  function (callback){
			var _this =this; 
			var param = {
				cmpInfo : JSON.stringify(this.cmpCheckInfo)
			};
			
			this.$ajax({
				url: {type:VARTOOL.uri.manager, url:'/cmpMonitoring/checkComponent'}
				,data : param
				,async : false
				,success: function(resData) {
					var items = resData.list;
					
					for(var i=0 ,len = items.length; i<len; i++){
						var item = items[i];
						VARTOOL.util.objectMerge(_this.allComponentInfos[item.cmpId], item); 
					}
					
					if(VARTOOL.isFunction(callback)){
						callback('success');
					}
				}
				,error :  function (e){
					console.log(arguments);
					if(VARTOOL.isFunction(callback)){
						callback('error');
					}
				}
			})
		}
		,childClose : function (item){
			item.isClose = !item.isClose;
			
			if(item.isClose){
				$('[data-group-id="'+item.logItemUid+'"]').hide();
			}else{
				$('[data-group-id="'+item.logItemUid+'"]').show();
			}
		}
		,clearInfo : function (item){
			var _this = this; 
			
			if(!confirm('로그가 초기화 됩니다\nclear 하시겠습니까?')){
				return ; 
			}
			
			var param = {
				cmpId : item.cmpId
				,type : item.cmpType
			}
			
			this.$ajax({
				url: {type:VARTOOL.uri.manager, url:'/cmpMonitoring/clearLog'}
				,data : param
				,success: function(resData) {
					_this.checkComponent();
				}
			})
		}
		,stopTail : function (item){
			var _this = this; 
			
			if(!confirm('stop 하시면 실시간 로그를 확인활수 없습니다.\ntail 로그를 stop 하시겠습니까?')){
				return ; 
			}
			
			var param = {
				cmpId : item.cmpId
			}
			
			this.$ajax({
				url:  {type:VARTOOL.uri.manager, url:'/cmpMonitoring/stopTail'}
				,data : param
				,success: function(resData) {
					_this.checkComponent();
				}
			})
		}
		,startTail : function (item){
			var _this = this; 
			
			if(!confirm('tail 로그를 start 하시겠습니까?')){
				return ; 
			}
			
			var param = {
				cmpId : item.cmpId
			}
			
			this.$ajax({
				url:  {type:VARTOOL.uri.manager, url:'/cmpMonitoring/startTail'}
				,data : param
				,success: function(resData) {
					if(resData.item =='running'){
						VARTOOLUI.toast.open({text:'이미실행중입니다.'})
					}else{
						_this.checkComponent();
					}
				}
			})
		}
		,killInfo : function (item){
			var _this = this; 
			
			if(!confirm('process를 kill 하시겠습니까?')){
				return ; 
			}
			
			var param = {
				cmpId : item.cmpId
			}
			
			this.$ajax({
				url: {type:VARTOOL.uri.manager, url:'/cmpMonitoring/killProcess'}
				,data : param
				,success: function(resData) {
					if(resData.item =='running'){
						VARTOOLUI.toast.open({text:'이미실행중입니다.'})
					}else{
						_this.checkComponent();
					}
				}
			})
		}
	}
});


}());

</script>