<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/tagLib.jspf"%>
<div class="col-lg-12">
	<h1>
		Log
	</h1>
</div>
<div class="display-off" id="appViewArea">
	<div class="col-lg-5">
		<div class="panel panel-default">
			<div class="panel-body">
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
									Name
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
							<template v-if="detailFlag===true">
								<button type="button" class="btn btn-default" @click="copyInfo()">Copy</button>
							</template>
							<button type="button" class="btn btn-primary btn_save" @click="saveInfo()">Save</button>
						</div>
						<div style="clear:both;"></div>
						
						<div class="panel-body">
							<div class="form-horizontal">
								<div class="form-group">
									<div class="row bottomHeight5">
										<label class="col-lg-3 control-label" for="inputError">Name</label>
										<div class="col-lg-9">
		           							<input type="text" v-model="detailItem.name" class="form-control input-init-type">
										</div>
									</div>
									
									<div class="row bottomHeight5">
										<label class="col-lg-3 control-label" for="inputError">Charset</label>
										<div class="col-lg-9">
											<input type="text" v-model="detailItem.charset" class="form-control input-init-type">
										</div>
									</div>
									<div class="row bottomHeight5">
										<label class="col-lg-3 control-label" for="inputError">Desc</label>
										<div class="col-lg-9">
											<input type="text" v-model="detailItem.description" class="form-control input-init-type">
										</div>
									</div>
									<div class="row bottomHeight5">
										<label class="col-lg-3 control-label" for="inputError">Log path
										</label>
										<div class="col-lg-9">
											<input type="text" v-model="detailItem.logPath" class="form-control input-init-type">
											<div>Pattern : <span style="font-weight: normal;">%d{yyyy-MM-dd} , %i</span></div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
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
		,itemObj:{}
		,pageInfo : {}
		,gridData :  []
		,detailItem :{}
		,detailFlag : false 
	}
	,methods:{
		init : function (){
			this.viewItem();
		}
		,search : function(no){
			var _this = this; 
			
			var param = {
				page:no?no:1
				,rows: _this.list_count
				,'searchVal':_this.searchVal
			};
			
			this.$ajax({
				url: {type:VARTOOL.uri.manager, url:'/cmp/logMgmt/list'}
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
					charset : "utf-8"
					,logPath : ""
					,name: ''
					,description: ''
				}
			}else{
				this.detailFlag = true; 
				
				item = VARTOOL.util.objectMerge({}, item);
				
				this.detailItem = item;
				this.detailItem.mode = "modify";
			}
		}
		,saveInfo : function (){
			
			if(!confirm('저장하시겠습니까?')){
				return ; 
			}
		
			var _this = this; 
			
			var param = $.parseJSON(JSON.stringify(this.detailItem));
			
			this.$ajax({
				url: {type:VARTOOL.uri.manager, url:'/cmp/logMgmt/save'}
				,data : param
				,success: function(resData) {
					_this.fieldClear();
					_this.search();
				}
			})
		}
		,copyInfo : function(){
			var _this = this; 
			
			if(!confirm('['+this.detailItem.name+']을 복사 하시겠습니까?')){
				return ; 
			}
			
			var param = VARTOOL.util.objectMerge(this.detailItem);

			this.$ajax({
				url :  {type:VARTOOL.uri.manager, url:'/cmp/logMgmt/copy'}
				,loadSelector : 'body'
				,data:param
				,success:function (resData){
					if(resData.resultCode ==200){
						VARTOOLUI.toast.open(VARTOOL.messageFormat('vartool.0027'));
						_this.search();

						_this.viewItem(resData.item);
						return
					}else{
						alert(resData.messageCode  +'\n'+ resData.message);
					}
				}
			});
		}
		,deleteInfo : function(selectItem){
			var _this = this; 
			
			if(typeof selectItem.cmpId ==='undefined'){
				return ;
			}
			
			if(!confirm('['+selectItem.name+']을 삭제 하시겠습니까?')){
				return ; 
			}
			
			this.$ajax({
				url: {type:VARTOOL.uri.manager, url:'/cmp/logMgmt/remove'}
				,data : {
					cmpId : selectItem.cmpId 
				}
				,success: function(resData) {
					_this.fieldClear();
					_this.search();
				}
			})
		}
	}
});


}());

</script>