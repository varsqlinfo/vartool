<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/tagLib.jspf"%>

<div class="col-lg-12">
	<h1>
		Deploy Setting
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
								<th style="width: 80px;">
									name
								</th>
								<th style="width: 180px;">
									path
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
									<div class="text-ellipsis" :title="item.uri">{{item.uri}}</div>
									<div class="text-ellipsis" :title="item.deployPath">{{item.deployPath}}</div>
								</td>
								<td class="center">
									<button type="button" class="btn btn-warning btn_delete btn-xs" @click="deleteInfo(item)">삭제</button>
								</td>
							</tr>
							<tr v-if="gridData.length === 0">
								<td colspan="3"><div class="text-center">데이타 없음</div></td>
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
							<button type="button" class="btn btn-primary" @click="saveInfo()">Save</button>
							<button type="button" class="btn btn-success" :class="detailFlag==true?'':'hidden'" @click="connChk()">check</button>
						</div>
						<br/>
						
						<div class="panel-body">
							<div class="form-horizontal">
								<div class="form-group">
									<div class="row bottomHeight5">
										<label class="col-lg-3 control-label" for="inputError">Name</label>
										<div class="col-lg-9 control-value">
		           							<input type="text" v-model="detailItem.name" class="form-control input-init-type">
										</div>
									</div>
									
									<div class="row bottomHeight5">
										<label class="col-lg-3 control-label" for="inputError">Desc</label>
										<div class="col-lg-9">
											<input type="text" v-model="detailItem.description" class="form-control input-init-type">
										</div>
									</div>
									
									<div class="row bottomHeight5">
										<label class="col-lg-3 control-label" for="inputError">Type</label>
										<div class="col-lg-9 control-value">
											<select v-model="detailItem.scmType" class="form-control input-init-type">
											<c:forEach var="item" items="${scmType}" varStatus="status">
												<option value="${item.viewName}">${item.viewName}</option>
											</c:forEach>
											</select>
										</div>
									</div>
									<div class="row bottomHeight5">
										<label class="col-lg-3 control-label" for="inputError">Uri</label>
										<div class="col-lg-9 control-value">
											<input type="text" v-model="detailItem.scmUrl" class="form-control input-init-type">
										</div>
									</div>
									<div class="row bottomHeight5">
										<label class="col-lg-3 control-label" for="inputError">Id</label>
										<div class="col-lg-9 control-value">
											<input type="text" v-model="detailItem.scmId" class="form-control input-init-type">
										</div>
									</div>
									<div class="row bottomHeight5">
										<label class="col-lg-3 control-label" for="inputError">Pw</label>
										<div class="col-lg-9 control-value">
											<input type="text" v-model="detailItem.scmPw" class="form-control input-init-type">
										</div>
									</div>
									<div class="row bottomHeight5">
										<label class="col-lg-3 control-label" for="inputError">Dependency path</label>
										<div class="col-lg-9 control-value">
											<input type="text" v-model="detailItem.dependencyPath" class="form-control input-init-type">
										</div>
									</div>
									
									<div class="row bottomHeight5">
										<label class="col-lg-3 control-label" for="inputError">배포경로</label>
										<div class="col-lg-9 control-value">
											<input type="text" v-model="detailItem.deployPath" class="form-control input-init-type">
											
											<div style="padding-top:10px;" v-if="detailFlag===true">
												<button type="button" class="btn btn-info btn-sm" @click="removeDeployDir('all')">배포 경로 파일 삭제</button>
												<button type="button" class="btn btn-info  btn-sm" @click="removeDeployDir('class')">classes 경로 삭제(/WEB-INF/classes)</button>
											</div>
										</div>
									</div>
									
									<div class="row bottomHeight5">
										<div>
											<label class="col-lg-3 control-label" for="inputError">빌드 Source</label>
											<div class="col-lg-9 control-value">
												<button type="button" class="btn btn-sm btn-success" :class="detailFlag==true?'':'hidden'" @click="xmlDownload()">다운로드</button>
											</div>
										</div>
										<div class="col-lg-12">
											<div style="position:relative;">
												<b>Ctrl-F</b> : searching ,
      											<b>Ctrl-G</b> : Find next,
      											<b>Shift-Ctrl-G</b> : Find previous
												<textarea id="buildScript" name="buildScript" rows="10" class="form-control input-init-type"></textarea>
											</div>
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

<script>

function completeAfter(cm, pred) {
  var cur = cm.getCursor();
  if (!pred || pred()) setTimeout(function() {
    if (!cm.state.completionActive)
      cm.showHint({completeSingle: false});
  }, 100);
  return CodeMirror.Pass;
}

function completeIfAfterLt(cm) {
  return completeAfter(cm, function() {
    var cur = cm.getCursor();
    return cm.getRange(CodeMirror.Pos(cur.line, cur.ch - 1), cur) == "<";
  });
}

function completeIfInTag(cm) {
  return completeAfter(cm, function() {
    var tok = cm.getTokenAt(cm.getCursor());
    if (tok.type == "string" && (!/['"]/.test(tok.string.charAt(tok.string.length - 1)) || tok.string.length == 1)) return false;
    var inner = CodeMirror.innerMode(cm.getMode(), tok.state).state;
    return inner.tagName;
  });
}

var tags = {
  "!top": ["top"],
  "!attrs": {
    id: null,
    class: ["A", "B", "C"]
  },
  top: {
    attrs: {
      lang: ["en", "de", "fr", "nl"],
      freeform: null
    },
    children: ["animal", "plant"]
  },
  animal: {
    attrs: {
      name: null,
      isduck: ["yes", "no"]
    },
    children: ["wings", "feet", "body", "head", "tail"]
  },
  plant: {
    attrs: {name: null},
    children: ["leaves", "stem", "flowers"]
  },
};
  
VartoolAPP.vueServiceBean({
	el: '#varsqlViewArea'
	,data: {
		list_count :10
		,searchVal : ''
		,pageInfo : {}
		,gridData :  []
		,detailItem :{}
		,detailFlag : false 
		,xmlEditor : false
	}
	,mounted : function (){
		var _this =this; 
		
		this.$nextTick(function (){
			_this.xmlEditor = CodeMirror.fromTextArea(document.getElementById('buildScript'), {
				mode: 'xml',
				indentWithTabs: true,
				smartIndent: true,
				autoCloseBrackets: true,
				indentUnit : 4,
				lineNumbers: true,
				height:500,
				lineWrapping: false,
				matchBrackets : true,
				autofocus: false,
				theme: "night",
				extraKeys: {
					"Ctrl-Space": "autocomplete"
					,"Shift-Ctrl-R" : function (){
						// 검색 재정의
					}
					,"F11": function(cm) {
						var fullScreenFlag = !cm.getOption("fullScreen"); 
						
						if(fullScreenFlag){
							$('.navbar-fixed-top').hide();
						}else{
							$('.navbar-fixed-top').show();
						}
						
						cm.setOption("fullScreen", fullScreenFlag);
					}
					,"Esc": function(cm) {
						$('.navbar-fixed-top').show();
						if (cm.getOption("fullScreen")) cm.setOption("fullScreen", false);
					}
					, "'<'": completeAfter
			        , "'/'": completeIfAfterLt
			        , "' '": completeIfInTag
			        , "'='": completeIfInTag
				}
				,hintOptions: {schemaInfo: tags}
			});
			
			_this.xmlEditor.setSize('100%',350); 
		})
	}
	,methods:{
		init : function (){
			this.fieldClear(); 
		}
		,fieldClear : function (){
			this.viewItem();
		}
		,removeDeployDir : function (mode){
			var param = {
				cmpId : this.detailItem.cmpId
				,mode : mode
			};
			
			if(!confirm('배포 경로 하위의 모든 파일이 삭제 됩니다\n삭제하시겠습니까?\n')) return ; 
			
			this.$ajax({
				url:  {type:VARTOOL.uri.manager, url:'/cmp/deployMgmt/removeDeployDir'}
				,data : param
				,success: function(resData) {
					console.log(resData);
				}
			})
		}
		,viewItem : function (item, typeChangeFlag){
			if(VARTOOL.isUndefined(item)){
				this.detailFlag = false;
				this.viewMode = 'save';
				this.detailItem ={
					cmpId : ''
					,name :''
					,description :''
					,scmType :'GIT'
					,scmUrl :''
					,scmId :''
					,scmPw :''
					,deployPath :''
					,buildScript :''
					,dependencyPath :''
				}
			}else{
				this.detailFlag = true; 
				
				item = VARTOOL.util.objectMerge({}, item);
				
				this.detailItem = item;
				this.detailItem.mode = "modify";
			}
			
			if(this.xmlEditor !== false) {
				this.xmlEditor.setValue(this.detailItem.buildScript||'');
				this.xmlEditor.setHistory({done:[],undone:[]});
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
				url:  {type:VARTOOL.uri.manager, url:'/cmp/deployMgmt/list'}
				,data : param
				,success: function(resData) {
					var items = resData.items;
					
					_this.gridData = resData.items;
					_this.pageInfo = resData.page;
				}
			})
		}
		,saveInfo : function (item){
			var _this = this; 
			
			if(!confirm('저장하시겠습니까?')){
				return ; 
			}
			
			var param = $.parseJSON(JSON.stringify(this.detailItem));
			
			param.buildScript = this.xmlEditor.getValue();
			
			this.$ajax({
				url: {type:VARTOOL.uri.manager, url:'/cmp/deployMgmt/save'} 
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
				url :  {type:VARTOOL.uri.manager, url:'/cmp/deployMgmt/copy'}
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
				url: {type:VARTOOL.uri.manager, url:'/cmp/deployMgmt/remove'} 
				,data : {
					cmpId : selectItem.cmpId
				}
				,success: function(resData) {
					_this.fieldClear();
					_this.search();
				}
			})
		}
		,connChk : function (){
			var _this = this; 
			var param = _this.detailItem;
			
			this.$ajax({
				url: {type:VARTOOL.uri.manager, url:'/cmp/deployMgmt/connChk'} 
				,data : param
				,success: function(resData) {
					VARTOOLUI.toast.open({text : resData.item});
					return ; 
				}
			})
		}
		,xmlDownload : function (){
			var param = {
				fileName : this.detailItem.name+'_build.xml'
				,type :'xml'
				,content : this.xmlEditor.getValue()
			}
						
			VARTOOL.req.download({
				params : param
			});
		}
	}
});


</script>