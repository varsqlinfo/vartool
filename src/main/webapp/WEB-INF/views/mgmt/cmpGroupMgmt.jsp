<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/tagLib.jspf"%>
<div class="col-lg-12">
	<h1>
		그룹 & Component 관리
	</h1>
</div>
<div class="display-off" id="varsqlViewArea">
	<div class="col-lg-5">
		<div class="panel panel-default">
			<div class="panel-body" >
				<div class="dataTables_filter">
					<label style="float:left; margin-right: 5px;"><select v-model="list_count" @change="search()" class="form-control"><option
							value="10">10</option>
						<option value="25">25</option>
						<option value="50">50</option>
						<option value="100">100</option></select>
					</label>
					<div class="input-group floatright">
						<input type="text" v-model="searchVal" class="form-control" @keyup.enter="search()" autofocus="autofocus" placeholder="Search...">
						<span class="input-group-btn">
							<button class="btn btn-default" @click="search()" type="button">
								<span class="glyphicon glyphicon-search"></span>
							</button>
						</span>
					</div>
				</div>
				<div class="list-group">
					<table
						class="table table-striped table-bordered table-hover dataTable no-footer"
						id="dataTables-example" style="table-layout:fixed;width:100%;">
						
						<thead>
							<tr role="row">
								<th style="width: *;">
									그룹
								</th>
								<th style="width: 50px;">
									&nbsp;
								</th>
							</tr>
						</thead>
						<tbody>
							<tr v-for="(item,index) in gridData" class="gradeA" :class="(index%2==0?'add':'even')">
								<td>
									<div class="text-ellipsis" :title="item.name">
										<a href="javascript:;" @click="viewItem(item)"> {{item.name}} </a>
									</div>
								</td>
								<td class="center">
									<button type="button" class="btn btn-warning btn_delete btn-xs" @click="deleteInfo(item)">삭제</button>
								</td>
							</tr>
							<tr v-if="gridData.length === 0">
								<td colspan="2"><div class="text-center">데이타 없음</div></td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
	<div class="col-lg-7">
		<div class="panel panel-default">
			<div class="panel-heading">
				<span>상세</span>
			</div>
			
			<div class="panel-body">
				<div>
					<form id="writeForm" name="writeForm" role="form" class="form-horizontal bv-form eportalForm">
						<div class="pull-right bottomHeight5">
							<button type="button" class="btn btn-default btn_add" :class="detailFlag==true?'':'hidden'" @click="fieldClear()">Add</button>
							<button type="button" class="btn btn-primary btn_save" @click="saveInfo()">Save</button>
						</div>
						<div style="clear:both;"></div>
						
						<div class="panel-body">
							<div class="form-horizontal">
								<div class="form-group">
									<div class="form-group">
										<label class="col-sm-4 control-label">그룹명</label>
										<div class="col-sm-8">
											<input class="form-control text required" v-model="detailItem.name">
										</div>
									</div>
									<div class="form-group">
										<label class="col-sm-4 control-label">설명</label>
										<div class="col-sm-8">
											<textarea class="form-control text" rows="3" v-model="detailItem.description" style="width:100%;"></textarea>
										</div>
									</div>
								</div>
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
		
		<div class="panel panel-default" :class="detailFlag ?'' :'hidden'" >
				<div class="panel-heading">Component 그룹맵핑</div>
				<!-- /.panel-heading -->
				<div class="panel-body">
					<div class="col-sm-5">
						<ul id="source" class="form-control" style="width:100%;height:200px;">
						  <li>데이타가 없습니다.</li>
						</ul>
					</div>
					<div class="col-sm-2" style="text-align:center;padding:10px;height:200px;">
						<div style="margin: 0 auto;">
							<a href="javascript:;" class="btn_m mb05" @click="moveItem('add')">
								추가<span class="fa fa-caret-right"></span>
							</a>
							<br/>
							<a href="javascript:;" class="btn_m mb05" @click="moveItem('del')">
								<span class="fa fa-caret-left"></span>삭제
							</a>
						</div>
					</div>
					<div class="col-sm-5">
						<ul id="target"  class="form-control" style="width:100%;height:200px;">
						  <li>데이타가 없습니다.</li>
						</ul>
					</div>
				</div>
				<!-- /.panel-body -->
			</div>
	</div>
</div>
					
<div id="errorMsg"></div>

<script>
(function() {
	
VartoolAPP.vueServiceBean({
	el: '#varsqlViewArea'
	,data: {
		list_count :10
		,searchVal : ''
		,pageInfo : {}
		,gridData :  []
		,detailItem :{}
		,detailFlag : false 
		,selectObj : {}
	}
	,methods:{
		init : function (){
			var _this =this; 
			
			_this.selectObj= $.pubMultiselect('#source', {
				targetSelector : '#target'
				,addItemClass:'text_selected'
				,useMultiSelect : true
				,useDragMove : false
				,useDragSort : false
				,duplicateCheck : true
				,message :{
					duplicate: VARTOOL.messageFormat('vartool.0018')
				}
				,sourceItem : {
					optVal : 'cmpId'
					,optTxt : 'name'
					,items : []
				}
				,targetItem : {
					optVal : 'cmpId'
					,optTxt : 'name'
					,items : []
				}
				,compleateSourceMove : function (moveItem){
					if($.isArray(moveItem)){
						_this.modifyMappingInfo('add', moveItem);
					}
				}
				,compleateTargetMove : function (moveItem){
					if($.isArray(moveItem)){
						_this.modifyMappingInfo('del', moveItem);
					}
				}
			}); 
			
			this.viewItem();
			this.initMappingInfo();
		}
		,moveItem : function (mode){
			var moveItem = [];
			if(mode =='add'){
				this.selectObj.sourceMove();
			}else{
				this.selectObj.targetMove();
			}
		}
		,search : function(no){
			var _this = this; 
			
			var param = {
				page:no?no:1
				,rows: _this.list_count
				,'searchVal':_this.searchVal
			};
			
			this.$ajax({
				url: {type:VARTOOL.uri.manager, url:'/cmpGroupMgmt/list'}
				,data : param
				,success: function(resData) {
					var items = resData.items;
					
					_this.gridData = resData.items;
					_this.pageInfo = resData.page;
				}
			})
		}
		,fieldClear : function (){
			this.viewItem();
		}
		,viewItem : function (item, typeChangeFlag){
			if(VARTOOL.isUndefined(item)){
				this.detailFlag = false;
				this.viewMode = 'save';
				this.detailItem ={
					name : ""
					,description : ""
					,groupId : ""
					,mode : "save"
				}
			}else{
				this.detailFlag = true; 
				
				item = VARTOOL.util.objectMerge({}, item);
				
				this.detailItem = item;
				this.detailItem.mode = "modify";
				this.mappingInfo();
			}
		}
		,saveInfo : function (){
			
			if(!confirm('저장하시겠습니까?')){
				return ; 
			}
		
			var _this = this; 
			
			var param = $.parseJSON(JSON.stringify(this.detailItem));
			
			this.$ajax({
				url: {type:VARTOOL.uri.manager, url:'/cmpGroupMgmt/save'}
				,data : param
				,success: function(resData) {
					_this.fieldClear();
					_this.search();
				}
			})
		}
		,deleteInfo : function(selectItem){
			var _this = this; 
			
			if(typeof selectItem.groupId ==='undefined'){
				return ;
			}
			
			if(!confirm('['+selectItem.name+']을 삭제 하시겠습니까?')){
				return ; 
			}
			
			this.$ajax({
				url: {type:VARTOOL.uri.manager, url:'/cmpGroupMgmt/remove'}
				,data : {
					groupId : selectItem.groupId 
				}
				,success: function(resData) {
					_this.fieldClear();
					_this.search();
				}
			})
		}
		// db mapping info
		,mappingInfo: function (){
			var _self = this;
			
			if(this.isViewMode ===false) return ; 
			
			var param = {
				groupId : this.detailItem.groupId
			};
			
			this.$ajax({
				data:param
				,loadSelector: '#main-content'
				,url : {type:VARTOOL.uri.manager, url:'/cmpGroupMgmt/mappingInfo'}
				,success:function (resData){
					var result = resData.items;
					
					for(var i =0 ;i <result.length;i++){
						var item = result[i];
						item.name = item.name+'('+item.cmpType+')';
					}
					
		    		_self.selectObj.setItem('target', result);
				}
			});
		}
		// 맵핑 정보 추가. 
		,modifyMappingInfo : function (mode, moveItem){
			var _self = this;
			
			if(this.isViewMode ===false) return ; 

			var param ={
				selectItem : moveItem.join(',')
				,groupId : this.detailItem.groupId
				, mode : mode
			};
			
			this.$ajax({
				data:param
				,url : {type:VARTOOL.uri.manager, url:'/cmpGroupMgmt/modifyMappingInfo'}
				,success:function (response){
					//_self.mappingInfo();
				}
			});
		}
		,initMappingInfo: function (){
			var _self = this;
			var param = {
				'searchVal':''
			};
			
			this.$ajax({
				url : {type:VARTOOL.uri.manager, url:'/cmpGroupMgmt/cmpList'}
				,data : param
				,success: function(resData) {
					var result = resData.items; 
					
					for(var i =0 ;i <result.length;i++){
						var item = result[i];
						item.name = item.name+'('+item.cmpType+')';
					}
					
					_self.selectObj.setItem('source', result);
				}
			})
		}
		// db mapp
	}
});


}());

</script>