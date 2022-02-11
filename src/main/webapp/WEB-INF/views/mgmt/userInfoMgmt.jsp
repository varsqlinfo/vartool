<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/tagLib.jspf"%>
<div class="col-lg-12">
	<h1>
		사용자 관리
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
				<div class="table-responsive">
					<div id="dataTables-example_wrapper"
						class="dataTables_wrapper form-inline" role="grid">
						<table
							class="table table-striped table-bordered table-hover dataTable no-footer"
							id="dataTables-example">
							<thead>
								<tr role="row">
									<th  style="width: 10px;"><div
											class="text-center">
											<input type="checkbox" :checked="selectAllCheck" @click="selectAll()">
										</div>
									</th>
									<th style="width: 195px;">
										사용자명
									</th>
									<th style="width: 150px;">
										ID
									</th>
									<th style="width: 179px;">
										등록일
									</th>
									<th style="width: 50px;">
										
									</th>
									<th style="width: 150px;">
										
									</th>
								</tr>
							</thead>
							<tbody class="dataTableContent">
								<tr v-for="(item,index) in gridData" class="gradeA" :class="(index%2==0?'add':'even')">
									<td><input type="checkbox" :value="item.viewid" v-model="selectItem"></td>
									<td><a href="javascript:;" @click="detailView(item)">{{item.uname}}</a></td>
									<td>{{item.uid}}</td>
									<td class="center">{{item.regDt}}</td>
									<td class="center">{{item.acceptYn?'Y':'N'}}</td>
									<td class="center">
										<!-- 
										<button class="btn btn-xs btn-default" @click="initPassword(item)" >초기화</button>
										<span>{{item.initpw}}</span>
										-->
									</td>
								</tr>
								<tr v-if="gridData.length === 0">
									<td colspan="10"><div class="text-center">데이터가 없습니다.</div></td>
								</tr>
							</tbody>
						</table>

						<page-navigation :page-info="pageInfo" callback="search"></page-navigation>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="col-lg-7">
		<div class="panel panel-default">
			<div class="panel-heading">
				<span>사용자 정보<span style="font-weight:bold;" v-if="detailFlag !== false"> [{{detailItem.uname}}]</span></span>
			</div>
			
			<div class="panel-body">
				<table class="user-detail-info table table-striped table-bordered table-hover dataTable no-footer">
					<colgroup>
						<col style="width:120px">
						<col style="width:*">
					</colgroup>
					<tbody class="dataTableContent">
						<tr>
							<th>사용자명</th><td>{{detailItem.uname}}</td>
						</tr>
						<tr>
							<th>Id</th><td>{{detailItem.uid}}</td>
						</tr>
						<tr>
							<th>Email</th><td>{{detailItem.uemail}}</td>
						</tr>
						<tr>
							<th>조직</th><td>{{detailItem.orgNm}}</td>
						</tr>
						<tr>
							<th>부서</th><td>{{detailItem.deptNm}}</td>
						</tr>
						<tr>
							<th valign="top" style="vertical-align: top;">설명</th><td><textarea rows="3" style="width:100%;overflow:auto;" disabled="disabled">{{detailItem.DESCRIPTION}}</textarea></td>
						</tr>

						<tr>
							<td colspan="2">
								<div class="col-lg-12 padding0" style="padding-right:10px;">
									<div class="tbl-header-fixed-container" style="width: 100%; height: 180px;">
										<div class="tbl-header-fixed-header-bg"></div>
										<div class="tbl-header-fixed-wrapper">
											<table  class="tbl-header-fixed">
												<colgroup>
													<col style="width:65%;">
													<col style="width:35%;">
												</colgroup>
												<thead>
													<tr>
														<th style="width: 100%" colspan="2"><div class="th-text text-center">그룹</div></th>
													</tr>
												</thead>
												<tbody>
													<tr v-for="(item,index) in userGroup">
														<td class="text-left">{{item.groupName}}</td>
														<td >
															<button type="button" @click="removeGroupInfo(item)">제거</button>
														</td>
													</tr>
													<tr v-if="userGroup.length === 0">
														<td colspan="2"><div class="text-center">데이터가 없습니다.</div></td>
													</tr>
												</tbody>
											</table>
										</div>
									</div>
								</div>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
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
		,selectAllCheck : ''
		,selectItem : []
		,userGroup : []
		,pageInfo : {}
		,gridData :  []
		,detailItem :{}
		,detailFlag : false 
		,selectObj : {}
	}
	,methods:{
		init : function (){
			var _this =this; 
		}
		,search : function(no){
			var _this = this; 
			
			var param = {
				page:no?no:1
				,rows: _this.list_count
				,'searchVal':_this.searchVal
			};
			
			this.$ajax({
				url: {type:VARTOOL.uri.manager, url:'/userInfoMgmt/list'}
				,data : param
				,success: function(resData) {
					var items = resData.items;
					
					_this.gridData = resData.items;
					_this.pageInfo = resData.page;
				}
			})
		}
		,detailView : function (item){
			this.detailFlag = true; 
			item = VARTOOL.util.objectMerge({}, item);
			this.detailItem = item;
			this.mappingInfo();
		}
		// db mapping info
		,mappingInfo: function (){
			var _self = this;
			
			if(this.isViewMode ===false) return ; 
			
			var param = {
				viewid : this.detailItem.viewid
			};
			
			this.$ajax({
				data:param
				,loadSelector: '#main-content'
				,url : {type:VARTOOL.uri.manager, url:'/userInfoMgmt/mappingInfo'}
				,success:function (resData){
					var result = resData.items;
		    		_self.userGroup = result; 
				}
			});
		}
		// 맵핑 정보 추가. 
		,removeGroupInfo : function (item){
			var _self = this;
			
			if(!confirm('삭제하시겠습니까?')) return ;  

			var param ={
				viewid : item.viewid
				,groupId : item.groupId
				, mode : 'del'
			};
			
			this.$ajax({
				data:param
				,url : {type:VARTOOL.uri.manager, url:'/userInfoMgmt/modifyMappingInfo'}
				,success:function (response){
					_self.mappingInfo();
				}
			});
		}
	}
});


}());

</script>