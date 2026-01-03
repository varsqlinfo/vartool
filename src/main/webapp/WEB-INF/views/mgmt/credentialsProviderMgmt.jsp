<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/tagLib.jspf"%>
<div class="col-lg-12">
	<h1>
		Credentials Provider
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
										<a href="javascript:;" @click="viewItem(item)"> {{item.credName}} </a>
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
					<form id="addForm" name="addForm" class="form-horizontal" onsubmit="return false;">
					
						<div class="form-group" style="height: 34px;margin-bottom:10px;">
							<div class="col-sm-12">
								<div class="pull-right">
									<button type="button" class="btn btn-default" :class="detailFlag==true?'':'hidden'" @click="fieldClear()"><spring:message code="btn.add" text="추가"/></button>
									<button type="button" class="btn btn-primary" @click="saveInfo()"><spring:message code="btn.save" text="저장"/></button>
									<template v-if="detailFlag===true">
										<button type="button" class="btn btn-default" @click="copyInfo()"><spring:message code="copy" text="복사"/></button>
									</template>
								</div>
							</div>
						</div>
						
						<div class="form-group" :class="errors.has('NAME') ? 'has-error' :''">
							<label class="col-sm-4 control-label"><spring:message code="cred.name" text="자격명"/></label>
							<div class="col-sm-8">
								<input type="text" v-model="detailItem.credName" v-validate="'required'" name="NAME" class="form-control" />
								<div v-if="errors.has('NAME')" class="help-block">{{ errors.first('NAME') }}</div>
							</div>
						</div>
						
						<div class="form-group">
							<label class="col-sm-4 control-label"><spring:message code="cred.type" text="타입"/></label>
							<div class="col-sm-8">
								<select v-model="detailItem.credType" class="form-control input-init-type">
									<c:forEach var="item" items="${credentialsTypeList}" varStatus="status">
										<option value="${item.code}">${item.viewLabel}</option>
									</c:forEach>
								</select>
							</div>
						</div>
						
						<template v-if="detailItem.credType =='secretText'">
							<div class="form-group">
								<label class="col-sm-4 control-label"><spring:message code="cred.secret.text" text="Secret Text" /></label>
								<div class="col-sm-8">
									<textarea rows="5"  v-model="detailItem.secretText" class="form-control input-init-type"></textarea> 
								</div>
							</div>
						</template>
						<template v-if="detailItem.credType =='idPw'">
							<div class="form-group" :class="errors.has('ID') ? 'has-error' :''">
								<label class="col-sm-4 control-label"><spring:message code="cred.username" text="ID"/></label>
								<div class="col-sm-8">
									<input type="text" v-model="detailItem.username" v-validate="'required'" name="ID" class="form-control" />
									<div v-if="errors.has('ID')" class="help-block">{{ errors.first('ID') }}</div>
								</div>
							</div>
							<div class="form-group" :class="errors.has('PASSWORD') ? 'has-error' :''">
								<label class="col-sm-4 control-label"><spring:message code="password" text="비밀번호"/></label>
								<div class="col-sm-8">
									<input type="checkbox" v-model="detailItem.changePassword" v-if="detailFlag">
									
									<template v-if="!detailFlag || detailItem.changePassword">
										<input v-model="detailItem.password" v-validate="'confirmed:password_confirmation'" name="password" type="password" autocomplete="new-password" class="form-control" placeholder="Password" ref="password" data-vv-as="password_confirmation"  style="margin-bottom:5px;">
										<input v-model="detailItem.confirmPassword" v-validate="" name="password_confirmation" type="password" class="form-control" autocomplete="false" placeholder="Password, Again" data-vv-as="password" ref="password_confirmation">
									    <div class="help-block" v-if="errors.has('password')">
									      {{ errors.first('password') }}
									    </div>
									    <div class="help-block" v-if="errors.has('password_confirmation')">
									      {{ errors.first('password_confirmation') }}
									    </div>
						    		</template>
								</div>
							</div>
						</template>
						
						<div class="form-group">
				            <label class="col-sm-4 control-label"><spring:message code="description" text="설명"/></label>
				
				            <div class="col-sm-8">
				                <textarea v-model="detailItem.description" class="form-control" rows="3"></textarea>
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
	,validateCheck : true
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
			var _this = this;
			
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
				url: {type:VARTOOL.uri.manager, url:'/cred/list'}
				,data : param
				,success: function(resData) {
					var items = resData.list;
					
					_this.gridData = resData.list;
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
					credId :'' 
					,credName :'' 
					,credType :'idPw' 
					,changePassword : false
					,username :'' 
					,password :'' 
					,confirmPassword :'' 
					,secretText :'' 
					,description :'' 
				}
			}else{
				this.detailFlag = true; 
				
				item = VARTOOL.util.objectMerge({}, item);
				
				item.changePassword = false; 
				item.confirmPassword = ''; 
				item.password = ''; 
				
				this.detailItem = item;
				this.detailItem.mode = "modify";
			}
		}
		,saveInfo : function (){
			
			if(this.detailItem.password != this.detailItem.confirmPassword){
				VARTOOL.toastMessage('비밀번호가 일치 하지 않습니다.');
				return ; 
			}
				
			
			if(!confirm('저장하시겠습니까?')){
				return ; 
			}
		
			var _this = this; 
			
			var param = $.parseJSON(JSON.stringify(this.detailItem));
			
			this.$ajax({
				url: {type:VARTOOL.uri.manager, url:'/cred/save'}
				,data : param
				,success: function(resData) {
					_this.fieldClear();
					_this.search();
				}
			})
		}
		,copyInfo : function(){
			var _this = this; 
			
			if(!confirm('['+this.detailItem.credName+']을 복사 하시겠습니까?')){
				return ; 
			}
			
			var param = VARTOOL.util.objectMerge(this.detailItem);

			this.$ajax({
				url :  {type:VARTOOL.uri.manager, url:'/cred/copy'}
				,loadSelector : 'body'
				,data:param
				,success:function (resData){
					if(resData.resultCode ==200){
						VARTOOL.toastMessage('vartool.0027');
						_this.search();

						_this.viewItem(resData.item);
						return
					}else{
						VARTOOL.alertMessage(resData.messageCode  +'\n'+ resData.message);
					}
				}
			});
		}
		,deleteInfo : function(selectItem){
			var _this = this; 
			
			if(typeof selectItem.credId ==='undefined'){
				return ;
			}
			
			if(!confirm('['+selectItem.credName+']을 삭제 하시겠습니까?')){
				return ; 
			}
			
			this.$ajax({
				url: {type:VARTOOL.uri.manager, url:'/cred/remove'}
				,data : {
					credId : selectItem.credId 
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
